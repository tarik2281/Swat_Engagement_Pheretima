package de.paluno.game.interfaces;

public class LobbyData {

    public int id;
    public String name;
    public int mapNumber;
    public int numWorms;
    public int creatingUserId;

    @Override
    public String toString() {
        return name;
    }
}
