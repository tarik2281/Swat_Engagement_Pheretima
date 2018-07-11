package de.paluno.game.interfaces;

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
