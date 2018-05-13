package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import de.paluno.game.*;
import de.paluno.game.gameobjects.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class PlayScreen extends ScreenAdapter {

	private SEPGame game;
	private SpriteBatch spriteBatch;

    private World world;
    private GameState oldGameState;
    private GameState gameState;

    private Rectangle worldBounds;

    private LinkedList<Object> objectRegisterQueue;
    private LinkedList<Object> objectForgetQueue;

    private ArrayList<Renderable> renderableObjects;
    private ArrayList<Updatable> updatableObjects;

    private Worm[] playerWorms;
    private ShotDirectionIndicator[] shotDirectionIndicators;

    private CollisionHandler collisionHandler;

    private GameCamera camera;

    private PlayUILayer uiLayer;

    private boolean isRenderDebug = false;
    private Box2DDebugRenderer debugRenderer;

    // receive user input events and handle them
    private InputAdapter inputAdapter = new InputAdapter() {

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            // mouse button down
            return super.touchDown(screenX, screenY, pointer, button);
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            // mouse button up
            return super.touchUp(screenX, screenY, pointer, button);
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return super.mouseMoved(screenX, screenY);
        }

        @Override
        public boolean keyDown(int keycode) {
            Worm worm = getCurrentWorm();
            ShotDirectionIndicator indicator = getCurrentIndicator();

            switch (keycode) {
                // gameplay key events
                case Constants.KEY_MOVE_LEFT:
                    if (worm != null && isPlayerTurn())
                        worm.setMovement(Constants.MOVEMENT_LEFT);
                    return true;
                case Constants.KEY_MOVE_RIGHT:
                    if (worm != null && isPlayerTurn())
                        worm.setMovement(Constants.MOVEMENT_RIGHT);
                    return true;
                case Constants.KEY_JUMP:
                    if (worm != null && worm.isStandsOnGround() && isPlayerTurn())
                        worm.setJump(true);
                    return true;
                case Constants.KEY_DO_ACTION:
                    if (worm != null && indicator != null && worm.isStandsOnGround() && isPlayerTurn())
                        fireProjectile(worm, indicator.getRotate());
                    return true;
                case Constants.KEY_ROTATE_INDICATOR_UP:
                    if (indicator != null)
                	    indicator.setRotate(Constants.MOVEMENT_UP);
                	return true;
                case Constants.KEY_ROTATE_INDICATOR_DOWN:
                    if (indicator != null)
                	    indicator.setRotate(Constants.MOVEMENT_DOWN);
                	return true;

                // debugging key events
                case Constants.KEY_MOVE_CAMERA_LEFT:
                    camera.setHorizontalMovement(Constants.MOVEMENT_LEFT);
                    return true;
                case Constants.KEY_MOVE_CAMERA_RIGHT:
                    camera.setHorizontalMovement(Constants.MOVEMENT_RIGHT);
                    return true;
                case Constants.KEY_MOVE_CAMERA_UP:
                    camera.setVerticalMovement(Constants.MOVEMENT_UP);
                    return true;
                case Constants.KEY_MOVE_CAMERA_DOWN:
                    camera.setVerticalMovement(Constants.MOVEMENT_DOWN);
                    return true;
                case Constants.KEY_TOGGLE_DEBUG_RENDER:
                    isRenderDebug = !isRenderDebug;
                    return true;
                case Constants.KEY_TOGGLE_CAMERA_FOCUS:
                    setCameraFocus(camera.getCameraFocus() == null ? getCurrentWorm() : null);
                    break;
            }

            return super.keyDown(keycode);
        }

        @Override
        public boolean keyUp(int keycode) {
            Worm worm = getCurrentWorm();

            switch (keycode) {
                // gameplay key events
                case Constants.KEY_MOVE_LEFT:
                    if (worm != null && worm.getMovement() == Constants.MOVEMENT_LEFT && isPlayerTurn())
                        worm.setMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
                case Constants.KEY_MOVE_RIGHT:
                    if (worm != null && worm.getMovement() == Constants.MOVEMENT_RIGHT && isPlayerTurn())
                        worm.setMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
                case Constants.KEY_ROTATE_INDICATOR_UP:
                case Constants.KEY_ROTATE_INDICATOR_DOWN:
                    if (getCurrentIndicator() != null)
                	    getCurrentIndicator().setRotate(0);
                	return true;

                // debugging key events
                case Constants.KEY_MOVE_CAMERA_LEFT:
                    if (camera.getHorizontalMovement() == Constants.MOVEMENT_LEFT)
                        camera.setHorizontalMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
                case Constants.KEY_MOVE_CAMERA_RIGHT:
                    if (camera.getHorizontalMovement() == Constants.MOVEMENT_RIGHT)
                        camera.setHorizontalMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
                case Constants.KEY_MOVE_CAMERA_UP:
                    if (camera.getVerticalMovement() == Constants.MOVEMENT_UP)
                        camera.setVerticalMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
                case Constants.KEY_MOVE_CAMERA_DOWN:
                    if (camera.getVerticalMovement() == Constants.MOVEMENT_DOWN)
                        camera.setVerticalMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
            }

            return super.keyUp(keycode);
        }
    };

    public PlayScreen(SEPGame game) {
        this.game = game;

        spriteBatch = new SpriteBatch();
        gameState = GameState.PLAYERONETURN;

        world = new World(Constants.GRAVITY, true);

        objectRegisterQueue = new LinkedList<Object>();
        objectForgetQueue = new LinkedList<Object>();

        renderableObjects = new ArrayList<Renderable>();
        updatableObjects = new ArrayList<Updatable>();

        playerWorms = new Worm[Constants.NUM_PLAYERS];
        shotDirectionIndicators = new ShotDirectionIndicator[Constants.NUM_PLAYERS];
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputAdapter);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        camera = new GameCamera(screenWidth, screenHeight);
        uiLayer = new PlayUILayer(screenWidth, screenHeight);

        debugRenderer = new Box2DDebugRenderer();

        registerAfterUpdate(new Ground(this));
        camera.setBottomLimit(-Constants.GROUND_HEIGHT / 2.0f);

        worldBounds = new Rectangle(-Constants.WORLD_WIDTH / 2.0f, -Constants.GROUND_HEIGHT / 2.0f,
                Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        initializePlayer(Constants.PLAYER_NUMBER_1, new Vector2(0.0f, 1.0f));
        initializePlayer(Constants.PLAYER_NUMBER_2, new Vector2(1.0f, 1.0f));

        collisionHandler = new CollisionHandler(this);
        world.setContactListener(collisionHandler);

        setGameState(GameState.PLAYERONETURN);
    }

    @Override
    public void render(float delta) {
        // game loop
        registerObjects();

        Gdx.graphics.setTitle("SEPGame FPS: " + Gdx.graphics.getFramesPerSecond());

        updatePhase(delta);
        physicsPhase(delta);
        renderPhase(delta);

        forgetObjects();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    public void physicsPhase(float delta) {
        float timeStep = 1.0f / 60.0f;
        // TODO: constant time step for consistent physics simulation
        world.step(timeStep, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
    }

    public void renderPhase(float delta) {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update(delta);

        if (isRenderDebug)
            debugRenderer.render(world, camera.getDebugProjection());

        spriteBatch.setProjectionMatrix(camera.getScreenProjection());
        spriteBatch.begin();

        for (Renderable renderable : renderableObjects) {
        	renderable.render(spriteBatch, delta);
        }

        spriteBatch.end();

        // UI drawing
        uiLayer.render(spriteBatch, delta);
    }

    public void updatePhase(float delta) {
        for (Updatable updatable : updatableObjects) {
            updatable.update(delta, gameState);
        }
    }

    public void registerAfterUpdate(Object gameObject) {
        // add object to queue
        objectRegisterQueue.add(gameObject);
    }

    public void forgetAfterUpdate(Object gameObject) {
        // add object to queue
        objectForgetQueue.add(gameObject);
    }

    public void setCameraFocus(PhysicsObject gameObject) {
        camera.setCameraFocus(gameObject);
    }

    public World getWorld() {
        return world;
    }

    public void setGameState(GameState gameState) {
        oldGameState = this.gameState;

        Worm worm = getCurrentWorm();
        if (worm != null)
            worm.setMovement(Constants.MOVEMENT_NO_MOVEMENT);

        this.gameState = gameState;

        uiLayer.setGameState(gameState);

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
                game.setGameOver(WinningPlayer.PLAYERONE);
                break;
            case GAMEOVERPLAYERTWOWON:
                game.setGameOver(WinningPlayer.PLAYERTWO);
                break;
        }

        if (gameState != GameState.SHOOTING)
            setCameraFocus(getCurrentWorm());
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

    public Rectangle getWorldBounds() {
        return worldBounds;
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

    private void initializePlayer(int playerNumber, Vector2 wormPosition) {
        Worm worm = new Worm(playerNumber, this, wormPosition);
        ShotDirectionIndicator indicator = new ShotDirectionIndicator(playerNumber, worm, this);
        HealthBar healthBar = new HealthBar(this, worm);

        playerWorms[playerNumber] = worm;
        shotDirectionIndicators[playerNumber] = indicator;

        registerAfterUpdate(worm);
        registerAfterUpdate(healthBar);
    }

    private void fireProjectile(Worm worm, float angle) {
        Vector2 position = new Vector2(worm.getBody().getPosition());
        Vector2 direction = new Vector2(1, 0).rotate(angle);

        // add an offset to the starting position, so the projectile does not collide with the shooting worm
        position.add(direction.x * Constants.PROJECTILE_SPAWN_OFFSET,
                direction.y * Constants.PROJECTILE_SPAWN_OFFSET);

        Projectile projectile = new Projectile(PlayScreen.this, position,
                new Vector2(1, 0).rotate(angle));

        registerAfterUpdate(projectile);
        forgetAfterUpdate(getCurrentIndicator());

        worm.unequipGun();

        advanceGameState();
        setCameraFocus(projectile);
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

            if (gameObject == camera.getCameraFocus())
                setCameraFocus(null);
        }

        objectForgetQueue.clear();
    }

    private Worm getCurrentWorm() {
        int player = getCurrentPlayer();
        if (player >= 0)
            return playerWorms[player];

        return null;
    }

    private ShotDirectionIndicator getCurrentIndicator() {
        int player = getCurrentPlayer();
        if (player >= 0)
            return shotDirectionIndicators[player];

        return null;
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

    private boolean isPlayerTurn() {
        return gameState == GameState.PLAYERTWOTURN || gameState == GameState.PLAYERONETURN;
    }
}
