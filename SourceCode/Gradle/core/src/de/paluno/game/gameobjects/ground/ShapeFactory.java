package de.paluno.game.gameobjects.ground;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import de.paluno.game.Constants;

class ShapeFactory {

    private static Polygon polygon = new Polygon();
    private static PolygonShape polygonShape = new PolygonShape();

    public static float[] createVertices(MapObject object) {
        if (object instanceof RectangleMapObject)
            return createVertices((RectangleMapObject)object);
        else if (object instanceof PolygonMapObject)
            return createVertices((PolygonMapObject)object);

        return null;
    }

    public static float[] createVertices(RectangleMapObject object) {
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

    public static float[] createVertices(PolygonMapObject object) {
        Polygon polygon = object.getPolygon();
        polygon.setPosition(polygon.getX() * Constants.WORLD_SCALE, polygon.getY() * Constants.WORLD_SCALE);
        polygon.setScale(Constants.WORLD_SCALE, Constants.WORLD_SCALE);
        return polygon.getTransformedVertices();
    }

    public static PolygonShape createRectangleShape(RectangleMapObject object, Vector2 center) {
        polygonShape.setAsBox(object.getRectangle().width * Constants.WORLD_SCALE / 2.0f,
                object.getRectangle().height * Constants.WORLD_SCALE / 2.0f,
                center, 0.0f);
        return polygonShape;
    }

    public static PolygonShape createPolygonShape(PolygonMapObject object, Vector2 center) {
        Rectangle bounding = object.getPolygon().getBoundingRectangle();
        polygon.setVertices(object.getPolygon().getVertices());
        polygon.setScale(Constants.WORLD_SCALE, Constants.WORLD_SCALE);
        polygon.setPosition(center.x - bounding.width / 2 * Constants.WORLD_SCALE, center.y - bounding.height / 2 * Constants.WORLD_SCALE);
        polygonShape.set(polygon.getTransformedVertices());
        return polygonShape;
    }

}
