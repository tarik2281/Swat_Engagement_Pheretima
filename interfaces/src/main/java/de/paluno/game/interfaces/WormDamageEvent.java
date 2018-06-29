package de.paluno.game.interfaces;

public class WormDamageEvent extends WormEvent {

    private int damage;

    public WormDamageEvent() {

    }

    public WormDamageEvent(int tick, int playerNumber, int wormNumber, int damage) {
        super(tick, Type.WORM_TOOK_DAMAGE, playerNumber, wormNumber);

        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}
