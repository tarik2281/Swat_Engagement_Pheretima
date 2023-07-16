package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
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
