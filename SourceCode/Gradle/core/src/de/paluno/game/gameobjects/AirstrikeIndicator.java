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

public class AirstrikeIndicator extends WorldObject {
	
	private Worm worm;
	private int playerNumber;
	private GameWorld world;
	private Texture texture;
	private Sprite sprite;
	Vector2 curserPosition = new Vector2();

	public AirstrikeIndicator(int playerNumber, GameWorld world) {
		this.playerNumber = playerNumber;
		this.world = world;
	}
	
	@Override
	public void setupAssets(AssetManager manager) {
		texture = manager.get(Assets.airstrikeCrosshair);
		sprite  = new Sprite(texture);
	}

	public void attachToWorm(Worm worm) {
		this.worm = worm;
	}
	
	@Override
	public void render(SpriteBatch batch, float delta) {
		curserPosition.x = Gdx.input.getX();
		curserPosition.y = Gdx.input.getY();
		Vector3 position = world.getCamera().getOrthoCamera().unproject(new Vector3(curserPosition.x, curserPosition.y, 0.0f));
		
		sprite.setOriginCenter();
		sprite.setOriginBasedPosition(position.x, position.y);
			
		sprite.draw(batch);
		
	}
	
}
