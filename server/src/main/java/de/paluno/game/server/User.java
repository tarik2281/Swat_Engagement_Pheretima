package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;
import de.paluno.game.interfaces.UserName;

public class User {

    private Connection connection;
    private UserName userName;
    private boolean udpEnabled;

    private int currentLobbyId;

    public User(Connection connection, String name, String[] wormNames, boolean udpEnabled) {
        this.connection = connection;
        this.userName = new UserName(name, wormNames);
        this.udpEnabled = udpEnabled;

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

    public boolean isUdpEnabled() {
        return udpEnabled;
    }

    public int getCurrentLobbyId() {
        return currentLobbyId;
    }

    public void setCurrentLobbyId(int currentLobbyId) {
        this.currentLobbyId = currentLobbyId;
    }

    public void send(Object object, boolean preferUdp) {
        if (preferUdp && udpEnabled)
            connection.sendUDP(object);
        else
            connection.sendTCP(object);
    }
}
