package de.paluno.game.gameobjects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.Constants;

public class Explosion {

    private Vector2 center;
    private float radius;
    private float blastPower;

    private int numSegments;
    private float[] polygonVertices;

    public Explosion(Vector2 center, float radius, float blastPower) {
        this.center = new Vector2(center);
        this.radius = radius;
        this.blastPower = blastPower;
    }

    public Explosion(float x, float y, float radius, float blastPower) {
        this.center = new Vector2(x, y);
        this.radius = radius;
        this.blastPower = blastPower;
    }

    public Vector2 getCenter() {
        return center;
    }

    public float getRadius() {
        return radius;
    }

    public float getBlastPower() {
        return blastPower;
    }

    public float getLowerX() {
        return center.x - radius;
    }

    public float getLowerY() {
        return center.y - radius;
    }

    public float getUpperX() {
        return center.x + radius;
    }

    public float getUpperY() {
        return center.y + radius;
    }

    public boolean applyBlastImpulse(Worm worm) {
        if (worm.getBody().getWorldCenter().dst2(center) >= radius * radius)
            return false;

        Vector2 diff = new Vector2(worm.getBody().getWorldCenter()).sub(center);
        float distance = diff.len();

        if (distance == 0)
            return true;

        float invDistance = 1.0f / distance;
        diff.scl(invDistance);

        float impulse = blastPower * invDistance * invDistance;
        worm.getBody().applyLinearImpulse(diff.scl(impulse), worm.getBody().getWorldCenter(), true);
        return true;
    }

    public int getNumSegments() {
        if (numSegments == 0)
            numSegments = Math.max(1, (int) (6 * (float) Math.cbrt(radius * Constants.SCREEN_SCALE)));

        return numSegments;
    }

    public float[] getPolygonVertices() {
        if (polygonVertices == null) {
            int segments = getNumSegments();

            polygonVertices = new float[2 * segments];
            float angle = 2 * MathUtils.PI / segments;
            float cos = MathUtils.cos(angle);
            float sin = MathUtils.sin(angle);

            float cx = radius, cy = 0;

            for (int i = 0; i < segments; i++) {
                polygonVertices[i * 2] = center.x + cx;
                polygonVertices[i * 2 + 1] = center.y + cy;
                float temp = cx;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
            }
        }

        return polygonVertices;
    }
}
