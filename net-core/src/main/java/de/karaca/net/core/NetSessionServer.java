package de.karaca.net.core;

import de.karaca.net.core.vertx.NetSessionServerVertx;

public interface NetSessionServer {
    static NetSessionServer create(NetSystem netSystem) {
        return new NetSessionServerVertx(netSystem);
    }
}
