package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;

import java.util.ArrayList;

public class Player {

    public Connection connection;

    public boolean ready;
    public int currentWormIndex;
    public ArrayList<Worm> worms = new ArrayList<>();
}
