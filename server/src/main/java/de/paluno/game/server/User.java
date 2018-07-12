package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;
import de.paluno.game.interfaces.UserName;

public class User {

    private Connection connection;
    private UserName userName;

    private int currentLobbyId;

    public User(Connection connection, String name, String[] wormNames) {
        this.connection = connection;
        this.userName = new UserName(name, wormNames);

        currentLobbyId = Lobby.ID_NONE;
    }

    public int getId() {
        return connection.getID();
    }

    public Connection getConnection() {
        return connection;
    }

    public UserName getUserName() {
        return userName;
    }

    public String getName() {
        return userName.getUserName();
    }

    public String[] getWormNames() {
        return userName.getWormNames();
    }

    public int getCurrentLobbyId() {
        return currentLobbyId;
    }

    public void setCurrentLobbyId(int currentLobbyId) {
        this.currentLobbyId = currentLobbyId;
    }
}
