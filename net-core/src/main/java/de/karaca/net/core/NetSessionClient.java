package de.karaca.net.core;

import de.karaca.net.core.vertx.NetSessionClientVertx;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public interface NetSessionClient {
    NetSession getNetSession();

    NetSessionClient send(NetMessage message);
    NetSessionClient onConnect(Runnable handler);
    NetSessionClient onDisconnect(Runnable handler);
    NetSessionClient onError(Consumer<Throwable> handler);
    NetSessionClient onReceive(Consumer<NetMessage> handler);

    CompletionStage<NetSessionClient> connect(NetConnectionConfig config);
    void disconnect();

    void close();

    static NetSessionClient create(NetSystem netSystem) {
        return new NetSessionClientVertx(netSystem);
    }
}
