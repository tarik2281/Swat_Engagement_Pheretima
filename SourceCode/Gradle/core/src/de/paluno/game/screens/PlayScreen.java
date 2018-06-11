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
import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.World;
import de.paluno.game.gameobjects.Worm;

public class PlayScreen extends ScreenAdapter implements Loadable {
 
	private boolean isdie  = true;
	private SEPGame game;
	private SpriteBatch spriteBatch; 
	
	private World world;
	private World replayWorld;
	
    private PlayUILayer uiLayer;
    private WeaponUI weaponUI;
	private de.paluno.game.gameobjects.World.SnapshotData data;
	
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

    public void setGameState(World world, GameState gameState, int currentPlayer) {
        uiLayer.setGameState(gameState, currentPlayer);
        
        if (this.world == world && gameState == GameState.SHOOTING)
        	data = world.makeSnapshot();
        
        if (this.replayWorld == world && gameState == GameState.REPLAY_ENDED)
        	replayWorld = null;
        
        if (this.world == world && gameState == GameState.PLAYERTURN && world.isWormDied()) {
        	replayWorld = new World(this, data);
        }
    }

    public void setGameOver(WinningPlayer winningPlayer) {
        game.setGameOver(winningPlayer);
    }
}
