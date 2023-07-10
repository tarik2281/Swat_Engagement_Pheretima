package de.karaca.net.core.vertx;

import de.karaca.net.core.NetMessageType;
import de.karaca.net.core.NetProtocol;
import de.karaca.net.core.NetSystem;
import io.vertx.core.Vertx;
import lombok.Getter;

@Getter
public class NetSystemVertx extends NetSystem {

    public static final NetMessageType<String> ASSIGN_SESSION_ID = NetMessageType.create("PING", String.class, NetProtocol.TCP);
    public static final NetMessageType<String> ASSIGN_UDP_ADDRESS = NetMessageType.create("ASSIGN_UDP_ADDRESS", String.class, NetProtocol.UDP);
    public static final NetMessageType<Void> CONFIRM_UDP_ADDRESS = NetMessageType.create("CONFIRM_UDP_ADDRESS", Void.class, NetProtocol.TCP);

    private final Vertx vertx;

    public NetSystemVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    public static NetSystem create() {
        return new NetSystemVertx(Vertx.vertx());
    }

    public static NetSystem from(Vertx vertx) {
        return new NetSystemVertx(vertx);
    }

    @Override
    public void close() {
        vertx.close();
    }
}
