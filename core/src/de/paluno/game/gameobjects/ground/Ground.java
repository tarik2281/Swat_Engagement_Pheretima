package de.paluno.game.gameobjects.ground;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.paluno.game.Constants;
import de.paluno.game.Map;
import de.paluno.game.UserData;
import de.paluno.game.gameobjects.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class Ground extends WorldObject {
	
	public class SnapshotData {
		private ArrayList<CollisionObject.SnapshotData> collisionObjects;
	    private ArrayList<Explosion> explosions;
	}

    private ClipperWrapper clipper;

    private ExplosionMaskRenderer maskRenderer;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Map map;
    private TiledMapTileLayer tileLayer;
    private TiledMapTileLayer backgroundLayer;

    private ArrayList<CollisionObject> collisionObjects;
    private ArrayList<CollisionObject> queriedObjects;

    private ArrayList<Explosion> explosions;
    private LinkedList<Explosion> explosionQueue;

    private QueryCallback explosionQueryCallback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if (UserData.getType(fixture) == UserData.ObjectType.Ground) {
                CollisionObject object = UserData.getObject(fixture);

                if (!queriedObjects.contains(object))
                    queriedObjects.add(object);
            }

            return true;
        }
    };

    public Ground(Map map, ExplosionMaskRenderer renderer) {
        this.map = map;
        this.tileLayer = (TiledMapTileLayer)map.getTiledMap().getLayers().get(Constants.TILE_LAYER);
        this.backgroundLayer = (TiledMapTileLayer)map.getTiledMap().getLayers().get(Constants.BACKGROUND_LAYER);

        this.maskRenderer = renderer;

        clipper = new ClipperWrapper();
        collisionObjects = new ArrayList<>();
        queriedObjects = new ArrayList<>();

        explosions = new ArrayList<>();
        explosionQueue = new LinkedList<>();
    }

    public void setFromSnapshot(SnapshotData data) {
        for (CollisionObject.SnapshotData objectData : data.collisionObjects)
            collisionObjects.add(new CollisionObject(objectData));

        explosions.addAll(data.explosions);
    }

    public void addExplosion(Explosion explosion) {
        explosionQueue.add(explosion);
    }

    private void executeExplosion(Explosion explosion) {
        queriedObjects.clear();
        getWorld().getWorld().QueryAABB(explosionQueryCallback, explosion.getLowerX(),
                explosion.getLowerY(), explosion.getUpperX(), explosion.getUpperY());

        for (CollisionObject object : queriedObjects) {
            removeCollisionObject(object);
            clipper.addSubjectPolygon(object.getVertices());
        }

        clipper.addClipPolygon(explosion.getPolygonVertices());

        float[][] resultPolygons = clipper.clip();
        for (float[] polygon : resultPolygons) {
            CollisionObject object = new CollisionObject(polygon);
            addCollisionObject(getBody(), object);
        }

        explosions.add(explosion);
    }

    private void addCollisionObject(Body body, CollisionObject object) {
        if (object.createFixture(body))
            collisionObjects.add(object);
    }

    private void removeCollisionObject(CollisionObject object) {
        object.destroyFixture();
        collisionObjects.remove(object);
    }

    ArrayList<Explosion> getExplosions() {
        return explosions;
    }

    public boolean isValidPosition(Vector2 position, float width, float height) {
        boolean valid = true;

        width /= 2.0f;
        height /= 2.0f;

        for (CollisionObject collisionObject : collisionObjects) {
            if (collisionObject.contains(position.x - width, position.y - height) ||
                    collisionObject.contains(position.x - width, position.y + height) ||
                    collisionObject.contains(position.x + width, position.y - height) ||
                    collisionObject.contains(position.x + width, position.y + height)) {
                valid = false;
                break;
            }
        }

        return valid;
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        while (!explosionQueue.isEmpty())
            executeExplosion(explosionQueue.poll());

        if (mapRenderer == null)
            mapRenderer = new OrthogonalTiledMapRenderer(map.getTiledMap(), batch);

        batch.end();

        mapRenderer.setView(getWorld().getCamera().getOrthoCamera());
        if (backgroundLayer != null) {
            batch.begin();
            mapRenderer.renderTileLayer(backgroundLayer);
            batch.end();
        }

        maskRenderer.enableMask();

        if (tileLayer != null) {
            batch.begin();
            mapRenderer.renderTileLayer(tileLayer);
            batch.end();
        }

        maskRenderer.disableMask();

        batch.begin();
    }

    @Override
    public Body onSetupBody(com.badlogic.gdx.physics.box2d.World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bodyDef);

        loadCollisions(body);

        return body;
    }

    private void loadCollisions(Body body) {
    	if (collisionObjects.isEmpty()) {
    	    for (MapObject object : map.getCollisionObjects()) {
    	        float[] vertices = ShapeFactory.createVertices(object);

    	        if (vertices != null)
    	            addCollisionObject(body, new CollisionObject(vertices));
            }
    	}
    	else {
    		for (CollisionObject object : collisionObjects)
    			object.createFixture(body);
    	}
    }

    public SnapshotData makeSnapshot() {
		SnapshotData data = new SnapshotData();

		data.collisionObjects = new ArrayList<>(this.collisionObjects.size());
		for (CollisionObject object : collisionObjects)
		    data.collisionObjects.add(object.makeSnapshot());

		data.explosions = new ArrayList<>(this.explosions);
		
		return data;
	}
}
