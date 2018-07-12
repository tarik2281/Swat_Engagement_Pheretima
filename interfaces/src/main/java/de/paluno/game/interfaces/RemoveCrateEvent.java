package de.paluno.game.interfaces;

public class RemoveCrateEvent extends GameEvent {

    private int crateId;

    public RemoveCrateEvent() {

    }

    public RemoveCrateEvent(int tick, Type type, int crateId) {
        super(tick, type);
        this.crateId = crateId;
    }

    public int getCrateId() {
        return crateId;
    }
}
