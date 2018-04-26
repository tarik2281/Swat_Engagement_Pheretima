package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import de.paluno.game.SEPGame;

public class GameOverScreen extends com.badlogic.gdx.ScreenAdapter {
	
	protected Sprite sprite;
	private SEPGame game;
	private WinningPlayer winningPlayer;
	
	public GameOverScreen(SEPGame game, WinningPlayer winningPlayer) {
		super();
		this.game = game;
		this.winningPlayer = winningPlayer;
	}
	
	public void show() {
		sprite = new Sprite();
		
		if (winningPlayer == WinningPlayer.PLAYERONE) {
			new Texture(("Spieler1Win"));
		}else{
			new Texture(("Spieler2Win"));
		}
	}
	
	public void render(float delta) {
		
	}
	
	public void hide() {
		
	}
}