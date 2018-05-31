package de.paluno.game.gameobjects.ground;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.ArrayList;

class CollisionObject {
    private float[] vertices;
    private Fixture fixture;

    public CollisionObject(float[] vertices) {
        this.vertices = vertices;
    }

    public void createFixture(Body body) {
        ChainShape shape = new ChainShape();
        ArrayList<Float> escapedVerts = new ArrayList<>(vertices.length);
        for (int i = 0; i < vertices.length; i += 2) {
            int prevIndex = i - 2;

            if (i == 0) {
                escapedVerts.add(vertices[i]);
                escapedVerts.add(vertices[i + 1]);
            }
            else if (i != vertices.length - 2 && b2DistanceSquared(vertices[prevIndex], vertices[prevIndex+1], vertices[i], vertices[i+1]) > 0.005f * 0.005f) {
                escapedVerts.add(vertices[i]);
                escapedVerts.add(vertices[i + 1]);
            }
            else if (i == vertices.length - 2 && b2DistanceSquared(vertices[prevIndex], vertices[prevIndex+1], vertices[i], vertices[i+1]) > 0.005f * 0.005f &&
                    b2DistanceSquared(vertices[i], vertices[i+1], vertices[0], vertices[1]) > 0.005f * 0.005f) {
                escapedVerts.add(vertices[i]);
                escapedVerts.add(vertices[i+1]);
            }
        }
        float[] escapedArray = new float[escapedVerts.size()];
        for (int i = 0; i < escapedVerts.size(); i++) {
            escapedArray[i] = escapedVerts.get(i);
        }
        shape.createLoop(escapedArray);
        fixture = body.createFixture(shape, 0.0f);
        fixture.setUserData(this);
        shape.dispose();
    }

    private float b2DistanceSquared(float x1, float y1, float x2, float y2) {
        Vector2 vec = new Vector2(x1, y1);
        return vec.dst2(x2, y2);
    }

    public void destroyFixture() {
        if (fixture != null) {
            Body body = fixture.getBody();
            fixture.setUserData(null);
            body.destroyFixture(fixture);
            fixture = null;
        }
    }

    public float[] getVertices() {
        return vertices;
    }

    public Fixture getFixture() {
        return fixture;
    }
}
