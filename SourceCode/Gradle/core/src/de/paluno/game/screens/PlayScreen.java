package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import de.paluno.game.CollisionHandler;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.SEPGame;
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

    private OrthographicCamera camera;
    private PhysicsObject cameraFocus;

    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera debugCamera;

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
        public boolean keyUp(int keycode) {
            return super.keyUp(keycode);
        }

        @Override
        public boolean keyDown(int keycode) {
            return super.keyDown(keycode);
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

        registerAfterUpdate(new Projectile(this, new Vector2(200, 200), new Vector2(1.0f, 0.0f)));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        debugRenderer = new Box2DDebugRenderer();
        debugCamera = new OrthographicCamera();

        float debugWidth = Gdx.graphics.getWidth() * Constants.WORLD_SCALE;
        float debugHeight = Gdx.graphics.getHeight() * Constants.WORLD_SCALE;

        debugCamera.setToOrtho(false, debugWidth, debugHeight);
        debugCamera.lookAt(debugWidth / 2, debugHeight / 2, 0);
        debugCamera.update();
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
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(world, debugCamera.combined);

        if (cameraFocus != null) {
            Vector2 position = Constants.getScreenSpaceVector(cameraFocus.getBody().getPosition());
            camera.lookAt(position.x, position.y, 0);
            camera.update();
        }

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.projection);
        spriteBatch.setTransformMatrix(camera.view);

        for (Renderable renderable : renderableObjects) {
        	//renderable.render(spriteBatch, delta);
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
        cameraFocus = gameObject;
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

            if (gameObject == cameraFocus)
                setCameraFocus(null);
        }

        objectForgetQueue.clear();
    }

    private int getCurrentPlayer() {
        int player = -1;

        switch (gameState) {
            case PLAYERONETURN:
                player = 0;
                break;
            case PLAYERTWOTURN:
                player = 1;
                break;
        }

        return player;
    }
}
