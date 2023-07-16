package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class ExplosionEvent extends GameEvent {

    private float centerX, centerY;
    private float radius;
    private float blastPower;
    public int projectileId;

    public ExplosionEvent() {
        super();
    }

    public ExplosionEvent(int tick, float centerX, float centerY, float radius, float blastPower) {
        super(tick, Type.EXPLOSION);

        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.blastPower = blastPower;
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

    public float getBlastPower() {
        return blastPower;
    }
}
