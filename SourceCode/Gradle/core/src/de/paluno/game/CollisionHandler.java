package de.paluno.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import de.paluno.game.gameobjects.Projectile;
import de.paluno.game.gameobjects.Worm;
import de.paluno.game.screens.PlayScreen;

public class CollisionHandler implements ContactListener {
	
	public static void main(String[] args) {
		
		int [] array1 = new int [5];
		int [] array2 = new int [5];
		for (int i = 0, j = 5; i < array1.length; i++, j--) {
			array1[i] = i;
			array2[i] = j;
		}
	}

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
		}
		
		// Projectile -> Ground -> Projectile explode
		
		if (a.getUserData() instanceof Projectile && b.getUserData() instanceof Ground) {
			worm = (Worm) a.getUserData();
			projectile = (Projectile) b.getUserData();
		}else if (b.getUserData() instanceof Projectile && a.getUserData() instanceof Ground) {
			worm = (Worm) b.getUserData();
			projectile = (Projectile) a.getUserData();
		}
		
		if (projectile!= null && worm != null) {
			projectile.explode();
		}
		
		
		// Worm -> Ground -> Worm jump ??
		
		if (a.getUserData() instanceof Worm && b.getUserData() instanceof Ground) {
			worm = (Worm) a.getUserData();
			ground = (Ground) b.getUserData();
		}else if (b.getUserData() && instanceof Worm && a.getUserData() instanceof Ground) {
			worm = (Worm) b.getUserData();
			ground = (Ground) a.getUserData();
		}
		
		if (worm != null && ground != null) {
			worm.setStandsOnGround(true);
		}
	
		
	

	}

	@Override
	public void endContact(Contact contact) {
		Worm worm = null;
		Ground ground = null;
		
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();
		
		if (a.getUserData() instanceof Worm && b.getUserData() instanceof Ground) {
			worm = (Worm) a.getUserData();
			ground = (Ground) b.getUserData();
		}else if (b.getUserData() instanceof Worm && a.getUserData() instanceof Ground) {
			worm = (Worm) b.getUserData();
			ground = (Ground) a.getUserData();
		}
		
		if (worm != null && ground != null) {
			worm.setStandsOnGround(false);
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
