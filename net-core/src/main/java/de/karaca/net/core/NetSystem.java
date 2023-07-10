package de.karaca.net.core;

import de.karaca.net.core.vertx.NetSystemVertx;

public abstract class NetSystem {
    public abstract void close();

    public static NetSystem create() {
        return NetSystemVertx.create();
    }
}
