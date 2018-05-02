package de.paluno.game.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.paluno.game.GameState;
import de.paluno.game.screens.PlayScreen;

public class ShotDirectionIndicator extends java.lang.Object implements Renderable, Updatable{
	
	private Worm worm;
	private int playerNumber;
	private SpriteBatch batch;
	private GameState gamestate;
	
	public ShotDirectionIndicator(int playerNumber, Worm worm, PlayScreen playScreen) {
		super();
		this.worm = worm;
		this.playerNumber = playerNumber;
	}

	public void render(SpriteBatch batch, float delta){
		this.batch = batch;
		batch = new SpriteBatch();
		batch.begin();
		
	}
	
	public void update(float delta, GameState gamestate) {
		this.gamestate = gamestate;
	}
	
}
