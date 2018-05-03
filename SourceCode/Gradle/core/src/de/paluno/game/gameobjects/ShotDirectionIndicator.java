package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import de.paluno.game.GameState;
import de.paluno.game.screens.PlayScreen;

public class ShotDirectionIndicator extends java.lang.Object implements Renderable, Updatable{
	
	private Worm worm;
	private int playerNumber;
	private SpriteBatch batch;
	private GameState gamestate;
	private Texture arrow;
	
	public ShotDirectionIndicator(int playerNumber, Worm worm, PlayScreen playScreen) {
		super();
		this.worm = worm;
		this.playerNumber = playerNumber;
		arrow = new Texture(Gdx.files.internal("Pfeil.png"));
	}

	public void render(SpriteBatch batch, float delta){
		this.batch = batch;
		if(Gdx.input.isKeyJustPressed(Keys.SPACE) == true){
			batch.begin();
			batch.draw(arrow, 
		}
		
	}
	
	public void update(float delta, GameState gamestate) {
		this.gamestate = gamestate;
	}
	
}
