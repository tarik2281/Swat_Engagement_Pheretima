package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class TurretsShootRequest {

    private int simulatingUserId;

    public TurretsShootRequest() {

    }

    public TurretsShootRequest(int simulatingUserId) {
        this.simulatingUserId = simulatingUserId;
    }

    public int getSimulatingUserId() {
        return simulatingUserId;
    }
}
