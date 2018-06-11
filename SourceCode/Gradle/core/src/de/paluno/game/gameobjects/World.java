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
    private WindHandler windHandler;

    private int currentPlayer;
    private GameState currentGameState = GameState.NONE;

    private LinkedList<Object> objectRegisterQueue;
    private LinkedList<Object> objectForgetQueue;
    private ArrayList<Renderable> renderableObjects;
    private ArrayList<Updatable> updatableObjects;

    private com.badlogic.gdx.physics.box2d.World world;
    private Rectangle worldBounds;
    private float targetLimit;

    private Player[] players;

    private Ground ground;
    private ExplosionMaskRenderer explosionMaskRenderer;

    private GameCamera camera;
    private boolean isRenderDebug = false;
    private Box2DDebugRenderer debugRenderer;

    private InputHandler.KeyListener keyListener = (keyCode, keyDown) -> {
        if (keyDown) {
            switch (keyCode) {
                case Constants.KEY_TOGGLE_DEBUG_RENDER:
                    toggleDebugRender();
                    return true;
            }
        }

        return true;
    };

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

    public World(PlayScreen screen) {
        this.screen = screen;
        this.windHandler = new WindHandler();

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

        players = new Player[Constants.NUM_PLAYERS];

        currentPlayer = Constants.PLAYER_NUMBER_1;
        initializePlayer(Constants.PLAYER_NUMBER_1);
        initializePlayer(Constants.PLAYER_NUMBER_2);

        registerAfterUpdate(ground);
        registerAfterUpdate(windHandler);

        setGameState(GameState.PLAYERTURN);

        InputHandler.getInstance().registerKeyListener(Constants.KEY_TOGGLE_DEBUG_RENDER, keyListener);
    }

    private void initializePlayer(int playerNumber) {
        players[playerNumber] = new Player(playerNumber, this);
        players[playerNumber].setWindHandler(windHandler);
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

    private void updatePhase(float delta) {
        if (currentGameState == GameState.WAITING) {
            boolean advance = true;

            players: for (Player player : players) {
                for (Worm worm : player.getCharacters()) {
                    if (worm != null && worm.getBody() != null && worm.getBody().isAwake()) {
                        advance = false;
                        break players;
                    }
                }
            }

            if (advance)
                advanceGameState();
        }
        else if (currentGameState == GameState.RAISE_LIMIT) {
            worldBounds.y += Constants.RAISE_LIMIT_SPEED * delta;
            camera.setBottomLimit(worldBounds.y);

            if (worldBounds.y >= targetLimit)
                advanceGameState();
        }

        for (Updatable updatable : updatableObjects) {
            updatable.update(delta, currentGameState);
        }
    }

    private void physicsPhase(float delta) {
        float timeStep = 1.0f / 60.0f;
        world.step(timeStep, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
    }

    private void renderPhase(SpriteBatch batch, float delta) {
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

    public ArrayList<Worm> addExplosion(Explosion explosion) {
        if (explosion.getBlastPower() > 0.0f)
            ground.addExplosion(explosion);

        final ArrayList<Worm> affectedWorms = new ArrayList<>();

        world.QueryAABB((fixture -> {
            if (UserData.getType(fixture) == UserData.ObjectType.Worm) {
                Worm worm = UserData.getObject(fixture);

                if (!affectedWorms.contains(worm))
                    affectedWorms.add(worm);
            }
            return true;
        }), explosion.getLowerX(), explosion.getLowerY(), explosion.getUpperX(), explosion.getUpperY());

        affectedWorms.removeIf(worm -> !explosion.applyBlastImpulse(worm));

        return affectedWorms;
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

    public void playerDefeated(Player player) {
        switch (player.getPlayerNumber()) {
            case Constants.PLAYER_NUMBER_1:
                screen.setGameOver(WinningPlayer.PLAYERTWO);
                break;
            case Constants.PLAYER_NUMBER_2:
                screen.setGameOver(WinningPlayer.PLAYERONE);
                break;
        }
    }

    private void setWormsStatic(boolean isStatic) {
        for (Player player : players) {
            player.setWormsStatic(isStatic);
        }
    }

    private void shiftPlayers() {
        currentPlayer = (currentPlayer + 1) % Constants.NUM_PLAYERS;
    }

    private void setGameState(GameState gameState) {
        switch (this.currentGameState) {
            case PLAYERTURN:
                getCurrentPlayer().onEndTurn();
                shiftPlayers();
                setWormsStatic(false);
                break;
        }

        this.currentGameState = gameState;

        screen.setGameState(gameState, currentPlayer);

        switch (gameState) {
            case PLAYERTURN:
                setWormsStatic(true);
                getCurrentPlayer().onBeginTurn();
                camera.setCameraFocus(getCurrentPlayer().getCurrentWorm());
                windHandler.setNextWind();
                break;
            case GAMEOVERPLAYERONEWON:
                screen.setGameOver(WinningPlayer.PLAYERONE);
                break;
            case GAMEOVERPLAYERTWOWON:
                screen.setGameOver(WinningPlayer.PLAYERTWO);
                break;
            case SHOOTING:
                break;
            case RAISE_LIMIT:
                targetLimit = worldBounds.y + Constants.RAISE_LIMIT_LENGTH;

                for (Player player : players)
                    player.setIsRoundEnded(false);
                break;
        }
    }

    public void advanceGameState() {
        switch (currentGameState) {
            case PLAYERTURN:
                setGameState(GameState.WAITING);
                break;
            case SHOOTING:
                setGameState(GameState.WAITING);
                break;
            case WAITING:
                boolean raiseLimit = true;

                for (Player player : players) {
                    if (!player.isRoundEnded())
                        raiseLimit = false;
                }

                if (raiseLimit)
                    setGameState(GameState.RAISE_LIMIT);
                else
                    setGameState(GameState.PLAYERTURN);

                break;
            case RAISE_LIMIT:
                setGameState(GameState.PLAYERTURN);
                break;
        }
    }

    public GameState getGameState() {
        return currentGameState;
    }

    public Vector2 generateSpawnPosition() {
        return ground.getRandomSpawnPosition();
    }

    public boolean isInWorldBounds(Body body) {
        return body.getPosition().y > worldBounds.y;
    }

    public Rectangle getWorldBounds() {
        return worldBounds;
    }

    public void spawnProjectile(Projectile projectile) {
        windHandler.setProjectile(projectile);
        registerAfterUpdate(projectile);
        setGameState(GameState.SHOOTING);
        camera.setCameraFocus(projectile);
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

    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }
}