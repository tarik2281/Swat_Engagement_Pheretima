package de.paluno.game.gameobjects.ground;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import de.paluno.game.UserData;

class CollisionObject {
	
	class SnapshotData {
		private float[] vertices;
	}

	private Polygon polygon;
    private float[] vertices;
    private Fixture fixture;

    CollisionObject(float[] vertices) {
        polygon = new Polygon(vertices);
        this.vertices = vertices;
    }

    CollisionObject(SnapshotData data) {
        this.vertices = data.vertices;
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

    public boolean contains(float x, float y) {
        return polygon.contains(x, y);
    }

    SnapshotData makeSnapshot() {
		SnapshotData data = new SnapshotData();

		data.vertices = this.vertices;

		return data;
	}
}
