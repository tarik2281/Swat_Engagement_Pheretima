package de.paluno.game.gameobjects.ground;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.Constants;

class Explosion {

    private Vector2 center;
    private float radius;

    private int numSegments;
    private float[] polygonVertices;

    public Explosion(Vector2 center, float radius) {
        this.center = new Vector2(center);
        this.radius = radius;
    }

    public Explosion(float x, float y, float radius) {
        this.center = new Vector2(x, y);
        this.radius = radius;
    }

    public Vector2 getCenter() {
        return center;
    }

    public float getRadius() {
        return radius;
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

    int getNumSegments() {
        if (numSegments == 0)
            numSegments = Math.max(1, (int) (6 * (float) Math.cbrt(radius * 100.0f)));

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
