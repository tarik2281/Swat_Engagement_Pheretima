package de.paluno.game.interfaces;

public class GameEvent extends GameData {
    public enum Type {
        WORM_DIED,
        WORM_FELL_DOWN,
        WORM_INFECTED,
        WORM_TOOK_DAMAGE,
        EXPLOSION,
        SHOOT,
        END_TURN,
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
