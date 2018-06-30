package de.paluno.game.interfaces;

public class PhysicsData{
    private float positionX;
    private float positionY;
    private float velocityX;
    private float velocityY;
    private float angle;

    public PhysicsData() {

    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public float getAngle() {
        return angle;
    }

    public PhysicsData setPositionX(float positionX) {
        this.positionX = positionX;
        return this;
    }

    public PhysicsData setPositionY(float positionY) {
        this.positionY = positionY;
        return this;
    }

    public PhysicsData setVelocityX(float velocityX) {
        this.velocityX = velocityX;
        return this;
    }

    public PhysicsData setVelocityY(float velocityY) {
        this.velocityY = velocityY;
        return this;
    }

    public PhysicsData setAngle(float angle) {
        this.angle = angle;
        return this;
    }

    public PhysicsData interpolate(PhysicsData fromData, PhysicsData toData, float ratio) {
        if (toData == null)
            return fromData;

        float from = 1.0f - ratio;
        positionX = fromData.positionX * from + toData.positionX * ratio;
        positionY = fromData.positionY * from + toData.positionY * ratio;
        velocityX = fromData.velocityX * from + toData.velocityX * ratio;
        velocityY = fromData.velocityY * from + toData.velocityY * ratio;
        angle = fromData.angle * from + toData.angle * ratio;

        return this;
    }
}
