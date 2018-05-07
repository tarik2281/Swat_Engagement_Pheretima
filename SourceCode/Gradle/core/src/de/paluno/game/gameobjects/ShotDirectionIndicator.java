package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.screens.PlayScreen;

public class ShotDirectionIndicator extends java.lang.Object implements Renderable, Updatable{
	
	private PlayScreen playScreen;
	private Worm worm;
	private int playerNumber;
	private static float positionX;
	private static float positionY;
	private float degrees = 0;
	private Sprite sprite;
	private GameState gamestate;
	private Texture texture;
	private int movement;
	
	
	public ShotDirectionIndicator(int playerNumber, Worm worm, PlayScreen playScreen) {
		this.playerNumber = playerNumber;
		this.worm = worm;
		this.playScreen = playScreen;
		
		texture = new Texture(Gdx.files.internal("Pfeil3.png"));
		sprite = new Sprite(texture);
		sprite.setSize(30, 30);
		
	}

	public void update(float delta, GameState gamestate) {
		this.gamestate = gamestate;
		
		positionX = worm.getBody().getPosition().x * Constants.SCREEN_SCALE;
	    positionY = (worm.getBody().getPosition().y * Constants.SCREEN_SCALE) + 50;
	    
		if(movement == -1){
			degrees -= 1;
		}else if(movement == 1) {
			degrees += 1;
		}else {}
	}
	
	public void render(SpriteBatch batch, float delta){
	    sprite.setOriginCenter();
		sprite.setRotation(degrees);
		sprite.setOriginBasedPosition(positionX, positionY);
	    
	    sprite.draw(batch);	
	}
	
	public void setRotate(int movement) {
		this.movement = movement;
	}
	
	public float getRotate() {
		return degrees;
	}
}