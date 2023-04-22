package de.paluno.game.gameobjects.ground;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import de.paluno.game.Constants;

class ShapeFactory {

    private static Vector2 distanceCheckVector = new Vector2();

    static float[] createVertices(MapObject object) {
        if (object instanceof RectangleMapObject)
            return createVertices((RectangleMapObject)object);
        else if (object instanceof PolygonMapObject)
            return createVertices((PolygonMapObject)object);

        return null;
    }

    static float[] createVertices(RectangleMapObject object) {
        Rectangle rect = object.getRectangle();

        float[] vertices = new float[2 * 4];
        vertices[0] = rect.x                    * Constants.WORLD_SCALE;
        vertices[1] = rect.y                    * Constants.WORLD_SCALE;
        vertices[2] = (rect.x + rect.width)     * Constants.WORLD_SCALE;
        vertices[3] = rect.y                    * Constants.WORLD_SCALE;
        vertices[4] = (rect.x + rect.width)     * Constants.WORLD_SCALE;
        vertices[5] = (rect.y + rect.height)    * Constants.WORLD_SCALE;
        vertices[6] = rect.x                    * Constants.WORLD_SCALE;
        vertices[7] = (rect.y + rect.height)    * Constants.WORLD_SCALE;
        return vertices;
    }

    static float[] createVertices(PolygonMapObject object) {
        float[] transformedVerts = object.getPolygon().getTransformedVertices();
        float[] vertices = new float[transformedVerts.length];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = transformedVerts[i] * Constants.WORLD_SCALE;
        }
        return vertices;
        /*Polygon polygon = object.getPolygon();
        polygon.setPosition(polygon.getX() * Constants.WORLD_SCALE, polygon.getY() * Constants.WORLD_SCALE);
        polygon.setScale(Constants.WORLD_SCALE, Constants.WORLD_SCALE);
        return polygon.getTransformedVertices();*/
    }

    static ChainShape createChainShape(float[] v) {
        float[] escapedVertices = new float[v.length];
        int size = 0;

        for (int i = 0; i < v.length; i+=2) {
            if (i == 0 || checkDistance(escapedVertices[size-2], escapedVertices[size-1], v[i], v[i+1])) {
                if (i == v.length - 2 && !checkDistance(v[i], v[i+1], v[0], v[1]))
                    continue;

                escapedVertices[size++] = v[i];
                escapedVertices[size++] = v[i+1];
            }
        }

        ChainShape shape = null;

        if (size / 2 > 2) {
            shape = new ChainShape();
            shape.createLoop(escapedVertices, 0, size);
        }

        return shape;
    }

    private static boolean checkDistance(float x1, float y1, float x2, float y2) {
        distanceCheckVector.set(x1, y1);
        return distanceCheckVector.dst2(x2, y2) > 0.005f * 0.005f;
    }

}
