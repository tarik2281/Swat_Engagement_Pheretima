package de.paluno.game.server.migration;

import de.paluno.game.interfaces.KryoNetSerializer;
import de.paluno.game.interfaces.PingMessage;
import de.paluno.game.interfaces.RegisterTcpClient;
import de.paluno.game.interfaces.RegisterUdpSocket;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
public class NettyServer extends AbstractVerticle {

    private final HashMap<UUID, NetSession> sessionMap = new HashMap<>();
    private final HashMap<SocketAddress, NetSession> udpSessionMap =
        new HashMap<>();

    private final KryoNetSerializer kryoNetSerializer = new KryoNetSerializer();

    private DatagramSocket udpSocket;

    private final NetListener listener = new GameServerHandler();

    public KryoNetSerializer getKryoNetSerializer() {
        return kryoNetSerializer;
    }

    public void sendUdp(Object object, SocketAddress address) {
        var buffer = kryoNetSerializer.writeObject(object);

        udpSocket.send(buffer, address.port(), address.host());
    }

    @Override
    public void start() throws Exception {
        log.info("Starting server...");

        var yamlStore =
            new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject().put("path", "config.yaml"));

        var sysPropsStore = new ConfigStoreOptions().setType("sys");
        var envStore = new ConfigStoreOptions().setType("env");

        ConfigRetriever configRetriever =
            ConfigRetriever.create(
                vertx,
                new ConfigRetrieverOptions()
                    .addStore(yamlStore)
                    .addStore(sysPropsStore)
                    .addStore(envStore));

        configRetriever.getConfig().map(JsonHelper::flatten).onSuccess(this::startServer);
    }

    private void startServer(JsonObject config) {
        var serverPort = config.getInteger("server.port", 8080);

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route()
            .handler(LoggerHandler.create(LoggerFormat.TINY))
            .handler(ResponseContentTypeHandler.create());

        router.get("/").produces("text/plain").handler(ctx -> ctx.response().end("Hello World!"));

        server.requestHandler(router).listen(serverPort);
        log.info("HTTP Server started on port {}", serverPort);

        vertx.createDatagramSocket()
            .handler(packet -> {
//                log.debug("Received {} bytes from UDP {}", packet.data().length(), packet.sender());

                final var senderAddress = packet.sender();
                final var netSession = udpSessionMap.get(senderAddress);

                kryoNetSerializer.readBuffer(packet.data());
                final var parsedObject = kryoNetSerializer.readNextObject();

                if (netSession == null) {
                    if (parsedObject instanceof RegisterUdpSocket registerUdpSocket) {
                        final var sessionId = UUID.fromString(registerUdpSocket.getSessionId());

                        final var session = sessionMap.get(sessionId);

                        if (session != null) {
                            session.setUdpAddress(senderAddress);
                            udpSessionMap.put(senderAddress, session);
                        }

                        log.debug("Assigned session id {} to UDP address {}", sessionId, senderAddress);

                        var buffer = kryoNetSerializer.writeObject(new PingMessage()
                            .setSessionId(sessionId.toString())
                            .setTimestamp(System.currentTimeMillis()));

                        udpSocket.send(buffer, senderAddress.port(), senderAddress.host());

                        listener.connected(session);
                    }
                } else {
                    listener.received(netSession, parsedObject);
                }
            })
            .listen(8082, "localhost")
            .onSuccess(datagramSocket -> {
                log.info("UDP server started on port 8082");
                udpSocket = datagramSocket;
            })
            .onFailure(throwable -> {
                log.error("Failed to start UDP server", throwable);
                vertx.close();
            });

        vertx.createNetServer()
            .connectHandler(socket -> {
                final var sessionId = UUID.randomUUID();

                final var netSession = new NetSession()
                    .setServer(this)
                    .setSessionId(sessionId)
                    .setTcpSocket(socket);

                sessionMap.put(sessionId, netSession);

                log.debug("Assigned session id {} to TCP address {}", sessionId, socket.remoteAddress());

                socket.write(kryoNetSerializer.writeObject(new RegisterTcpClient(sessionId.toString())));

                socket.handler(buffer -> {
                    log.info("Received {} bytes from session id {}", buffer.length(), sessionId);

                    kryoNetSerializer.readBuffer(buffer);

                    do {
                        var parsedObject = kryoNetSerializer.readNextObject();
                        log.info("Received object {}", parsedObject);
                        listener.received(netSession, parsedObject);
                    } while (kryoNetSerializer.hasData());
                });

                socket.closeHandler(v -> {
                    if (netSession.getUdpAddress() != null) {
                        udpSessionMap.remove(netSession.getUdpAddress());
                    }

                    sessionMap.remove(sessionId);

                    log.info("Connection closed from {}", socket.remoteAddress());

                    listener.disconnected(netSession);
                });
            })
            .listen(8081)
            .onSuccess(netServer -> log.info("TCP server started on port 8081"))
            .onFailure(throwable -> {
                log.error("Failed to start TCP server", throwable);
                vertx.close();
            });
    }
}
