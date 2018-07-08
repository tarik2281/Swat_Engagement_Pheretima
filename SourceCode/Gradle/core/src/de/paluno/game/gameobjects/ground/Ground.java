package de.paluno.game.gameobjects.ground;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.badlogic.gdx.physics.box2d.*;
import de.paluno.game.Map;
import de.paluno.game.UserData;
import de.paluno.game.gameobjects.*;
import de.paluno.game.gameobjects.GameWorld;

import java.util.ArrayList;
import java.util.LinkedList;

public class Ground extends WorldObject {
	
	public class SnapshotData {
		private Map map;
		private ArrayList<CollisionObject.SnapshotData> collisionObjects;
	    private ArrayList<Explosion> explosions;
	}

    private ClipperWrapper clipper;

    private ExplosionMaskRenderer maskRenderer;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Map map;

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

        this.maskRenderer = renderer;

        clipper = new ClipperWrapper();
        collisionObjects = new ArrayList<CollisionObject>();
        queriedObjects = new ArrayList<CollisionObject>();

        explosions = new ArrayList<Explosion>();
        explosionQueue = new LinkedList<Explosion>();
    }

    public void setFromSnapshot(SnapshotData data) {
        for (CollisionObject.SnapshotData objectData : data.collisionObjects)
            collisionObjects.add(new CollisionObject(objectData));

        explosions.addAll(data.explosions);
    }
    
    public Ground(GameWorld world, ExplosionMaskRenderer renderer, SnapshotData data) {
        this.map = data.map;
    	this.maskRenderer = renderer;
    	
    	clipper = new ClipperWrapper();
    	
    	collisionObjects = new ArrayList<CollisionObject>(data.collisionObjects.size());
    	for (CollisionObject.SnapshotData objectData : data.collisionObjects)
    	    collisionObjects.add(new CollisionObject(objectData));

    	queriedObjects = new ArrayList<CollisionObject>();
    	explosions = new ArrayList<Explosion>(data.explosions);
    	explosionQueue = new LinkedList<Explosion>();
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

    @Override
    public void update(float delta) {
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        while (!explosionQueue.isEmpty())
            executeExplosion(explosionQueue.poll());

        if (mapRenderer == null)
            mapRenderer = new OrthogonalTiledMapRenderer(map.getTiledMap(), batch);

        batch.end();

        maskRenderer.enableMask();

        mapRenderer.setView(getWorld().getCamera().getOrthoCamera());
        mapRenderer.render();

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
		data.map = this.map;
		
		return data;
	}
}
