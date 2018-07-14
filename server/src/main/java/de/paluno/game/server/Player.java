package de.paluno.game.server;

import de.paluno.game.interfaces.PlayerData;
import de.paluno.game.interfaces.WormData;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private User controllingUser;

    private Runnable defeatedListener;
    private int number;
    private boolean ready;
    private int currentWormIndex;
    private ArrayList<Worm> worms;
    private int numWormsAlive;
    private boolean roundEnded;
    private boolean disconnected;

    public Player(User user, int number) {
        this.controllingUser = user;

        this.number = number;
        currentWormIndex = -1;
        roundEnded = false;
        disconnected = false;

        worms = new ArrayList<>();
    }

    public void setDefeatedListener(Runnable listener) {
        this.defeatedListener = listener;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    public boolean isDisconnected() {
        return disconnected;
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

    public List<Worm> getWorms() {
        return worms;
    }

    public void shiftTurn() {
        if (numWormsAlive == 0)
            return;

        do {
            currentWormIndex = (currentWormIndex + 1) % worms.size();
            if (currentWormIndex == worms.size() - 1)
                roundEnded = true;
        } while (worms.get(currentWormIndex).isDead());
    }

    public Worm getCurrentWorm() {
        return worms.get(currentWormIndex);
    }

    public Worm getWormByNumber(int wormNumber) {
        return worms.get(wormNumber);
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

    public User getControllingUser() {
        return controllingUser;
    }

    public boolean isRoundEnded() {
        return roundEnded;
    }

    public void setRoundEnded(boolean roundEnded) {
        this.roundEnded = roundEnded;
    }
}
