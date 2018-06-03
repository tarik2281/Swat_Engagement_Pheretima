package de.paluno.game.gameobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.GameState;

public class ShotDirectionIndicator implements Renderable, Updatable{

	private static final float MOVEMENT_SPEED = 90.0f; // in degrees

	private World world;
	private Worm worm;
	private int playerNumber;
	private float degrees = 0;
	private Sprite sprite;
	private Texture texture;
	private int movement;
	
	
	public ShotDirectionIndicator(int playerNumber, World world) {
		this.playerNumber = playerNumber;
		this.world = world;

		texture = world.getAssetManager().get(Assets.arrow);
		sprite = new Sprite(texture);
	}

	public void attachToWorm(Worm worm) {
		this.worm = worm;
	}

	@Override
	public void update(float delta, GameState gamestate) {
		switch (movement) {
			case Constants.MOVEMENT_UP:
				degrees += MOVEMENT_SPEED * delta;
				break;
			case Constants.MOVEMENT_DOWN:
				degrees -= MOVEMENT_SPEED * delta;
				break;
		}
	}

	@Override
	public void render(SpriteBatch batch, float delta) {
		if (worm != null) {
			Vector2 position = Constants.getScreenSpaceVector(worm.getBody().getPosition());

			sprite.setOriginCenter();
			sprite.setRotation(degrees);
			sprite.setOriginBasedPosition(position.x, position.y);

			sprite.draw(batch);
		}
	}
	
	public void setRotationMovement(int movement) {
		this.movement = movement;
	}

	public int getRotationMovement() {
		return this.movement;
	}

	public float getAngle() {
		return degrees;
	}

	public void setCloningParameters(ShotDirectionIndicator clone) {
		// TODO Auto-generated method stub
		this.playerNumber = clone.playerNumber;
		this.world=clone.world;
		this.degrees= clone.degrees;
		this.worm= clone.worm;
		this.sprite=clone.sprite;
		this.texture= clone.texture;
		this.movement= clone.movement;
		
	}
}