package de.paluno.game.interfaces;

public class GameSetupRequest {

    private int[] clientIds;
    private int[] playerNumbers;

    public GameSetupRequest() {

    }

    public GameSetupRequest(int[] clientIds, int[] playerNumbers) {
        this.clientIds = clientIds;
        this.playerNumbers = playerNumbers;
    }

    public int[] getClientIds() {
        return clientIds;
    }

    public int[] getPlayerNumbers() {
        return playerNumbers;
    }
}
