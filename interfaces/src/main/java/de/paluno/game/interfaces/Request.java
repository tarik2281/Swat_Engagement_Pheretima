package de.paluno.game.interfaces;

public class Request {

    public enum Type {
        LobbyCreate, LobbyJoin, LobbyLeave, LobbyList
    }

    private Type type;

    public static Request createLobby(String name, int mapNumber, int numWorms) {
        return null;
    }
}
