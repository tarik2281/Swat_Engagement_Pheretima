package de.paluno.game.server;

import de.paluno.game.interfaces.WormDamageEvent;
import de.paluno.game.interfaces.WormData;

public class Worm {

    private Runnable deathListener;
    private int playerNumber;
    private int wormNumber;
    private int health;
    private boolean isDead;
    private boolean isInfected;

    public Worm(WormData data) {
        this.playerNumber = data.getPlayerNumber();
        this.wormNumber = data.getWormNumber();
        this.health = 100;
        this.isDead = false;
        this.isInfected = false;
    }

    public void setDeathListener(Runnable runnable) {
        this.deathListener = runnable;
    }

    public void applyDamage(WormDamageEvent event) {
        health -= event.getDamage();
    }

    public WormDamageEvent takeDamage(int damage, int damageType) {
        health -= damage;

        if (health <= 0)
            setDead(true);

        return new WormDamageEvent(0, playerNumber, wormNumber, damage, damageType);
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getWormNumber() {
        return wormNumber;
    }

    public int getHealth() {
        return health;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        if (!isDead && dead && deathListener != null)
            deathListener.run();

        isDead = dead;
    }

    public boolean isInfected() {
        return isInfected;
    }

    public void setInfected(boolean infected) {
        isInfected = infected;
    }
}
