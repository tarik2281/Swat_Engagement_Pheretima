package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private GameState gameState;

    private LinkedList<Object> objectRegisterQueue;
    private LinkedList<Object> objectForgetQueue;

    private ArrayList<Renderable> renderableObjects;
    private ArrayList<Updatable> updatableObjects;

    private Worm[] playerWorms;

    private CollisionHandler collisionHandler;

    private GameCamera camera;

    private boolean isRenderDebug = false;
    private Box2DDebugRenderer debugRenderer;

    private InputAdapter inputAdapter = new InputAdapter() {
        // TODO: input handling

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
            switch (keycode) {
                // TODO: hardcoded keybindings
                case Constants.KEY_MOVE_LEFT:
                    getCurrentWorm().setMovement(Constants.MOVEMENT_LEFT);
                    return true;
                case Constants.KEY_MOVE_RIGHT:
                    getCurrentWorm().setMovement(Constants.MOVEMENT_RIGHT);
                    return true;
                case Constants.KEY_JUMP:
                    if (getCurrentWorm().isStandsOnGround())
                        getCurrentWorm().setJump(true);
                    return true;
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
            switch (keycode) {
                case Constants.KEY_MOVE_LEFT:
                    if (getCurrentWorm().getMovement() == Constants.MOVEMENT_LEFT)
                        getCurrentWorm().setMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
                case Constants.KEY_MOVE_RIGHT:
                    if (getCurrentWorm().getMovement() == Constants.MOVEMENT_RIGHT)
                        getCurrentWorm().setMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
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
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputAdapter);

        float screenWidth = Gdx.graphics.getWidth() / 2;
        float screenHeight = Gdx.graphics.getHeight() / 2;

        camera = new GameCamera(screenWidth, screenHeight);

        debugRenderer = new Box2DDebugRenderer();

        registerAfterUpdate(new Ground(this));
        camera.setBottomBorder(-Constants.GROUND_HEIGHT / 2.0f);

        playerWorms[Constants.PLAYER_NUMBER_1] = new Worm(Constants.PLAYER_NUMBER_1,
                this, Constants.getWorldSpaceVector(new Vector2(0, 100)));
        playerWorms[Constants.PLAYER_NUMBER_2] = new Worm(Constants.PLAYER_NUMBER_2,
                this, Constants.getWorldSpaceVector(new Vector2(100, 100)));
        registerAfterUpdate(playerWorms[Constants.PLAYER_NUMBER_1]);
        registerAfterUpdate(playerWorms[Constants.PLAYER_NUMBER_2]);
        collisionHandler = new CollisionHandler(this);
        world.setContactListener(collisionHandler);
        setCameraFocus(playerWorms[Constants.PLAYER_NUMBER_1]);
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
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void advanceGameState() {

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
        return playerWorms[getCurrentPlayer()];
    }

    private int getCurrentPlayer() {
        int player = -1;

        switch (gameState) {
            case PLAYERONETURN:
                player = Constants.PLAYER_NUMBER_1;
                break;
            case PLAYERTWOTURN:
                player = Constants.PLAYER_NUMBER_2;
                break;
        }

        return player;
    }
}
