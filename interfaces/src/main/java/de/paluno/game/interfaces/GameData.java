package de.paluno.game.interfaces;

public abstract class GameData {

    private int tick;
    private float receivingTimeStamp;

    public GameData() {

    }

    public GameData(int tick) {
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }

    public void setReceivingTimeStamp(float timeStamp) {
        this.receivingTimeStamp = timeStamp;
    }

    public float getReceivingTimeStamp() {
        return receivingTimeStamp;
    }
}
