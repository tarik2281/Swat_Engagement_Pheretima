package de.paluno.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import de.paluno.game.screens.PlayScreen;

public class CollisionHandler implements ContactListener {

	private PlayScreen playScreen;

	public CollisionHandler(PlayScreen playScreen) {
		this.playScreen = playScreen;
	}
	
	public void beginContact(Contact contact) {
		
	}
	
	public void endContact(Contact contact){
		
	}
	
	public void preSolve(Contact contact, Manifold oldManifold){
		
	}
	
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}
	
}
