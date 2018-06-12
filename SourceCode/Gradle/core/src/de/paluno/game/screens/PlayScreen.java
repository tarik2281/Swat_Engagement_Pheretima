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
	private World replayWorld;
	private boolean disposeReplayAfterUpdate;
    private World.SnapshotData worldSnapshot;

    private WinningPlayer winningPlayer = WinningPlayer.NONE;

    private PlayUILayer uiLayer;
    private WeaponUI weaponUI;
	
    public PlayScreen(SEPGame game) {
        this.game = game;
   
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void show() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
  
        uiLayer = new PlayUILayer(screenWidth, screenHeight);
  
        world = new World(this);
        world.initializeNew();
        weaponUI = new WeaponUI(this);

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

        if (winningPlayer != WinningPlayer.NONE && replayWorld == null) {
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
        for (AssetDescriptor asset : Assets.PlayScreenAssets) {
            manager.load(asset);
        }

        manager.load(Assets.windGreen);
        manager.load(Assets.windOrange);
        manager.load(Assets.windRed);

        return false;
    }

    public void setGameState(World world, GameState gameState, int currentPlayer) {
        uiLayer.setGameState(gameState, currentPlayer);
        
        if (this.world == world && gameState == GameState.SHOOTING)
        	worldSnapshot = world.makeSnapshot();
        
        if (this.replayWorld == world && gameState == GameState.REPLAY_ENDED) {
            disposeReplayAfterUpdate = true;
        }

        if (this.world == world && (gameState == GameState.PLAYERTURN || gameState == GameState.GAMEOVERPLAYERONEWON || gameState == GameState.GAMEOVERPLAYERTWOWON)) {
            if (world.isWormDied() && worldSnapshot != null) {
                replayWorld = new World(this);
                replayWorld.initializeFromSnapshot(worldSnapshot);
            }

            worldSnapshot = null;
        }
    }

    public void setGameOver(WinningPlayer winningPlayer) {
        this.winningPlayer = winningPlayer;
    }
}
