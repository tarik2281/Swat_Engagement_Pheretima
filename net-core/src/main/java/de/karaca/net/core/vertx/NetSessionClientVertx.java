package de.karaca.net.core.vertx;

import de.karaca.net.core.*;
import io.vertx.core.Context;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.SocketAddress;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import static de.karaca.net.core.vertx.NetSystemVertx.*;

@Slf4j
public class NetSessionClientVertx implements NetSessionClient {

    private NetSessionVertx netSession;

    private Runnable connectHandler;
    private Runnable disconnectHandler;
    private Consumer<Throwable> errorHandler;
    private NetMessageRouter messageRouter;

    private final NetSystemVertx netSystem;
    private NetClient netClient;
    private DatagramSocket datagramSocket;

    private final NetDataHandlerVertx dataHandler = new NetDataHandlerVertx();

    private CompletableFuture<NetSessionClient> connectFuture;

    public NetSessionClientVertx(NetSystem netSystem) {
        if (netSystem instanceof NetSystemVertx netSystemVertx) {
            this.netSystem = netSystemVertx;
        } else {
            throw new IllegalArgumentException("NetSystem must be of type NetSystemVertx");
        }

        messageRouter = new NetMessageRouter()
            .route(ASSIGN_SESSION_ID, (session, message) -> {
                final var sessionId = message.getPayload();
                netSession.setSessionId(UUID.fromString(sessionId));

                dataHandler.send(netSession, NetMessage.builder(ASSIGN_UDP_ADDRESS)
                    .payload(sessionId)
                    .build());
                // TODO: repeat this until we get a response
            })
            .route(CONFIRM_UDP_ADDRESS, (session, message) -> connectFuture.complete(this));
    }

    @Override
    public NetSession getNetSession() {
        return netSession;
    }

    @Override
    public NetSessionClient send(NetMessage<Object> message) {
        // TODO: check if this is correct and the message is sent on the event loop thread
        if (!Context.isOnEventLoopThread()) {
            netSystem.getVertx().runOnContext(v -> dataHandler.send(netSession, message));
        } else {
            dataHandler.send(netSession, message);
        }

        return this;
    }

    @Override
    public CompletionStage<NetSessionClient> connect(NetConnectionConfig config) {
        if (connectFuture == null) {
            connectFuture = new CompletableFuture<>();

            if (netClient == null) {
                netClient = netSystem.getVertx().createNetClient();
                datagramSocket = netSystem.getVertx().createDatagramSocket()
                    .handler(packet -> {
                        dataHandler.handleDatagramPacket(packet, message -> messageRouter.accept(netSession, message));
                    });
            }

            netClient.connect(config.getTcpPort(), config.getHost())
                .onSuccess(netSocket -> {
                    netSession = new NetSessionVertx();
                    netSession.setTcpSocket(netSocket);
                    netSession.setUdpSocket(datagramSocket);
                    netSession.setRemoteUdpAddress(SocketAddress.inetSocketAddress(config.getUdpPort(), config.getHost()));

                    netSocket.handler(buffer -> dataHandler.handleNetData(netSession, buffer, messageRouter));

                    netSocket.closeHandler(v -> {
                        if (disconnectHandler != null) {
                            disconnectHandler.run();
                        }
                    });
                })
                .onFailure(ex -> connectFuture.completeExceptionally(ex));
        }

        return connectFuture;
    }

    @Override
    public void disconnect() {
        if (netSession != null) {
            netSession.getTcpSocket().close();
            netSession = null;
        }
    }

    @Override
    public void close() {
        if (netClient != null) {
            datagramSocket.close();
            netClient.close();

            datagramSocket = null;
            netClient = null;
        }
    }

    @Override
    public NetSessionClient onConnect(Runnable handler) {
        this.connectHandler = handler;
        return this;
    }

    @Override
    public NetSessionClient onDisconnect(Runnable handler) {
        this.disconnectHandler = handler;
        return this;
    }

    @Override
    public NetSessionClient onError(Consumer<Throwable> handler) {
        this.errorHandler = handler;
        return this;
    }

    @Override
    public NetSessionClient onReceive(NetMessageConsumer<Object> handler) {
        messageRouter.chain(handler);
        return this;
    }
}
