package de.paluno.game.interfaces;

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
