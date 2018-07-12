package de.paluno.game.interfaces;

public class SpawnAirdropEvent extends GameEvent {

    private int crateId;
    private int chuteId;
    private float positionX;
    private float positionY;
    private int dropType;

    public SpawnAirdropEvent() {

    }

    public SpawnAirdropEvent(int tick, int crateId, int chuteId, float positionX, float positionY, int dropType) {
        super(tick, Type.SPAWN_AIRDROP);
        this.crateId = crateId;
        this.chuteId = chuteId;
        this.positionX = positionX;
        this.positionY = positionY;
        this.dropType = dropType;
    }

    public int getCrateId() {
        return crateId;
    }

    public int getChuteId() {
        return chuteId;
    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public int getDropType() {
        return dropType;
    }
}
