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

    private final NetListener netListener;

    public NetClientVerticle() {
        this.netListener = new NetListener() {
            @Override
            public void connected() {
                log.info("Connected!");
            }

            @Override
            public void disconnected() {
                log.info("Disconnected!");
            }

            @Override
            public void connectionFailed() {
                log.info("Connection failed!");
            }

            @Override
            public void received(Object object) {
                log.info("Received: {}", object);
            }
        };
    }

    public NetClientVerticle(NetListener netListener) {
        this.netListener = netListener;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void sendTcp(Object object) {
        context.runOnContext(v -> {
            var dataId = (int) (Math.random() * 1000);
            log.info("[{}] Sending over TCP: {}", dataId, object);
            tcpSocket.write(kryoNetSerializer.writeObject(object)).onComplete(res -> {
                if (res.succeeded()) {
                    log.info("[{}] Sent!", dataId);
                } else {
                    log.error("[{}] Failed to send", dataId, res.cause());
                }
            });

        });
//        var dataId = (int) (Math.random() * 1000);
//        log.info("[{}] Sending over TCP: {}", dataId, object);
//        tcpSocket.write(kryoNetSerializer.writeObject(object)).onComplete(res -> {
//            if (res.succeeded()) {
//                log.info("[{}] Sent!", dataId);
//            } else {
//                log.error("[{}] Failed to send", dataId, res.cause());
//            }
//        });
    }

    public void sendUdp(Object object) {
        context.runOnContext(v -> {
            var dataId = (int) (Math.random() * 1000);
//            log.info("[{}] Sending over UDP: {}", dataId, object);
            udpSocket.send(kryoNetSerializer.writeObject(object), 8082, "localhost").onComplete(res -> {
                if (res.succeeded()) {
//                    log.info("[{}] Sent!", dataId);
                } else {
//                    log.error("[{}] Failed to send", dataId, res.cause());
                }
            });
        });
//        var dataId = (int) (Math.random() * 1000);
//        log.info("[{}] Sending over UDP: {}", dataId, object);
//        udpSocket.send(kryoNetSerializer.writeObject(object), 8082, "localhost").onComplete(res -> {
//            if (res.succeeded()) {
//                log.info("[{}] Sent!", dataId);
//            } else {
//                log.error("[{}] Failed to send", dataId, res.cause());
//            }
//        });
    }

    @Override
    public void start() {
        udpSocket = vertx.createDatagramSocket()
            .handler(packet -> {
                kryoNetSerializer.readBuffer(packet.data());
                var parsedObject = kryoNetSerializer.readNextObject();

                if (parsedObject instanceof PingMessage pingMessage) {
                    log.info("Received ping from {}", packet.sender());

                    netListener.connected();
                } else {
                    netListener.received(parsedObject);
                }
            });

        vertx.createNetClient()
            .connect(8081, "localhost")
            .onSuccess(socket -> {
                log.info("Connected!");

                tcpSocket = socket;

                socket.handler(buffer -> {
                    kryoNetSerializer.readBuffer(buffer);
                    do {
                        var parsedObject = kryoNetSerializer.readNextObject();

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
                        } else {
                            netListener.received(parsedObject);
                        }
                    } while (kryoNetSerializer.hasData());
                });

                socket.closeHandler(v -> {
                    log.info("Connection closed");

                    netListener.disconnected();

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
