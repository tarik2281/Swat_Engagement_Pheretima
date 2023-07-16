package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class SpawnAirdropRequest {

    private int simulatingUserId;

    public SpawnAirdropRequest() {

    }

    public SpawnAirdropRequest(int simulatingUserId) {
        this.simulatingUserId = simulatingUserId;
    }

    public int getSimulatingUserId() {
        return simulatingUserId;
    }
}
