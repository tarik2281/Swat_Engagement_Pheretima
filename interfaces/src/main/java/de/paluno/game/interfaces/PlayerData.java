package de.paluno.game.interfaces;

public class PlayerData {

    private int clientId;
    private int playerNumber;
    private WormData[] worms;
    private UserName userName;

    public PlayerData() {

    }

    public PlayerData(int clientId, int playerNumber, WormData[] worms, UserName userName) {
        this.clientId = clientId;
        this.playerNumber = playerNumber;
        this.userName = userName;
        this.worms = worms;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public WormData[] getWorms() {
        return worms;
    }

    public WormData getWormByNumber(int number) {
        return worms[number];
    }

    public int getClientId() {
        return clientId;
    }

    public UserName getUserName() {
        return userName;
    }
}
