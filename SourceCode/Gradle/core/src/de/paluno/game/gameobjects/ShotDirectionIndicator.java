package de.paluno.game.gameobjects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.interfaces.ShotDirectionData;

public class ShotDirectionIndicator extends WeaponIndicator<ShotDirectionData> {

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
			Vector2 position = Constants.getScreenSpaceVector(getParent().getPosition());

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

	public void setAngle(float angle) {
		this.degrees = angle;
	}

	@Override
	public ShotDirectionData makeSnapshot() {
		ShotDirectionData data = new ShotDirectionData();
		data.angle = getAngle();
		return data;
	}

	@Override
	public void interpolateSnapshots(ShotDirectionData from, ShotDirectionData to, float ratio) {
		if (from == null)
			return;
		if (to == null)
			setAngle(from.angle);
		else
			setAngle(from.angle * (1.0f - ratio) + to.angle * ratio);
	}

	@Override
	public Type getType() {
		return Type.ShotDirection;
	}
}