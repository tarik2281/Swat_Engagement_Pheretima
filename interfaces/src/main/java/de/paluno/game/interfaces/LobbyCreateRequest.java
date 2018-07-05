package de.paluno.game.interfaces;

public class LobbyCreateRequest {

    public static class Result {
        public int lobbyId;
    }

    private String name;
    private int mapNumber;
    private int numWorms;

    public String getName() {
        return name;
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public int getNumWorms() {
        return numWorms;
    }
}
