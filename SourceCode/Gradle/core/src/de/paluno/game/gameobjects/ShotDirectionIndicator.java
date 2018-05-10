package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.screens.PlayScreen;

public class ShotDirectionIndicator implements Renderable, Updatable{

	private static final float MOVEMENT_SPEED = 2.0f; // in degrees

	private PlayScreen playScreen;
	private Worm worm;
	private int playerNumber;
	private float degrees = 0;
	private Sprite sprite;
	private Texture texture;
	private int movement;
	
	
	public ShotDirectionIndicator(int playerNumber, Worm worm, PlayScreen playScreen) {
		this.playerNumber = playerNumber;
		this.worm = worm;
		this.playScreen = playScreen;

		texture = new Texture(Gdx.files.internal("Arrow.png"));
		sprite = new Sprite(texture);
	}

	public void update(float delta, GameState gamestate) {
		switch (movement) {
			case Constants.MOVEMENT_UP:
				degrees += MOVEMENT_SPEED;
				break;
			case Constants.MOVEMENT_DOWN:
				degrees -= MOVEMENT_SPEED;
				break;
		}
	}
	
	public void render(SpriteBatch batch, float delta){
		Vector2 position = Constants.getScreenSpaceVector(worm.getBody().getPosition());

	    sprite.setOriginCenter();
		sprite.setRotation(degrees);
		sprite.setOriginBasedPosition(position.x, position.y);
	    
	    sprite.draw(batch);	
	}
	
	public void setRotate(int movement) {
		this.movement = movement;
	}
	
	public float getRotate() {
		return degrees;
	}
}