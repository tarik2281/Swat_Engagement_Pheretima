package de.paluno.game.server;

import de.paluno.game.interfaces.Constants;

import java.util.ArrayList;
import java.util.List;

public class Lobby2 {

    private int id;
    private String name;
    private List<User> users;
    private byte mapNumber;
    private byte numWorms;

    public Lobby2(int id, String name, int mapNumber, int numWorms) {
        this.id = id;
        this.name = name;
        this.mapNumber = (byte)mapNumber;
        this.numWorms = (byte)numWorms;

        users = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public boolean addUser(User user) {
        if (users.size() < Constants.NUM_MAX_PLAYERS) {
            users.add(user);
            return true;
        }

        return false;
    }

    public void removeUser(User user) {
        users.remove(user);
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
