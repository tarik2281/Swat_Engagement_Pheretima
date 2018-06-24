package de.paluno.game.gameobjects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.screens.Loadable;

public class ShotDirectionIndicator extends WorldObject {

	private static final float MOVEMENT_SPEED = 90.0f; // in degrees

	private Worm worm;
	private float degrees = 0;
	private Sprite sprite;
	private Texture texture;
	private int movement;

	public ShotDirectionIndicator() {

		//texture = world.getAssetManager().get(Assets.arrow);
		//sprite = new Sprite(texture);
	}

	@Override
	public void setupAssets(AssetManager manager) {
		texture = manager.get(Assets.arrow);
		sprite = new Sprite(texture);
	}

	public void attachToWorm(Worm worm) {
		this.worm = worm;
	}

	@Override
	public void update(float delta) {
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
		if (getParent() != null) {
			Vector2 position = Constants.getScreenSpaceVector(getParent().getBody().getPosition());

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
}