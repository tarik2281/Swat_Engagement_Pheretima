package de.paluno.game.interfaces;

public class GameSetupData {

    public int mapNumber;
    private PlayerData[] playerData;

    public GameSetupData() {

    }

    public GameSetupData(PlayerData[] playerData) {
        this.playerData = playerData;
    }

    public PlayerData[] getPlayerData() {
        return playerData;
    }
}
