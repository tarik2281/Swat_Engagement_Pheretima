package de.paluno.game.interfaces;

public class GameEvent extends GameData {
    public enum Type {
        WORM_DIED,
        WORM_FELL_DOWN,
        WORM_INFECTED,
        WORM_TOOK_DAMAGE,
        EXPLOSION,
        SHOOT,
        AIR_BALL,
        HEADSHOT,
        GRENADE_COLLISION,
        FEET_COLLISION,
        END_TURN,
        SPAWN_AIRDROP,
        CRATE_PICKUP,
        REMOVE_CRATE,
        DESTROY_CHUTE,
        REMOVE_CHUTE,
        CRATE_LANDED,
        TELEPORTER_USE,
        TURRET_SHOT,
        GAME_STATE_CHANGED
    }

    private Type type;

    public GameEvent() {

    }

    public GameEvent(int tick, Type type) {
        super(tick);

        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
