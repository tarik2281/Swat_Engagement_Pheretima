package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import de.paluno.game.*;
import de.paluno.game.gameobjects.ground.ExplosionMaskRenderer;
import de.paluno.game.gameobjects.ground.Ground;
import de.paluno.game.screens.PlayScreen;
import de.paluno.game.screens.WinningPlayer;

import java.util.ArrayList;
import java.util.LinkedList;

public class World {

    private PlayScreen screen;

    private GameState oldGameState = GameState.PLAYERONETURN;
    private GameState gameState = GameState.PLAYERONETURN;

    private LinkedList<Object> objectRegisterQueue;
    private LinkedList<Object> objectForgetQueue;
    private ArrayList<Renderable> renderableObjects;
    private ArrayList<Updatable> updatableObjects;

    private com.badlogic.gdx.physics.box2d.World world;
    private Rectangle worldBounds;

    private Worm[] playerWorms;
    private ShotDirectionIndicator[] shotDirectionIndicators;

    private Ground ground;
    private ExplosionMaskRenderer explosionMaskRenderer;

    private GameCamera camera;
    private boolean isRenderDebug = false;
    private Box2DDebugRenderer debugRenderer;

    private ContactFilter contactFilter = new ContactFilter() {
        @Override
        public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
            if (fixtureA.getUserData() == "Worm" && fixtureB.getUserData() == "Projectile") {
                Projectile projectile = (Projectile)fixtureB.getBody().getUserData();
                if (!projectile.isWormContactEnded() && projectile.getShootingWorm() == fixtureA.getBody().getUserData())
                    return false;
            }
            else if (fixtureB.getUserData() == "Worm" && fixtureA.getUserData() == "Projectile") {
                Projectile projectile = (Projectile)fixtureA.getBody().getUserData();
                if (!projectile.isWormContactEnded() && projectile.getShootingWorm() == fixtureB.getBody().getUserData())
                    return false;
            }

            return true;
        }
    };

    public World(PlayScreen screen) {
        this.screen = screen;

        objectRegisterQueue = new LinkedList<>();
        objectForgetQueue = new LinkedList<>();
        renderableObjects = new ArrayList<>();
        updatableObjects = new ArrayList<>();

        world = new com.badlogic.gdx.physics.box2d.World(Constants.GRAVITY, true);
        world.setContactListener(new CollisionHandler());
        world.setContactFilter(contactFilter);

        worldBounds = new Rectangle();

        camera = new GameCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setBottomLimit(0.0f);
        explosionMaskRenderer = new ExplosionMaskRenderer(camera.getOrthoCamera());

        ground = new Ground(this, screen.getAssetManager().get(Assets.map), explosionMaskRenderer);
        explosionMaskRenderer.setGround(ground);

        worldBounds.set(ground.getWorldOriginX(), ground.getWorldOriginY(),
                ground.getWorldWidth(), ground.getWorldHeight());

        debugRenderer = new Box2DDebugRenderer();

        playerWorms = new Worm[Constants.NUM_PLAYERS];
        shotDirectionIndicators = new ShotDirectionIndicator[Constants.NUM_PLAYERS];

        initializePlayer(Constants.PLAYER_NUMBER_1);
        initializePlayer(Constants.PLAYER_NUMBER_2);

        registerAfterUpdate(ground);

        setGameState(GameState.PLAYERONETURN);
    }

    private void initializePlayer(int playerNumber) {
        Player player = new Player(playerNumber, this, getAssetManager());
        // TODO: add worms

        /*Worm worm = new Worm(playerNumber, this, wormPosition);
        ShotDirectionIndicator indicator = new ShotDirectionIndicator(playerNumber, worm, this);
        HealthBar healthBar = new HealthBar(this, worm);

        playerWorms[playerNumber] = worm;
        shotDirectionIndicators[playerNumber] = indicator;

        registerAfterUpdate(worm);
        registerAfterUpdate(healthBar);*/
    }

    public void toggleDebugRender() {
        isRenderDebug = !isRenderDebug;
    }

    public void doGameLoop(SpriteBatch batch, float delta) {
        registerObjects();

        updatePhase(delta);
        physicsPhase(delta);
        renderPhase(batch, delta);

        forgetObjects();
    }

    public void updatePhase(float delta) {
        for (Updatable updatable : updatableObjects) {
            updatable.update(delta, gameState);
        }
    }

    public void physicsPhase(float delta) {
        float timeStep = 1.0f / 60.0f;
        world.step(timeStep, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
    }

    public void renderPhase(SpriteBatch batch, float delta) {
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
    }

    public void registerAfterUpdate(Object gameObject) {
        // add object to queue
        objectRegisterQueue.add(gameObject);
    }

    public void forgetAfterUpdate(Object gameObject) {
        // add object to queue
        objectForgetQueue.add(gameObject);
    }

    public void addExplosion(Vector2 center, float radius) {
        ground.addExplosion(center, radius);
    }

    public Body createBody(BodyDef bodyDef) {
        return world.createBody(bodyDef);
    }

    public Joint createJoint(JointDef jointDef) {
        return world.createJoint(jointDef);
    }

    public com.badlogic.gdx.physics.box2d.World getWorld() {
        return world;
    }

    public GameCamera getCamera() {
        return camera;
    }

    public AssetManager getAssetManager() {
        return screen.getAssetManager();
    }

    public void setGameState(GameState gameState) {
        oldGameState = this.gameState;

        Worm worm = getCurrentWorm();
        if (worm != null)
            worm.setMovement(Constants.MOVEMENT_NO_MOVEMENT);

        this.gameState = gameState;

        screen.setGameState(gameState);

        switch (gameState) {
            case PLAYERONETURN:
                getCurrentWorm().equipGun();
                registerAfterUpdate(getCurrentIndicator());
                break;
            case PLAYERTWOTURN:
                getCurrentWorm().equipGun();
                registerAfterUpdate(getCurrentIndicator());
                break;
            case GAMEOVERPLAYERONEWON:
                screen.setGameOver(WinningPlayer.PLAYERONE);
                break;
            case GAMEOVERPLAYERTWOWON:
                screen.setGameOver(WinningPlayer.PLAYERTWO);
                break;
        }

        if (gameState != GameState.SHOOTING)
            camera.setCameraFocus(getCurrentWorm());
    }

    public void advanceGameState() {
        switch (gameState) {
            case PLAYERONETURN:
                setGameState(GameState.SHOOTING);
                break;
            case PLAYERTWOTURN:
                setGameState(GameState.SHOOTING);
                break;
            case SHOOTING:
                switch (oldGameState) {
                    case PLAYERONETURN:
                        setGameState(GameState.PLAYERTWOTURN);
                        break;
                    case PLAYERTWOTURN:
                        setGameState(GameState.PLAYERONETURN);
                        break;
                }
                break;
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public Vector2 generateSpawnPosition() {
        return ground.getRandomSpawnPosition();
    }

    public Rectangle getWorldBounds() {
        return worldBounds;
    }

    public Worm getCurrentWorm() {
        int player = getCurrentPlayer();
        if (player >= 0)
            return playerWorms[player];

        return null;
    }

    public ShotDirectionIndicator getCurrentIndicator() {
        int player = getCurrentPlayer();
        if (player >= 0)
            return shotDirectionIndicators[player];

        return null;
    }

    public void shootProjectile() {
        Worm worm = getCurrentWorm();
        ShotDirectionIndicator indicator = getCurrentIndicator();

        Vector2 position = new Vector2(worm.getBody().getPosition());
        Vector2 direction = new Vector2(1, 0).rotate(indicator.getRotate());

        // add an offset to the starting position, so the projectile does not collide with the shooting worm
        //position.add(direction.x * Constants.PROJECTILE_SPAWN_OFFSET,
        //        direction.y * Constants.PROJECTILE_SPAWN_OFFSET);
        Projectile projectile = new Projectile(this, worm, position, direction);

        registerAfterUpdate(projectile);
        forgetAfterUpdate(indicator);

        worm.unequipGun();

        advanceGameState();
        camera.setCameraFocus(projectile);
    }

    public void wormDied(Worm worm) {
        switch (worm.getPlayerNumber()) {
            case Constants.PLAYER_NUMBER_1:
                setGameState(GameState.GAMEOVERPLAYERTWOWON);
                break;
            case Constants.PLAYER_NUMBER_2:
                setGameState(GameState.GAMEOVERPLAYERONEWON);
                break;
        }
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

    private int getCurrentPlayer() {
        int player = -1;

        switch (gameState) {
            case PLAYERONETURN:
            case GAMEOVERPLAYERONEWON:
                player = Constants.PLAYER_NUMBER_1;
                break;
            case PLAYERTWOTURN:
            case GAMEOVERPLAYERTWOWON:
                player = Constants.PLAYER_NUMBER_2;
                break;
        }

        return player;
    }
}
