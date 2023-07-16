package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class CratePickupEvent extends GameEvent {

    private int crateId;
    private int playerNumber;
    private int wormNumber;

    public CratePickupEvent() {

    }

    public CratePickupEvent(int tick, int crateId, int playerNumber, int wormNumber) {
        super(tick, Type.CRATE_PICKUP);

        this.crateId = crateId;
        this.playerNumber = playerNumber;
        this.wormNumber = wormNumber;
    }

    public int getCrateId() {
        return crateId;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getWormNumber() {
        return wormNumber;
    }
}
