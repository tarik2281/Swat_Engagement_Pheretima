package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.paluno.game.Assets;
import de.paluno.game.GameState;
import de.paluno.game.InputHandler;
import de.paluno.game.SEPGame;
import de.paluno.game.gameobjects.World;

public class PlayScreen extends ScreenAdapter implements Loadable {

	private SEPGame game;
	private SpriteBatch spriteBatch;

	private World world;

    private PlayUILayer uiLayer;
    WeaponUI weaponUI;

    private InputMultiplexer inputMultiplexer;

    public PlayScreen(SEPGame game) {
        this.game = game;

        spriteBatch = new SpriteBatch();

    }

    @Override
    public void show() {
        //Gdx.input.setInputProcessor(inputAdapter);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        uiLayer = new PlayUILayer(screenWidth, screenHeight);

        world = new World(this);
        //Gdx.input.setInputProcessor(inputAdapter);
        weaponUI = new WeaponUI(this);

        inputMultiplexer = new InputMultiplexer(weaponUI.getInputProcessor(), InputHandler.getInstance());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // game loop
        Gdx.graphics.setTitle("SEPGame FPS: " + Gdx.graphics.getFramesPerSecond());

        world.doGameLoop(spriteBatch, delta);

        renderPhase(delta);

        weaponUI.render(spriteBatch, delta);
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
        for (AssetDescriptor asset : Assets.PlayScreenAssets) {
            manager.load(asset);
        }

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
