package de.paluno.game.interfaces;

public class GameSetupData {

    public int mapNumber;
    private int[] clientIds;
    private PlayerData[] playerData;

    public GameSetupData() {

    }

    public GameSetupData(int[] clientIds, PlayerData[] playerData) {
        this.clientIds = clientIds;
        this.playerData = playerData;
    }

    public int[] getClientIds() {
        return clientIds;
    }

    public PlayerData[] getPlayerData() {
        return playerData;
    }
}
