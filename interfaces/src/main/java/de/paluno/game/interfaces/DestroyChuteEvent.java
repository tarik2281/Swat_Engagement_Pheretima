package de.paluno.game.interfaces;

public class DestroyChuteEvent extends GameEvent {

    private int chuteId;

    public DestroyChuteEvent() {

    }

    public DestroyChuteEvent(int tick, Type type, int chuteId) {
        super(tick, type);
        this.chuteId = chuteId;
    }

    public int getChuteId() {
        return chuteId;
    }
}
