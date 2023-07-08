package de.paluno.game.server.migration;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class ServerApplication {
    public static void main(String[] args) {
        System.setProperty(
            "vertx.logger-delegate-factory-class-name",
            "io.vertx.core.logging.SLF4JLogDelegateFactory");

        var vertx = Vertx.vertx();
        vertx.deployVerticle("de.paluno.game.server.migration.NettyServer", new DeploymentOptions().setInstances(1));
    }
}
