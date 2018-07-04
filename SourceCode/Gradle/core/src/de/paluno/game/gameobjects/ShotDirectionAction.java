package de.paluno.game.gameobjects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.interfaces.ShotDirectionData;

public class ShotDirectionAction  extends WeaponIndicator<ShotDirectionData> {

	
	
	Texture texture;
	Sprite sprite;
	Worm worm;
	float degrees;
	Turret turret;
	
	private GameWorld gameworld;
	
	
	
	
	@Override
	public ShotDirectionData makeSnapshot() {
		// TODO Auto-generated method stub
		return null;
	}
	public ShotDirectionAction() {

		//texture = world.getAssetManager().get(Assets.arrow);
		//sprite = new Sprite(texture);
	}

	@Override
	public void setupAssets(AssetManager manager) {
		texture = manager.get(Assets.arrow);
		sprite = new Sprite(texture);
	}

	public void attachTostandgeschutz(Turret turret) {
		this.turret = turret;
	}

	@Override
	public void update(float delta) {
		
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

	@Override
	public void interpolateSnapshots(ShotDirectionData from, ShotDirectionData to, float ratio) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}
	public Vector2 directions(Worm worm) {
		Vector2 result= new Vector2();
		Vector2 position1 = Constants.getScreenSpaceVector(this.getAdversaryWorm().getBody().getPosition());
		Vector2 position2 = Constants.getScreenSpaceVector(worm.getBody().getPosition());
		
		result.x= position2.x-position1.x;
		result.y=position2.y-position1.y;
		return result;
		
	}
	public float getAngle(Worm worm) {
	degrees = this.directions(worm).angle(new Vector2(1, 0));
		return degrees;
		
	}
	
	public Worm getAdversaryWorm() {
		worm=gameworld.getWorldHandler().getNextActiveWorm();
		return worm;
			}

}
