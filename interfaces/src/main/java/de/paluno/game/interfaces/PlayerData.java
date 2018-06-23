package de.paluno.game.interfaces;

public class PlayerData {

    private int playerNumber;
    private WormData[] worms;

    public PlayerData() {

    }

    public PlayerData(int playerNumber, WormData[] worms) {
        this.playerNumber = playerNumber;
        this.worms = worms;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public WormData[] getWorms() {
        return worms;
    }

}
