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
	private Texture sp1won;
	private Texture sp2won;
	private WinningPlayer winningPlayer;
	
	public GameOverScreen(SEPGame game, WinningPlayer winningPlayer) {
		super();
		this.winningPlayer = winningPlayer;
	}
	
	public void show() {
		sprite = new Sprite();
		batch = new SpriteBatch();
		
		if (winningPlayer == WinningPlayer.PLAYERONE) {
			sp1won = new Texture(Gdx.files.internal("Spieler1Win.png"));
		}else{
			sp2won = new Texture(Gdx.files.internal("Spieler2Win.png"));
		}
	}
	
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if (winningPlayer == WinningPlayer.PLAYERONE) {
			batch.draw(sp1won, 0, 0);
		}else{
			batch.draw(sp2won, 0, 0);
		}
		batch.end();
	}
	
	public void hide(){
		batch.dispose();
		if(winningPlayer == WinningPlayer.PLAYERONE){
			sp1won.dispose();
		}else{
			sp2won.dispose();
		}
	}
}