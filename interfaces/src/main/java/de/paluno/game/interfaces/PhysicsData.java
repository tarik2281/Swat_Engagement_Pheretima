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
}
