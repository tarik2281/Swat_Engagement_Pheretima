package de.karaca.net.core;

import de.karaca.net.core.vertx.NetSessionServerVertx;

import java.util.function.Consumer;

public interface NetSessionServer {
    static NetSessionServer create(NetSystem netSystem) {
        return new NetSessionServerVertx(netSystem);
    }

    NetSessionServer onConnect(Consumer<NetSession> handler);
    NetSessionServer onDisconnect(Consumer<NetSession> handler);
    NetSessionServer onError(Consumer<Throwable> handler);
    NetSessionServer onReceive(NetMessageConsumer<Object> handler);

    NetSessionServer start();

    <T> void send(NetSession session, NetMessage<T> message);
}
