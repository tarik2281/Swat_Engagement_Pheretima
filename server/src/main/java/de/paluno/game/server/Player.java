package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;
import de.paluno.game.interfaces.PlayerData;
import de.paluno.game.interfaces.WormData;

import java.util.ArrayList;

public class Player {

    private User controllingUser;

    private Runnable defeatedListener;
    private int number;
    private boolean ready;
    private int currentWormIndex;
    private ArrayList<Worm> worms;
    private int numWormsAlive;

    public Player(User user, int number) {
        this.controllingUser = user;

        this.number = number;
    }

    public void setDefeatedListener(Runnable listener) {
        this.defeatedListener = listener;
    }

    public Worm addWorm(int wormNumber) {
        Worm worm = new Worm(number, wormNumber);
        worm.setDeathListener(() -> {
            if (--numWormsAlive == 0 && defeatedListener != null)
                defeatedListener.run();
        });
        numWormsAlive++;
        worms.add(worm);
        return worm;
    }

    public void setupFromData(PlayerData data) {
        currentWormIndex = 0;
        numWormsAlive = 0;

        worms = new ArrayList<>(data.getWorms().length);
        for (WormData wormData : data.getWorms()) {
            Worm worm = new Worm(wormData.getPlayerNumber(), wormData.getWormNumber());

            worm.setDeathListener(() ->  {
                if (--numWormsAlive == 0 && defeatedListener != null)
                    defeatedListener.run();
            });

            worms.add(worm);

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

    public Worm getCurrentWorm() {
        return worms.get(currentWormIndex);
    }

    public Worm getWormByNumber(int wormNumber) {
        return worms.get(wormNumber);
    }

    public Connection getConnection() {
        return null;
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
