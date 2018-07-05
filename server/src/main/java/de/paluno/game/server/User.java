package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;

public class User {

    private Connection connection;
    private String name;
    private String[] wormNames;

    public User(Connection connection, String name, String[] wormNames) {
        this.connection = connection;
        this.name = name;
        this.wormNames = wormNames;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getName() {
        return name;
    }

    public String[] getWormNames() {
        return wormNames;
    }
}
