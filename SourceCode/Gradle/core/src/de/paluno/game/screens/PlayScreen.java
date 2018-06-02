package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.SEPGame;
import de.paluno.game.gameobjects.ShotDirectionIndicator;
import de.paluno.game.gameobjects.World;
import de.paluno.game.gameobjects.Worm;

public class PlayScreen extends ScreenAdapter implements Loadable {

	private SEPGame game;
	private SpriteBatch spriteBatch;

	private World world;

    private PlayUILayer uiLayer;

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
            Worm worm = world.getCurrentWorm();
            ShotDirectionIndicator indicator = world.getCurrentIndicator();

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
                        world.shootProjectile();
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
                    //camera.setHorizontalMovement(Constants.MOVEMENT_LEFT);
                    return true;
                case Constants.KEY_MOVE_CAMERA_RIGHT:
                    //camera.setHorizontalMovement(Constants.MOVEMENT_RIGHT);
                    return true;
                case Constants.KEY_MOVE_CAMERA_UP:
                    //camera.setVerticalMovement(Constants.MOVEMENT_UP);
                    return true;
                case Constants.KEY_MOVE_CAMERA_DOWN:
                    //camera.setVerticalMovement(Constants.MOVEMENT_DOWN);
                    return true;
                case Constants.KEY_TOGGLE_DEBUG_RENDER:
                    world.toggleDebugRender();
                    return true;
                case Constants.KEY_TOGGLE_CAMERA_FOCUS:
                    //setCameraFocus(camera.getCameraFocus() == null ? getCurrentWorm() : null);
                    break;
            }

            return super.keyDown(keycode);
        }

        @Override
        public boolean keyUp(int keycode) {
            Worm worm = world.getCurrentWorm();

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
                    if (world.getCurrentIndicator() != null)
                	    world.getCurrentIndicator().setRotate(0);
                	return true;

                // debugging key events
                case Constants.KEY_MOVE_CAMERA_LEFT:
                    //if (camera.getHorizontalMovement() == Constants.MOVEMENT_LEFT)
                    //    camera.setHorizontalMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
                case Constants.KEY_MOVE_CAMERA_RIGHT:
                    //if (camera.getHorizontalMovement() == Constants.MOVEMENT_RIGHT)
                    //    camera.setHorizontalMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
                case Constants.KEY_MOVE_CAMERA_UP:
                    //if (camera.getVerticalMovement() == Constants.MOVEMENT_UP)
                    //    camera.setVerticalMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
                case Constants.KEY_MOVE_CAMERA_DOWN:
                    //if (camera.getVerticalMovement() == Constants.MOVEMENT_DOWN)
                    //    camera.setVerticalMovement(Constants.MOVEMENT_NO_MOVEMENT);
                    return true;
            }

            return super.keyUp(keycode);
        }
    };

    public PlayScreen(SEPGame game) {
        this.game = game;

        spriteBatch = new SpriteBatch();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputAdapter);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        uiLayer = new PlayUILayer(screenWidth, screenHeight);

        world = new World(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // game loop
        Gdx.graphics.setTitle("SEPGame FPS: " + Gdx.graphics.getFramesPerSecond());

        world.doGameLoop(spriteBatch, delta);

        renderPhase(delta);
    }

    public void renderPhase(float delta) {
        uiLayer.render(spriteBatch, delta);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    public AssetManager getAssetManager() {
        return game.getAssetManager();
    }

    @Override
    public boolean load(AssetManager manager) {
        manager.load(Assets.map);
        manager.load(Assets.wormWalk);
        manager.load(Assets.wormBreath);
        manager.load(Assets.wormEquipGun);
        manager.load(Assets.arrow);
        manager.load(Assets.projectile);
        manager.load(Assets.ground);
        manager.load(Assets.windGreen);
        manager.load(Assets.windOrange);
        manager.load(Assets.windRed);

        return false;
    }

    public void setGameState(GameState gameState) {
        uiLayer.setGameState(gameState);
    }

    public void setGameOver(WinningPlayer winningPlayer) {
        game.setGameOver(winningPlayer);
    }

    private boolean isPlayerTurn() {
        return world.getGameState() == GameState.PLAYERTWOTURN || world .getGameState() == GameState.PLAYERONETURN;
    }
}
