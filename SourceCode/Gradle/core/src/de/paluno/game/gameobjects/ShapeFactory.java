package de.paluno.game.gameobjects;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import de.paluno.game.Constants;

public class ShapeFactory {

    private static Polygon polygon = new Polygon();
    private static PolygonShape polygonShape = new PolygonShape();

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
