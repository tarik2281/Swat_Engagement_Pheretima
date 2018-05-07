package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
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

    private CollisionHandler collisionHandler;

    private GameCamera camera;
    private OrthographicCamera uiCamera;

    private float screenWidth;
    private float screenHeight;
    private BitmapFont uiFont;
    private float uiMessageShowTime;
    private GlyphLayout uiMessage;

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
            Worm worm = getCurrentWorm();

            switch (keycode) {
                // TODO: hardcoded keybindings
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
                    if (worm != null && worm.isStandsOnGround() && isPlayerTurn()) {
                        fireProjectile(worm);
                    }
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
            Worm worm = getCurrentWorm();

            switch (keycode) {
                case Constants.KEY_MOVE_LEFT:
                    if (worm != null && worm.getMovement() == Constants.MOVEMENT_LEFT && isPlayerTurn())
                        worm.setMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
                case Constants.KEY_MOVE_RIGHT:
                    if (worm != null && worm.getMovement() == Constants.MOVEMENT_RIGHT && isPlayerTurn())
                        worm.setMovement(Constants.MOVEMENT_NO_MOVEMENT);
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

        uiMessage = new GlyphLayout();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputAdapter);

        screenWidth = Gdx.graphics.getWidth() / 2;
        screenHeight = Gdx.graphics.getHeight() / 2;

        camera = new GameCamera(screenWidth, screenHeight);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(true, screenWidth, screenHeight);
        uiCamera.position.set( screenWidth / 2, screenHeight / 2, 0);
        uiCamera.update();

        uiFont = new BitmapFont(true);
        uiMessageShowTime = Constants.MESSAGE_DURATION;

        debugRenderer = new Box2DDebugRenderer();

        registerAfterUpdate(new Ground(this));
        camera.setBottomBorder(-Constants.GROUND_HEIGHT / 2.0f);

        worldBounds = new Rectangle(-Constants.WORLD_WIDTH / 2.0f, -Constants.GROUND_HEIGHT / 2.0f * Constants.WORLD_SCALE,
                Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        playerWorms[Constants.PLAYER_NUMBER_1] = new Worm(Constants.PLAYER_NUMBER_1,
                this, Constants.getWorldSpaceVector(new Vector2(0, 100)));
        playerWorms[Constants.PLAYER_NUMBER_2] = new Worm(Constants.PLAYER_NUMBER_2,
                this, Constants.getWorldSpaceVector(new Vector2(100, 100)));

        registerAfterUpdate(playerWorms[Constants.PLAYER_NUMBER_1]);
        registerAfterUpdate(playerWorms[Constants.PLAYER_NUMBER_2]);

        registerAfterUpdate(new HealthBar(this, playerWorms[Constants.PLAYER_NUMBER_1]));
        registerAfterUpdate(new HealthBar(this, playerWorms[Constants.PLAYER_NUMBER_2]));

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
        spriteBatch.setProjectionMatrix(uiCamera.combined);
        spriteBatch.begin();

        if (uiMessageShowTime < Constants.MESSAGE_DURATION) {
            uiMessageShowTime += delta;
            uiFont.draw(spriteBatch, uiMessage, screenWidth / 2, 100);
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
        oldGameState = this.gameState;

        Worm worm = getCurrentWorm();
        if (worm != null)
            worm.setMovement(Constants.MOVEMENT_NO_MOVEMENT);

        this.gameState = gameState;

        switch (gameState) {
            case PLAYERONETURN:
                setMessage("Spieler 1 ist am Zug!", Constants.PLAYER_1_COLOR);
                getCurrentWorm().equipGun();
                break;
            case PLAYERTWOTURN:
                setMessage("Spieler 2 ist am Zug!", Constants.PLAYER_2_COLOR);
                getCurrentWorm().equipGun();
                break;
        }

        if (gameState != GameState.SHOOTING)
            setCameraFocus(getCurrentWorm());
    }

    public GameState getGameState() {
        return this.gameState;
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
                setMessage("Spieler 2 hat gewonnen!", Constants.PLAYER_2_COLOR);
                //game.setGameOver(WinningPlayer.PLAYERTWO);
                break;
            case Constants.PLAYER_NUMBER_2:
                setGameState(GameState.GAMEOVERPLAYERONEWON);
                setMessage("Spieler 1 hat gewonnen!", Constants.PLAYER_1_COLOR);
                //game.setGameOver(WinningPlayer.PLAYERONE);
                break;
        }
    }

    private void fireProjectile(Worm worm) {
        Vector2 position = new Vector2(worm.getBody().getPosition());
        position.x += 17 * Constants.WORLD_SCALE;
        Projectile projectile = new Projectile(PlayScreen.this, position, new Vector2(1, 0));
        registerAfterUpdate(projectile);
        worm.unequipGun();
        advanceGameState();
        setCameraFocus(projectile);
    }

    private void setMessage(CharSequence message, Color color) {
        uiMessageShowTime = 0.0f;
        uiMessage.setText(uiFont, message, color, 0, Align.center, false);
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
        int player = getCurrentPlayer();
        if (player >= 0)
            return playerWorms[player];

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
