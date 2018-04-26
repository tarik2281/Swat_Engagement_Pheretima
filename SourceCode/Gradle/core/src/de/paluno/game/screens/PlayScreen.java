package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.SEPGame;
import de.paluno.game.gameobjects.PhysicsObject;
import de.paluno.game.gameobjects.Renderable;
import de.paluno.game.gameobjects.Updatable;

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

    private InputAdapter inputAdapter = new InputAdapter() {
        // TODO: input handling
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
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputAdapter);
    }

    @Override
    public void render(float delta) {
        // game loop
        registerObjects();

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
        // TODO: constant time step for consistent physics simulation
        world.step(delta, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
    }

    public void renderPhase(float delta) {
        for (Renderable renderable : renderableObjects) {
            renderable.render(spriteBatch, delta);
        }
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

    }

    public World getWorld() {
        return world;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
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
        }
    }
}
