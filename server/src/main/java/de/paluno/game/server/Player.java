package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;
import de.paluno.game.interfaces.PlayerData;
import de.paluno.game.interfaces.WormData;

import java.util.ArrayList;

public class Player {

    private Connection connection;

    private int number;
    private boolean ready;
    private int currentWormIndex;
    private ArrayList<Worm> worms;
    private int numWormsAlive;

    public Player(Connection connection, int number) {
        this.connection = connection;

        this.number = number;
    }

    public void setupFromData(PlayerData data) {
        currentWormIndex = 0;
        numWormsAlive = 0;

        worms = new ArrayList<>(data.getWorms().length);
        for (WormData wormData : data.getWorms()) {
            worms.add(new Worm(wormData));
            numWormsAlive++;
        }
    }

    public void shiftTurn() {
        if (numWormsAlive == 0)
            return;

        do {
            currentWormIndex = (currentWormIndex + 1) % worms.size();
        } while (worms.get(currentWormIndex).isDead());
    }

    public void wormDied(int wormNumber) {
        worms.get(wormNumber).setDead(true);
        numWormsAlive--;
    }

    public Worm getCurrentWorm() {
        return worms.get(currentWormIndex);
    }

    public Worm getWormByNumber(int wormNumber) {
        return worms.get(wormNumber);
    }

    public Connection getConnection() {
        return connection;
    }

    public int getNumber() {
        return number;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isDefeated() {
        return numWormsAlive <= 0;
    }
}
