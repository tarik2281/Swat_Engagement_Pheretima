package de.paluno.game.interfaces;

public class ExplosionEvent extends GameEvent {

    private float centerX, centerY;
    private float radius;
    public int projectileId;

    public ExplosionEvent() {
        super();
    }

    public ExplosionEvent(int tick, float centerX, float centerY, float radius) {
        super(tick, Type.EXPLOSION);

        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getRadius() {
        return radius;
    }
}
