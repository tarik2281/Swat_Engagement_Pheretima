package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.paluno.game.*;
import de.paluno.game.gameobjects.World;

public class PlayScreen extends ScreenAdapter implements Loadable {

    private SEPGame game;
    private SpriteBatch spriteBatch;

    private World world;

    private int mapNumber;
    private PlayUILayer uiLayer;
    private WeaponUI weaponUI;

    public PlayScreen(SEPGame game, int mapNumber, int numWorms) {
        this.game = game;

        this.mapNumber = mapNumber;
        spriteBatch = new SpriteBatch();


    }

    @Override
    public void show() {
        //Gdx.input.setInputProcessor(inputAdapter);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        uiLayer = new PlayUILayer(screenWidth, screenHeight);

        world = new World(this, mapNumber);
        //Gdx.input.setInputProcessor(inputAdapter);
        weaponUI = new WeaponUI(this);
        weaponUI.setPlayer(world.getCurrentPlayer());

        InputMultiplexer inputMultiplexer = new InputMultiplexer(weaponUI.getInputProcessor(), InputHandler.getInstance());
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
        Assets.loadAssets(manager, Assets.PlayScreenAssets);

        manager.load(Assets.getMapByIndex(mapNumber));

        return false;
    }

    public void setGameState(GameState gameState, int currentPlayer) {
        uiLayer.setGameState(gameState, currentPlayer);

   }

    public void setGameOver(WinningPlayer winningPlayer) {
        game.setGameOver(winningPlayer);
    }
}
