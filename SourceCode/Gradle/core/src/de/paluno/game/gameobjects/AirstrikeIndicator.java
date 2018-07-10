package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.interfaces.PointerData;

public class AirstrikeIndicator extends WeaponIndicator<PointerData> {
	
	private Texture texture;
	private Sprite sprite;

	public AirstrikeIndicator() {

	}
	
	@Override
	public void setupAssets(AssetManager manager) {
		texture = manager.get(Assets.airstrikeCrosshair);
		sprite  = new Sprite(texture);
	}

	@Override
	public void update(float delta) {
		Vector3 worldPosition = getWorld().getCamera().getWorldCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		setPosition(worldPosition.x, worldPosition.y);
	}

	@Override
	public void render(SpriteBatch batch, float delta) {
		Vector2 position = Constants.getScreenSpaceVector(getPosition());

		sprite.setOriginCenter();
		sprite.setOriginBasedPosition(position.x, position.y);
			
		sprite.draw(batch);
	}

	@Override
	public PointerData makeSnapshot() {
		PointerData pointerData = new PointerData();
		pointerData.x = getPosition().x;
		pointerData.y = getPosition().y;
		return pointerData;
	}

	@Override
	public void interpolateSnapshots(PointerData from, PointerData to, float ratio) {
		if (from == null)
			return;
		if (to == null)
			setPosition(from.x, from.y);
		else
			setPosition(from.x * (1.0f - ratio) + to.x * ratio, from.y * (1.0f - ratio) + to.y * ratio);
	}

	@Override
	public Type getType() {
		return Type.Pointer;
	}
}
