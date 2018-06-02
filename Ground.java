package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import de.paluno.game.screens.PlayScreen;

public class Ground implements PhysicsObject, Renderable {
	
	private Body Body;
	PlayScreen play;
	private Texture texture;
	
	
	Ground(PlayScreen play){
		this.play = play;
		
	}

	@Override
	public void render(SpriteBatch batch, float delta) {
		// TODO Auto-generated method stub
		 
		Gdx.gl.glClearDepthf(delta);
		texture = new Texture(Gdx.files.internal("Wii - Wii Sports Resort - Ground.png"));
		
		batch.begin();
		batch.draw(texture, 800, 800);
		  
		batch.setColor(0, 1 , 0, 1);
		
		// draw
		batch.end();
	}

	@Override
	public void setBodyToNullReference() {
		// TODO Auto-generated method stub
		this.Body =null;
		
	}

	@Override
	public void setupBody() {
		// TODO Auto-generated method stub
		Body = new Body(this.play.getWorld(), address);
	
		
	}

	@Override
	public Body getBody() {
		// TODO Auto-generated method stub
		return this.Body;
	}

}
