package de.paluno.game.gameobjects.ground;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import de.paluno.game.UserData;

class CollisionObject {
    private float[] vertices;
    private Fixture fixture;

    CollisionObject(float[] vertices) {
        this.vertices = vertices;
    }

    boolean createFixture(Body body) {
        ChainShape shape = ShapeFactory.createChainShape(vertices);
        if (shape != null) {
            fixture = body.createFixture(shape, 0.0f);
            fixture.setUserData(new UserData(UserData.ObjectType.Ground,this));
            shape.dispose();
        }

        return shape != null;
    }

    void destroyFixture() {
        if (fixture != null) {
            Body body = fixture.getBody();
            fixture.setUserData(null);
            body.destroyFixture(fixture);
            fixture = null;
        }
    }

    float[] getVertices() {
        return vertices;
    }

    Fixture getFixture() {
        return fixture;
    }
}
