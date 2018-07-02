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
	private World replayWorld;
	private boolean disposeReplayAfterUpdate;
    private World.SnapshotData worldSnapshot;

    private int winningPlayer = -2;

    private int mapNumber;
    private int numWorms;
    private int numPlayers;
    private PlayUILayer uiLayer;
    private WeaponUI weaponUI;

    public PlayScreen(SEPGame game, int mapNumber, int numWorms, int numPlayers) {
        this.game = game;

        this.mapNumber = mapNumber;
        this.numWorms = numWorms;
        this.numPlayers = numPlayers;

        spriteBatch = new SpriteBatch();
    }

    @Override
    public void show() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        uiLayer = new PlayUILayer(screenWidth, screenHeight);

        world = new World(this);
        world.initializeNew(mapNumber, numWorms, numPlayers);
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

        if (replayWorld != null)
        	replayWorld.doGameLoop(spriteBatch, delta);
        else
        	world.doGameLoop(spriteBatch, delta);

        renderPhase(delta);

        weaponUI.render(spriteBatch, delta);

        if (disposeReplayAfterUpdate) {
            replayWorld.dispose();
            replayWorld = null;
            disposeReplayAfterUpdate = false;
        }

        if (winningPlayer != -2 && replayWorld == null) {
            game.setGameOver(winningPlayer);
        }
    }

    public void renderPhase(float delta) {
        uiLayer.render(spriteBatch, delta);
    }

    @Override
    public void hide() {
        world.dispose();
        world = null;

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

    public void setGameState(World world, GameState gameState, int currentPlayer) {
        if(winningPlayer != -2) uiLayer.setGameState(gameState, winningPlayer);
        else uiLayer.setGameState(gameState, currentPlayer);

        if (this.world == world && gameState == GameState.SHOOTING)
        	worldSnapshot = world.makeSnapshot();

        if (this.replayWorld == world && gameState == GameState.REPLAY_ENDED) {
            disposeReplayAfterUpdate = true;
        }

        if (this.world == world && (gameState == GameState.PLAYERTURN || gameState == GameState.GAMEOVER)) {
            if (world.isWormDied() && worldSnapshot != null) {
                replayWorld = new World(this);
                replayWorld.initializeFromSnapshot(worldSnapshot);
            }

            weaponUI.setPlayer(this.world.getCurrentPlayer());
            worldSnapshot = null;
        }
    }

    public void setGameOver(int winningPlayer) {
        this.winningPlayer = winningPlayer;
    }
}
