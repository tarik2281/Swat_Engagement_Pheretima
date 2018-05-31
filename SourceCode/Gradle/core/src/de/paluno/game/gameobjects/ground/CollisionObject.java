package de.paluno.game.gameobjects.ground;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;

class CollisionObject {
    private float[] vertices;
    private Fixture fixture;

    public CollisionObject(float[] vertices) {
        this.vertices = vertices;
    }

    public void createFixture(Body body) {
        ChainShape shape = new ChainShape();
        shape.createLoop(vertices);
        fixture = body.createFixture(shape, 0.0f);
        fixture.setUserData(this);
        shape.dispose();
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
