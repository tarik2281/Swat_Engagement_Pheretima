package de.paluno.game;

import org.w3c.dom.UserDataHandler;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.paluno.game.gameobjects.Ground;
import de.paluno.game.gameobjects.Projectile;
import de.paluno.game.gameobjects.Worm;
import de.paluno.game.screens.PlayScreen;

public class CollisionHandler implements ContactListener {

	public CollisionHandler() {
		// TODO Auto-generated constructor stub

	}

	private PlayScreen playScreen;

	public CollisionHandler(PlayScreen playScreen) {
		this.playScreen = playScreen;

	}

	@Override
	public void beginContact(Contact contact) {
		Projectile projectile = null;
		Worm worm = null;
		Ground ground = null;
		
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();
		
		if (a == null || b == null) {
			return;
		}
		if (a.getUserData() == null || b.getUserData() == null) {
			return;
		}
		System.out.println("Collision!");
		
		if (a.getUserData() instanceof Worm && b.getUserData() instanceof Ground && a.isSensor()) {
			worm = (Worm) a.getUserData();
			ground = (Ground) b.getUserData();
			
		}
		
		
		
		

	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}
	// TODO Auto-generated method stub

}
