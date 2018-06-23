package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.utils.Disposable;
import de.paluno.game.*;
import de.paluno.game.gameobjects.ground.ExplosionMaskRenderer;
import de.paluno.game.gameobjects.ground.Ground;
import de.paluno.game.screens.Loadable;

import java.util.ArrayList;
import java.util.LinkedList;

public class World2 implements Disposable {

    private WorldHandler worldHandler;

    private GameState lastGameState = GameState.NONE;
    private GameState currentGameState = GameState.NONE;

    private LinkedList<Object> objectRegisterQueue;
    private LinkedList<Object> objectForgetQueue;
    private ArrayList<Renderable> renderableObjects;
    private ArrayList<Updatable> updatableObjects;

    private com.badlogic.gdx.physics.box2d.World world;

    private Ground ground;
    private ExplosionMaskRenderer explosionMaskRenderer;

    private Projectile projectile;

    private GameCamera camera;
    private boolean isRenderDebug = false;
    private Box2DDebugRenderer debugRenderer;

    private ContactFilter contactFilter = (fixtureA, fixtureB) -> {
        if (UserData.getType(fixtureA) == UserData.ObjectType.Worm && UserData.getType(fixtureB) == UserData.ObjectType.Projectile) {
            Projectile projectile = UserData.getObject(fixtureB);
            if (!projectile.isWormContactEnded() && projectile.getShootingWorm() == UserData.getObject(fixtureA))
                return false;
        }
        else if (UserData.getType(fixtureB) == UserData.ObjectType.Worm && UserData.getType(fixtureA) == UserData.ObjectType.Projectile) {
            Projectile projectile = UserData.getObject(fixtureA);
            if (!projectile.isWormContactEnded() && projectile.getShootingWorm() == UserData.getObject(fixtureB))
                return false;
        }

        return true;
    };

    public World2(WorldHandler worldHandler) {
        this.worldHandler = worldHandler;

        objectRegisterQueue = new LinkedList<>();
        objectForgetQueue = new LinkedList<>();
        renderableObjects = new ArrayList<>();
        updatableObjects = new ArrayList<>();
    }

    public void initialize(Map map) {
        world = new com.badlogic.gdx.physics.box2d.World(Constants.GRAVITY, true);
        world.setContactListener(new CollisionHandler());
        world.setContactFilter(contactFilter);
        debugRenderer = new Box2DDebugRenderer();

        camera = new GameCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        explosionMaskRenderer = new ExplosionMaskRenderer(camera.getOrthoCamera());

        ground = new Ground(this, map, explosionMaskRenderer);
        explosionMaskRenderer.setGround(ground);
        windHandler = new WindHandler();
        camera.setBottomLimit(worldBounds.y);
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        explosionMaskRenderer.dispose();
    }

    public void update(float delta) {
        switch (currentGameState) {
            case WAITING:
                if (worldHandler.shouldIdle()) // TODO: idle game state
                    currentGameState = GameState.IDLE;
                break;
        }

        for (Updatable updatable : updatableObjects) {
            updatable.update(delta, currentGameState);
        }
    }

    public void step() {
        world.step(1.0f / 60.0f, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
    }

    public void render(SpriteBatch batch, float delta) {
        camera.update(delta);

        explosionMaskRenderer.renderDepthMask();

        batch.setProjectionMatrix(camera.getScreenProjection());
        batch.begin();

        for (Renderable renderable : renderableObjects) {
            renderable.render(batch, delta);
        }

        batch.end();

        if (isRenderDebug)
            debugRenderer.render(world, camera.getDebugProjection());

        forgetObjects();
        registerObjects();
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

    public void registerAfterUpdate(Object gameObject) {
        // add object to queue
        objectRegisterQueue.add(gameObject);
    }

    public void forgetAfterUpdate(Object gameObject) {
        // add object to queue
        objectForgetQueue.add(gameObject);
    }

    private void registerObjects() {
        // add all objects from queue
        for (Object gameObject : objectRegisterQueue) {
            if (gameObject instanceof Updatable) {
                updatableObjects.add((Updatable)gameObject);
            }
            if (gameObject instanceof PhysicsObject) {
                PhysicsObject physicsObject = (PhysicsObject)gameObject;
                physicsObject.setupBody();
                physicsObject.getBody().setUserData(gameObject);
            }
            if (gameObject instanceof Renderable) {
                renderableObjects.add((Renderable)gameObject);
            }
            if (gameObject instanceof Loadable) {
                ((Loadable)gameObject).load(worldHandler.getAssetManager());
            }
        }

        objectRegisterQueue.clear();
    }

    private void forgetObjects() {
        // remove all objects from queue
        for (Object gameObject : objectForgetQueue) {
            if (gameObject instanceof Updatable) {
                updatableObjects.remove((Updatable)gameObject);
            }
            if (gameObject instanceof PhysicsObject) {
                PhysicsObject physicsObject = (PhysicsObject)gameObject;
                if (physicsObject.getBody() != null)
                    world.destroyBody(physicsObject.getBody());
                physicsObject.setBodyToNullReference();
            }
            if (gameObject instanceof Renderable) {
                renderableObjects.remove((Renderable)gameObject);
            }
            if (gameObject instanceof Disposable) {
                ((Disposable)gameObject).dispose();
            }

            if (gameObject == camera.getCameraFocus())
                camera.setCameraFocus(null);
        }

        objectForgetQueue.clear();
    }
}
