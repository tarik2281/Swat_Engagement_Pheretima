package de.paluno.game.gameobjects.ground;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.gameobjects.PhysicsObject;
import de.paluno.game.gameobjects.Renderable;
import de.paluno.game.gameobjects.Updatable;
import de.paluno.game.gameobjects.World;
import de.paluno.game.gameobjects.Worm.SnapshotData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Ground implements PhysicsObject, Renderable, Updatable {
	
	public class SnapshotData {
		private TiledMap tiled;
		
		private ArrayList<CollisionObject.SnapshotData> collisionobjects;

	    private ArrayList<Explosion> explosions;
	}

    private Body body;
    private World world;

    private ClipperWrapper clipper;

    private ExplosionMaskRenderer maskRenderer;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap tiledMap;

    private ArrayList<CollisionObject> collisionObjects;
    private ArrayList<CollisionObject> queriedObjects;

    private ArrayList<Vector2> spawnPoints;

    private ArrayList<Explosion> explosions;
    private LinkedList<Explosion> explosionQueue;

    private QueryCallback explosionQueryCallback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if (fixture.getUserData() instanceof CollisionObject) {
                CollisionObject object = (CollisionObject)fixture.getUserData();

                if (!queriedObjects.contains(object))
                    queriedObjects.add(object);
            }

            return true;
        }
    };

    public Ground(World world, TiledMap tiledMap, ExplosionMaskRenderer renderer) {
        this.world = world;

        this.tiledMap = tiledMap;

        this.maskRenderer = renderer;

        clipper = new ClipperWrapper();
        collisionObjects = new ArrayList<CollisionObject>();
        queriedObjects = new ArrayList<CollisionObject>();

        explosions = new ArrayList<Explosion>();
        explosionQueue = new LinkedList<Explosion>();

        spawnPoints = new ArrayList<>();

        loadSpawnPositions();
    }
    
    public Ground(World world, ExplosionMaskRenderer renderer, SnapshotData data) {
    	this.world = world;
    	this.tiledMap = data.tiled;
    	this.maskRenderer = renderer;
    	
    	clipper = new ClipperWrapper();
    	
    	collisionObjects = new ArrayList<CollisionObject>(data.collisionobjects.size());
    	for (CollisionObject.SnapshotData objectData : data.collisionobjects)
    	    collisionObjects.add(new CollisionObject(objectData));

    	queriedObjects = new ArrayList<CollisionObject>();
    	explosions = new ArrayList<Explosion>(data.explosions);
    	explosionQueue = new LinkedList<Explosion>();
    }

    public void addExplosion(Vector2 center, float radius) {
        explosionQueue.add(new Explosion(center, radius));
    }

    private void executeExplosion(Explosion explosion) {
        queriedObjects.clear();
        world.getWorld().QueryAABB(explosionQueryCallback, explosion.getLowerX(),
                explosion.getLowerY(), explosion.getUpperX(), explosion.getUpperY());

        for (CollisionObject object : queriedObjects) {
            removeCollisionObject(object);
            clipper.addSubjectPolygon(object.getVertices());
        }

        clipper.addClipPolygon(explosion.getPolygonVertices());

        float[][] resultPolygons = clipper.clip();
        for (float[] polygon : resultPolygons) {
            CollisionObject object = new CollisionObject(polygon);
            addCollisionObject(object);
        }

        explosions.add(explosion);
    }

    private void addCollisionObject(CollisionObject object) {
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

    public float getWorldOriginX() {
        return 0.0f;
    }

    public float getWorldOriginY() {
        return 0.0f;
    }

    public float getWorldWidth() {
        TiledMapTileLayer tileLayer = (TiledMapTileLayer)tiledMap.getLayers().get(Constants.TILE_LAYER);
        return tileLayer.getWidth() * tileLayer.getTileWidth() * Constants.WORLD_SCALE;
    }

    public float getWorldHeight() {
        TiledMapTileLayer tileLayer = (TiledMapTileLayer)tiledMap.getLayers().get(Constants.TILE_LAYER);
        return tileLayer.getHeight() * tileLayer.getTileHeight() * Constants.WORLD_SCALE;
    }

    @Override
    public void update(float delta, GameState gamestate) {
        while (!explosionQueue.isEmpty())
            executeExplosion(explosionQueue.poll());
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (mapRenderer == null)
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);

        batch.end();

        maskRenderer.enableMask();

        mapRenderer.setView(world.getCamera().getOrthoCamera());
        mapRenderer.render();

        maskRenderer.disableMask();

        batch.begin();
    }

    @Override
    public void setBodyToNullReference() {
        this.body = null;
    }

    @Override
    public void setupBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = world.createBody(bodyDef);

        loadCollisions();
    }

    @Override
    public Body getBody() {
        return this.body;
    }

    public Vector2 getRandomSpawnPosition() {
        Random random = new Random();
        int index = random.nextInt(spawnPoints.size());
        return spawnPoints.remove(index);
    }

    private void loadSpawnPositions() {
        MapLayer spawnLayer = tiledMap.getLayers().get(Constants.SPAWN_LAYER);
        if (spawnLayer != null) {
            for (MapObject object : spawnLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                    spawnPoints.add(new Vector2(rectangle.getX() * Constants.WORLD_SCALE,
                            rectangle.getY() * Constants.WORLD_SCALE + Constants.WORM_HEIGHT / 2.0f));
                }
            }
        }
    }

    private void loadCollisions() {
    	if (collisionObjects.isEmpty()) {
    		MapLayer collisionLayer = tiledMap.getLayers().get(Constants.COLLISION_LAYER);
    		if (collisionLayer != null) {
    			for (MapObject object : collisionLayer.getObjects()) {
    				float[] vertices = ShapeFactory.createVertices(object);

    				if (vertices != null)
    					addCollisionObject(new CollisionObject(vertices));
    			}
    		}
    	}
    	else {
    		for (CollisionObject object : collisionObjects)
    			object.createFixture(body);
    	}
    }
    public SnapshotData makeSnapshot() {
		SnapshotData data = new SnapshotData();

		data.collisionobjects = new ArrayList<>(this.collisionObjects.size());
		for (CollisionObject object : collisionObjects)
		    data.collisionobjects.add(object.makeSnapshot());

		data.explosions = new ArrayList<>(this.explosions);
		data.tiled = this.tiledMap;
		
		return data;
	}
}
