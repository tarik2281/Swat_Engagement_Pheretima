package de.karaca.net.core.vertx;

import de.karaca.net.core.*;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.SocketAddress;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static de.karaca.net.core.vertx.NetSystemVertx.*;

@Slf4j
public class NetSessionServerVertx implements NetSessionServer {

    private final NetSystemVertx netSystem;

    private NetServer netServer;
    private DatagramSocket datagramSocket;

    private final NetDataHandlerVertx messageHandler = new NetDataHandlerVertx();

    private final Map<UUID, NetSessionVertx> sessionMap = new HashMap<>();
    // TODO: is this a good idea?
    private final Map<SocketAddress, NetSessionVertx> udpSessionMap = new HashMap<>();

    public NetSessionServerVertx(NetSystem netSystem) {
        if (netSystem instanceof NetSystemVertx netSystemVertx) {
            this.netSystem = netSystemVertx;
        } else {
            throw new IllegalArgumentException("NetSystem must be of type NetSystemVertx");
        }
    }

    public void start() {
        var vertx = netSystem.getVertx();

        vertx.createNetServer()
            .connectHandler(socket -> {
                final var sessionId = UUID.randomUUID();
                final var netSession = new NetSessionVertx(sessionId)
                    .setTcpSocket(socket)
                    .setUdpSocket(datagramSocket);

                sessionMap.put(sessionId, netSession);

                messageHandler.send(netSession, NetMessage.builder(ASSIGN_SESSION_ID)
                    .payload(sessionId.toString())
                    .build());

                socket.handler(buffer -> {
                    messageHandler.handleNetData(netSession, buffer, netMessage -> {

                    });
                });

                socket.closeHandler(v -> {
                    sessionMap.remove(sessionId);

                    var remoteUdpAddress = netSession.getRemoteUdpAddress();
                    if (remoteUdpAddress != null) {
                        udpSessionMap.remove(remoteUdpAddress);
                    }
                });
            })
            .listen(8081)
            .onSuccess(server -> {
                netServer = server;
                log.info("NetServer started on port {}", server.actualPort());
            })
            .onFailure(throwable -> log.error("Failed to start NetServer", throwable));

        vertx.createDatagramSocket()
            .handler(packet -> {
                final var remoteAddress = packet.sender();
                final var netSession = udpSessionMap.get(remoteAddress);

                messageHandler.handleDatagramPacket(packet, netMessage -> {
                    if (netSession == null) {
                        if (netMessage.isOfType(ASSIGN_UDP_ADDRESS)) {
                            final var sessionId = UUID.fromString((String) netMessage.getPayload());
                            final var session = sessionMap.get(sessionId);

                            if (session != null) {
                                session.setRemoteUdpAddress(remoteAddress);
                                udpSessionMap.put(remoteAddress, session);
                                messageHandler.send(session, NetMessage.builder(CONFIRM_UDP_ADDRESS).build());
                            } else {
                                log.warn("Received UDP address assignment for unknown session {}", sessionId);
                            }
                        } else {
                            log.warn("Received UDP message from unknown address {}", remoteAddress);
                        }
                    } else {
                        // handle message
                    }
                });
            })
            .listen(8082, "localhost")
            .onSuccess(socket -> {
                datagramSocket = socket;
                log.info("DatagramSocket started on port {}", socket.localAddress().port());
            })
            .onFailure(throwable -> log.error("Failed to start DatagramSocket", throwable));
    }
}
