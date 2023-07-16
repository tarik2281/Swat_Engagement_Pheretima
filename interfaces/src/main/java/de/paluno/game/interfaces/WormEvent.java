package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class WormEvent extends GameEvent {

    private int playerNumber;
    private int wormNumber;

    public WormEvent() {
        super();
    }

    public WormEvent(int tick, Type type, int playerNumber, int wormNumber) {
        super(tick, type);

        this.playerNumber = playerNumber;
        this.wormNumber = wormNumber;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getWormNumber() {
        return wormNumber;
    }
}
