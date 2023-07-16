package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class WormDamageEvent extends WormEvent {

    private int damage;
    private int damageType;

    public WormDamageEvent() {

    }

    public WormDamageEvent(int tick, int playerNumber, int wormNumber, int damage, int damageType) {
        super(tick, Type.WORM_TOOK_DAMAGE, playerNumber, wormNumber);

        this.damage = damage;
        this.damageType = damageType;
    }

    public int getDamage() {
        return damage;
    }

    public int getDamageType() {
        return damageType;
    }
}
