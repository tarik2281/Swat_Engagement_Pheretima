package de.paluno.game.interfaces;

public class LobbyJoinRequest {

    public static class Result {
        public boolean success;
        public int lobbyId;
    }

    public int lobbyId;
}
