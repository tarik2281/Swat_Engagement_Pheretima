package de.paluno.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.paluno.game.gameobjects.Renderable;
import de.paluno.game.gameobjects.Updatable;

public class Explosion implements Updatable,Renderable {
	
	public static final float  framelength =0.3f;
	public static final int offset= 8;
	public static final int  size = 32;
	private static Animation animation = null;
	
float x,y,statetime;
public boolean remove= false;
 
 public Explosion (float x,float y) {
	 
	 
 }

@Override
public void update(float delta, GameState gamestate) {
	// TODO Auto-generated method stub
	if (animation==null) {
		
	}
}

@Override
public void render(SpriteBatch batch, float delta) {
	
	batch.draw((Texture) animation.getKeyFrame(statetime), x, y);
}

}
