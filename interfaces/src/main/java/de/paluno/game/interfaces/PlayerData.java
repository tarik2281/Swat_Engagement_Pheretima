package de.paluno.game.interfaces;

public class PlayerData {

    private int clientId;
    private int playerNumber;
    private WormData[] worms;

    public PlayerData() {

    }

    public PlayerData(int clientId, int playerNumber, WormData[] worms) {
        this.clientId = clientId;
        this.playerNumber = playerNumber;
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
}
