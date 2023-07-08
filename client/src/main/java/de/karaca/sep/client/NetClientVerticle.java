package de.karaca.sep.client;

import de.paluno.game.interfaces.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class NetClientVerticle extends AbstractVerticle {

    private final KryoNetSerializer kryoNetSerializer = new KryoNetSerializer();

    private UUID sessionId;
    private NetSocket tcpSocket;
    private DatagramSocket udpSocket;

    @Override
    public void start() {
        udpSocket = vertx.createDatagramSocket()
            .handler(packet -> {
                var parsedObject = kryoNetSerializer.readObject(packet.data());

                if (parsedObject instanceof PingMessage pingMessage) {
                    log.info("Received ping from {}", packet.sender());

                    tcpSocket.write(kryoNetSerializer.writeObject(new UserLoginRequest("testUser", new String[]{"testUser"}, false)));
                }
            });

        vertx.createNetClient()
            .connect(8081, "localhost")
            .onSuccess(socket -> {
                log.info("Connected!");

                tcpSocket = socket;

                socket.handler(buffer -> {
                    var parsedObject = kryoNetSerializer.readObject(buffer);

                    if (parsedObject instanceof RegisterTcpClient registerTcpClient) {
                        sessionId = UUID.fromString(registerTcpClient.getSessionId());

                        log.info("Received session id: {}", sessionId);

                        var objectBuffer = kryoNetSerializer.writeObject(new RegisterUdpSocket(sessionId.toString()));

                        udpSocket.send(objectBuffer, 8082, "localhost", res -> {
                            if (res.succeeded()) {
                                log.info("Sent UDP socket registration");
                            } else {
                                log.error("Failed to send UDP socket registration", res.cause());
                            }
                        });
                    }
                });

                socket.closeHandler(v -> {
                    log.info("Connection closed");

                    sessionId = null;
                    tcpSocket = null;

                    log.info("Shutting down...");
                    vertx.close();
                });
            })
            .onFailure(err -> {
                log.error("Failed to connect", err);
                vertx.close();
            });
    }
}
