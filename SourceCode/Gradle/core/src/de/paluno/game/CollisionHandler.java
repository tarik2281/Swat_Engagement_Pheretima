package de.paluno.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;

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
		
		
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();
			
		if (a == null || b == null) {
			return;
		}
		if (a.getUserData() == null || b.getUserData() == null) {
			return;
		}
		
		
		System.out.println("Collision!");
		
		// Projectile -> Worm -> Projectile explode && Worm Die
		if (a.getUserData() instanceof Worm && b.getUserData() instanceof Projectile) {
			worm = (Worm) a.getUserData();
			projectile = (Projectile) b.getUserData();
		}else if (b.getUserData() instanceof Worm && a.getUserData() instanceof Projectile) {
			worm = (Worm) b.getUserData();
			projectile = (Projectile) a.getUserData();
		}
		
		if (worm != null && projectile != null) {
			projectile.explode();
			worm.die();
			System.out.println("Worm dead!");
		}
		
		// Projectile -> Ground -> Projectile explode
		
		if (a.getUserData() instanceof Projectile && b.getUserData() instanceof Ground) {
			ground = (Ground) b.getUserData();
			projectile = (Projectile) b.getUserData();
		}else if (b.getUserData() instanceof Projectile && a.getUserData() instanceof Ground) {
			ground = (Ground) a.getUserData();
			projectile = (Projectile) a.getUserData();
		}
		
		if (projectile!= null && worm != null) {
			projectile.explode();
			
		}
		
		
		// Worm -> Ground -> Worm jump ??
		
		if (a.getUserData() instanceof Worm && b.getUserData() instanceof Ground) {
			worm = (Worm) a.getUserData();
			ground = (Ground) b.getUserData();
		}else if (b.getUserData() instanceof Worm && a.getUserData() instanceof Ground) {
			worm = (Worm) b.getUserData();
			ground = (Ground) a.getUserData();
		}
		
		if (worm != null && ground != null) {
			worm.setStandsOnGround(true);
			System.out.println("Worm back on Ground!");
		}
	
		
	

	}

	@Override
	public void endContact(Contact contact) {
		
		Worm worm = null;
		Ground ground = null;
		
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();
		
		if (a.getUserData() instanceof Worm && b.getUserData() instanceof Ground) {
			worm = (Worm) a.getUserData();
			ground = (Ground) b.getUserData();
		}else if (b.getUserData() instanceof Worm && a.getUserData() instanceof Ground) {
			worm = (Worm) b.getUserData();
			ground = (Ground) a.getUserData();
		}
		
		if (worm != null && ground != null) {
			worm.setStandsOnGround(false);
			System.out.println("Worm jumped");
		}
		
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
