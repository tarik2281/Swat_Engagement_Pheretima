package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.paluno.game.Assets;
import de.paluno.game.SEPGame;

public class GameOverScreen extends com.badlogic.gdx.ScreenAdapter implements Loadable {
	
	protected Sprite sprite;
	private SpriteBatch batch;
	private WinningPlayer winningPlayer;
	private SEPGame game;
	
	public GameOverScreen(SEPGame game, WinningPlayer winningPlayer) {
		this.winningPlayer = winningPlayer;
		this.game = game;
	}

	@Override
	public boolean load(AssetManager manager) {
		Assets.loadAssets(manager, Assets.GameOverScreenAssets);

		return false;
	}

	public void show() {
		batch = new SpriteBatch();
		
		Texture texture = null;
		
		switch (winningPlayer) {
		case PLAYERONE:
			texture = game.getAssetManager().get(Assets.gameOverScreen1);
			break;
		case PLAYERTWO:
			texture = game.getAssetManager().get(Assets.gameOverScreen2);
			break;
		}
		
		sprite = new Sprite(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());	
	}
	
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		sprite.draw(batch);
		batch.end();

		if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
			game.setMenuScreen();
		}
	}
	
	public void hide(){
		batch.dispose();
	}
}