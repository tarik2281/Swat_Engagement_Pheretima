package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.paluno.game.SEPGame;

public class GameOverScreen extends com.badlogic.gdx.ScreenAdapter {
	
	protected Sprite sprite;
	private SpriteBatch batch;
	private WinningPlayer winningPlayer;
	
	public GameOverScreen(SEPGame game, WinningPlayer winningPlayer) {
		super();
		this.winningPlayer = winningPlayer;
	}
	
	public void show() {
		batch = new SpriteBatch();
		
		Texture texture = null;
		
		switch (winningPlayer) {
		case PLAYERONE:
			texture = new Texture(Gdx.files.internal("Spieler1Win.png"));
			break;
		case PLAYERTWO:
			texture = new Texture(Gdx.files.internal("Spieler2Win.png"));
			break;
		}
		
		sprite = new Sprite(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		sprite.draw(batch);
		batch.end();
	}
	
	public void hide(){
		batch.dispose();
		sprite.getTexture().dispose();
	}
}