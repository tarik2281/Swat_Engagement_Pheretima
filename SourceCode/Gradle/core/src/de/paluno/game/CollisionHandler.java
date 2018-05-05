package de.paluno.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
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

		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		Body bodyA = fixA.getBody();
		Body bodyB = fixB.getBody();
		
		Object o1 = bodyA.getUserData();
		Object o2 = bodyB.getUserData();
		

		if (fixA == null || fixB == null) {
			return;
		}
		if (o1 == null || o2 == null) {
			return;
		}
		// Worm -> Ground
		if ((fixA.getUserData() == "Worm" && fixB.getUserData() == "Ground")) {
			Worm worm = (Worm) o1;
			worm.setStandsOnGround(true);
			
			System.out.println("Worm on Ground");
			
		} else if ((fixB.getUserData() == "Worm" && fixA.getUserData() == "Ground")) {
			Worm worm = (Worm) o2;
			worm.setStandsOnGround(true);
			
			System.out.println("Worm on Ground!");
			
		}

		// Projectile -> Worm

		if ((fixB.getUserData() == "Projectile" && fixA.getUserData() == "Worm")) {
			Worm worm = (Worm) o1;
			Projectile projectile = (Projectile) o2;
			projectile.explode();
			worm.die();
			
			System.out.println("Projectile collided with Worm!");
			System.out.println("Worm died!");
			
		} else if ((fixB.getUserData() == "Worm" && fixA.getUserData() == "Projectile")) {
			Worm worm = (Worm) o2;
			Projectile projectile = (Projectile) o1;
			projectile.explode();
			worm.die();
			
			System.out.println("Projectile collided with Worm!");
			System.out.println("Worm died!");
		}
		
		
		// Projectile -> Ground
		

		if ((fixB.getUserData() == "Projectile" && fixA.getUserData() == "Ground")) {
			
			Projectile projectile = (Projectile) o2;
			projectile.explode();
			
			System.out.println("Projectile collided with Ground");
			
		} else if ((fixB.getUserData() == "Ground" && fixA.getUserData() == "Projectile")) {
		
			Projectile projectile = (Projectile) o1;
			projectile.explode();
			
			System.out.println("Projectile collided with Ground");
		}
		
		}
		

	@Override
	public void endContact(Contact contact) {

		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		Body bodyA = fixA.getBody();
		Body bodyB = fixB.getBody();
		
		Object o1 = bodyA.getUserData();
		Object o2 = bodyB.getUserData();
		

		if (fixA == null || fixB == null) {
			return;
		}
		if (o1 == null || o2 == null) {
			return;
		}
		
		
		// Worm -> jump || fall -> Ground
		if ((fixA.getUserData() == "Worm" && fixB.getUserData() == "Ground")) {
			Worm worm = (Worm) o1;
			worm.setStandsOnGround(false);
			
			
			System.out.println("Worm not on the Ground!");
			
		} else if ((fixB.getUserData() == "Worm" && fixA.getUserData() == "Ground")) {
			Worm worm = (Worm) o2;
			worm.setStandsOnGround(false);
			
			System.out.println("Worm not on the Ground!");
			
			
		}
		
		// Worm -> fell off World
		
//		if ((fixA.getUserData() == "Worm" && fixB.getUserData() == "Ground")) {
//			Worm worm = (Worm) o1;
//			worm.setStandsOnGround(false);
//			
//			
//			System.out.println("Worm fell off World!");
//			
//		} else if ((fixB.getUserData() == "Worm" && fixA.getUserData() == "Ground")) {
//			Worm worm = (Worm) o2;
//			worm.setStandsOnGround(false);
//			
//			System.out.println("Worm fell off World!");
//			
//			
//		}

		

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
