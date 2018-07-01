package de.paluno.game.server;

import de.paluno.game.interfaces.Constants;
import de.paluno.game.interfaces.WormDamageEvent;

public class Worm {

    private Runnable deathListener;
    private int playerNumber;
    private int wormNumber;
    private int health;
    private boolean isDead;
    private boolean isInfected;

    public Worm(int playerNumber, int wormNumber) {
        this.playerNumber = playerNumber;
        this.wormNumber = wormNumber;
        this.health = Constants.WORM_MAX_HEALTH;
        this.isDead = false;
        this.isInfected = false;
    }

    public void setDeathListener(Runnable runnable) {
        this.deathListener = runnable;
    }

    /**
     * apply damage as received from clients without further death handling
     * @param damage
     */
    public void applyDamage(int damage) {
        health -= damage;
    }

    /**
     * apply damage to be sent to clients also handling worm death
     * @param damage
     * @param damageType
     * @return event which should be sent to the clients
     */
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
