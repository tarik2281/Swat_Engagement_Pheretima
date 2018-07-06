package de.paluno.game.interfaces;

public class LobbyCreateRequest {

    public static class Result {
        public int lobbyId;
    }

    private String name;
    private int mapNumber;
    private int numWorms;

    public LobbyCreateRequest() {

    }

    public LobbyCreateRequest(String name, int mapNumber, int numWorms) {
        this.name = name;
        this.mapNumber = mapNumber;
        this.numWorms = numWorms;
    }

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
