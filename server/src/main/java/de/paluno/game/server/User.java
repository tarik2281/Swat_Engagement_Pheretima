package de.paluno.game.server;

import de.paluno.game.interfaces.UserName;
import de.paluno.game.server.migration.NetSession;

public class User {

    private NetSession netSession;
    private UserName userName;
    private boolean udpEnabled;

    private int currentLobbyId;

    public User(NetSession netSession, String name, String[] wormNames, boolean udpEnabled) {
        this.netSession = netSession;
        this.userName = new UserName(name, wormNames);
        this.udpEnabled = udpEnabled;

        currentLobbyId = Lobby.ID_NONE;
    }

    public int getId() {
        return netSession.getSessionId().hashCode();
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
            netSession.sendUDP(object);
        else
            netSession.sendTCP(object);
    }
}
