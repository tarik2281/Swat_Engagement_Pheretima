package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import de.paluno.game.*;
import de.paluno.game.gameobjects.ground.ExplosionMaskRenderer;
import de.paluno.game.gameobjects.ground.Ground;

import java.util.ArrayList;
import java.util.LinkedList;

public class GameWorld implements Disposable {

    private WorldHandler worldHandler;

    private LinkedList<WorldObject> objectRegisterQueue;
    private LinkedList<WorldObject> objectForgetQueue;
    private ArrayList<WorldObject> objects;

    private com.badlogic.gdx.physics.box2d.World world;

    private Ground ground;
    private ExplosionMaskRenderer explosionMaskRenderer;

    private GameCamera camera;
    private boolean isRenderDebug = false;
    private Box2DDebugRenderer debugRenderer;

    private ContactFilter contactFilter = (fixtureA, fixtureB) -> {
        if (UserData.getType(fixtureA) == UserData.ObjectType.Worm && UserData.getType(fixtureB) == UserData.ObjectType.Projectile) {
            Projectile projectile = UserData.getObject(fixtureB);
            if (!projectile.isWormContactEnded() && projectile.getShootingWorm() == UserData.getObject(fixtureA)) {
                return false;
            }else {
            	
            }
        }
        else if (UserData.getType(fixtureB) == UserData.ObjectType.Worm && UserData.getType(fixtureA) == UserData.ObjectType.Projectile) {
            Projectile projectile = UserData.getObject(fixtureA);
            if (!projectile.isWormContactEnded() && projectile.getShootingWorm() == UserData.getObject(fixtureB)) {
                return false;
            }else {
            	
            }
        }

        return true;
    };

    public GameWorld(WorldHandler worldHandler) {
        this.worldHandler = worldHandler;

        objectRegisterQueue = new LinkedList<>();
        objectForgetQueue = new LinkedList<>();
        objects = new ArrayList<>();
    }

    public void initialize(Map map) {
        world = new com.badlogic.gdx.physics.box2d.World(Constants.GRAVITY, true);
        world.setContactListener(new CollisionHandler());
        world.setContactFilter(contactFilter);
        debugRenderer = new Box2DDebugRenderer();

        camera = new GameCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        explosionMaskRenderer = new ExplosionMaskRenderer(camera.getOrthoCamera());

        ground = new Ground(map, explosionMaskRenderer);
        explosionMaskRenderer.setGround(ground);

        registerAfterUpdate(ground);
        //windHandler = new WindHandler();
        //camera.setBottomLimit(worldBounds.y);
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        explosionMaskRenderer.dispose();
    }
    
    public WorldHandler getWorldHandler() {
    	return worldHandler;
    }

    public com.badlogic.gdx.physics.box2d.World getWorld() {
        return world;
    }

    public void update(float delta) {
        for (WorldObject object : objects)
            object.update(delta);
    }

    public void step() {
        world.step(1.0f / 60.0f, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
    }

    public void render(SpriteBatch batch, float delta) {
        forgetObjects();
        registerObjects();

        camera.update(delta);

        explosionMaskRenderer.renderDepthMask();

        batch.setProjectionMatrix(camera.getScreenProjection());
        batch.begin();

        for (WorldObject object : objects)
            object.render(batch, delta);

        batch.end();

        //if (isRenderDebug)
            debugRenderer.render(world, camera.getDebugProjection());
    }

    public GameCamera getCamera() {
        return camera;
    }

    public boolean isInWorldBounds(Body body) {
        return body.getWorldCenter().y > 0;
    }

    public ArrayList<Worm> addExplosion(Explosion explosion) {
        if (explosion.getBlastPower() > 0.0f)
            ground.addExplosion(explosion);

        final ArrayList<Worm> affectedWorms = new ArrayList<>();

        world.QueryAABB(fixture -> {
            if (UserData.getType(fixture) == UserData.ObjectType.Worm) {
                Worm worm = UserData.getObject(fixture);

                if (!affectedWorms.contains(worm))
                    affectedWorms.add(worm);
            }
            return true;
        }, explosion.getLowerX(), explosion.getLowerY(), explosion.getUpperX(), explosion.getUpperY());

        affectedWorms.removeIf(worm -> !explosion.applyBlastImpulse(worm));

        return affectedWorms;
    }

    public void toggleDebugRender() {
        isRenderDebug = !isRenderDebug;
    }

    public void registerAfterUpdate(WorldObject gameObject) {
        // add object to queue
        objectRegisterQueue.add(gameObject);
    }

    public void forgetAfterUpdate(WorldObject gameObject) {
        // add object to queue
        objectForgetQueue.add(gameObject);
    }

    private void addObject(WorldObject object) {
        System.out.println("Adding object: " + object.toString());
        object.setWorld(this);
        object.setupAssets(worldHandler.getAssetManager());
        object.setupBody(world);

        objects.add(object);

        for (WorldObject child : object.getChildren())
            addObject(child);
    }

    private void removeObject(WorldObject object) {
        System.out.println("Removing object: " + object.toString());

        if (object.getBody() != null)
            world.destroyBody(object.getBody());
        object.setBodyToNullReference();

        objects.remove(object);

        for (WorldObject child : object.getChildren()) {
            System.out.println("Removing child object: " + child.toString() + " from: " + object.toString());
            removeObject(child);
        }

        object.setWorld(null);

        if (camera.getCameraFocus() == object)
            camera.setCameraFocus(null);
    }

    private void registerObjects() {
        // add all objects from queue
        for (WorldObject object : objectRegisterQueue) {
            addObject(object);
        }

        objectRegisterQueue.clear();
    }

    private void forgetObjects() {
        // remove all objects from queue
        for (WorldObject object : objectForgetQueue) {
            removeObject(object);
        }

        objectForgetQueue.clear();
    }
}
