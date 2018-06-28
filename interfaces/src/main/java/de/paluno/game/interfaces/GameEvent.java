package de.paluno.game.interfaces;

public abstract class GameEvent extends GameData {
    public enum Type {
        WORM_DIED,
        WORM_INFECTED,
        EXPLOSION,
        SHOOT,
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
