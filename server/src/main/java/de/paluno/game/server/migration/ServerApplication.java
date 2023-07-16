package de.paluno.game.server.migration;

import de.karaca.net.core.NetMessageType;
import de.paluno.game.server.net.GameServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class ServerApplication {
    public static void main(String[] args) {
        System.setProperty(
            "vertx.logger-delegate-factory-class-name",
            "io.vertx.core.logging.SLF4JLogDelegateFactory");

        NetMessageType.scan("de.paluno.game.interfaces");

        var vertx = Vertx.vertx();
//        vertx.deployVerticle("de.paluno.game.server.migration.NettyServer", new DeploymentOptions().setInstances(1));
        vertx.deployVerticle(new GameServerVerticle());
    }
}
