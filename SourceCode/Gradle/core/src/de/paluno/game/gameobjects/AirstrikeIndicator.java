package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.paluno.game.Assets;
import de.paluno.game.GameState;
import de.paluno.game.interfaces.PointerData;

public class AirstrikeIndicator extends WeaponIndicator<PointerData> {
	
	private Texture texture;
	private Sprite sprite;
	Vector2 curserPosition = new Vector2();

	public AirstrikeIndicator() {

	}
	
	@Override
	public void setupAssets(AssetManager manager) {
		texture = manager.get(Assets.airstrikeCrosshair);
		sprite  = new Sprite(texture);
	}
	
	@Override
	public void render(SpriteBatch batch, float delta) {
		curserPosition.x = Gdx.input.getX();
		curserPosition.y = Gdx.input.getY();
		Vector3 position = getWorld().getCamera().getOrthoCamera().unproject(new Vector3(curserPosition.x, curserPosition.y, 0.0f));
		Vector3 worldPosition = getWorld().getCamera().getWorldCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		
		setPosition(worldPosition.x, worldPosition.y);
		
		sprite.setOriginCenter();
		sprite.setOriginBasedPosition(position.x, position.y);
			
		sprite.draw(batch);
		
	}

	@Override
	public PointerData makeSnapshot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void interpolateSnapshots(PointerData from, PointerData to, float ratio) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return Type.Pointer;
	}
	
	
}
