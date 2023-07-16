package de.paluno.game.server;

import de.karaca.net.core.NetMessage;
import de.karaca.net.core.NetSession;
import de.karaca.net.core.NetSessionServer;
import de.paluno.game.interfaces.UserName;

public class User {

    private NetSession netSession;
    private NetSessionServer netSessionServer;
    private UserName userName;
    private boolean udpEnabled;

    private int currentLobbyId;

    public User(NetSession netSession, NetSessionServer netSessionServer, String name, String[] wormNames, boolean udpEnabled) {
        this.netSession = netSession;
        this.netSessionServer = netSessionServer;
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
        this.netSessionServer.send(netSession, NetMessage.from(object));

//        if (preferUdp && udpEnabled)
//            netSession.sendUDP(object);
//        else
//            netSession.sendTCP(object);
    }
}
