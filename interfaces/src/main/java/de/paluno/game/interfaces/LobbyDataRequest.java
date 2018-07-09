package de.paluno.game.interfaces;

public class LobbyDataRequest {

    public static class Result {
        public LobbyData lobbyData;
        public String[] users;
    }

    public int lobbyId;
}
