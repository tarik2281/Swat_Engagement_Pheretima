package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;

public class User {

    private Connection connection;
    private String name;
    private String[] wormNames;

    private int currentLobbyId;

    public User(Connection connection, String name, String[] wormNames) {
        this.connection = connection;
        this.name = name;
        this.wormNames = wormNames;

        currentLobbyId = Lobby2.ID_NONE;
    }

    public int getId() {
        return connection.getID();
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

    public int getCurrentLobbyId() {
        return currentLobbyId;
    }

    public void setCurrentLobbyId(int currentLobbyId) {
        this.currentLobbyId = currentLobbyId;
    }
}
