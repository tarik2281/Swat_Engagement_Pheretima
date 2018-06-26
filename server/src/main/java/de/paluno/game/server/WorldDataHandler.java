package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;
import de.paluno.game.interfaces.WorldData;

public class WorldDataHandler extends DataHandler<WorldData> {

    private GameServer gameServer;

    public WorldDataHandler initialize(GameServer server) {
        this.gameServer = server;

        return this;
    }

    @Override
    public void handle(Connection connection, WorldData data) {
        // broadcast to other clients
        for (Connection c : gameServer.connections) {
            if (c.getID() != connection.getID())
                c.sendUDP(data);
        }
    }
}
