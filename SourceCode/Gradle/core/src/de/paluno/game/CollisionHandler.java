package de.paluno.game;

import com.badlogic.gdx.physics.box2d.*;

import de.paluno.game.gameobjects.Projectile;
import de.paluno.game.gameobjects.Worm;
import de.paluno.game.gameobjects.ground.Ground;
import de.paluno.game.screens.PlayScreen;

public class CollisionHandler implements ContactListener {

	public CollisionHandler() {

	}

	private PlayScreen playScreen;

	public CollisionHandler(PlayScreen playScreen) {
		this.playScreen = playScreen;

	}
	
	/**
    * treats the contact of colliding fixtures
    */
	@Override
	public void beginContact(Contact contact) {

		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();

		if (fixA == null || fixB == null) {
			return;
		}
		
		Object o1 = fixA.getBody().getUserData();
		Object o2 = fixB.getBody().getUserData();

		if (o1 == null || o2 == null) {
			return;
		}



        /**
         * Worms Foot -> Worm
         * Worm stands on another worm
         */
        if (fixA.getUserData() == "WormFoot" && fixB.getUserData() == "Worm") {
			((Worm)o1).beginContact();
		}
		else if (fixB.getUserData() == "WormFoot" && fixA.getUserData() == "Worm") {
			// worm stands on another worm
			((Worm)o2).beginContact();
		}

        /**
         * Worms Foot -> Ground
         * Worm stands on ground
         */
		if ((fixA.getUserData() == "WormFoot" && o2 instanceof Ground)) {

			Worm worm = (Worm) o1;
			worm.beginContact();
			
		} else if ((fixB.getUserData() == "WormFoot" && o1 instanceof Ground)) {
			Worm worm = (Worm) o2;
			worm.beginContact();
		}


		// Projectile -> Worm

		if ((fixB.getUserData() == "Projectile" && fixA.getUserData() == "Worm")) {
			Worm worm = (Worm) o1;
			Projectile projectile = (Projectile) o2;
			if (projectile.explodeOnCollision()) {
				projectile.explode();
				worm.takeDamage(Constants.PROJECTILE_DAMAGE);
			}
			
			System.out.println("Projectile collided with Worm!");
			System.out.println("Worms life = " + worm.getHealth());

		} else if ((fixB.getUserData() == "Worm" && fixA.getUserData() == "Projectile")) {
			Worm worm = (Worm) o2;
			Projectile projectile = (Projectile) o1;

			if (projectile.explodeOnCollision()) {
				projectile.explode();
				worm.takeDamage(Constants.PROJECTILE_DAMAGE);
			}
			
			System.out.println("Projectile collided with Worm!");
			System.out.println("Worms life = " + worm.getHealth());
		}


		// Projectile Bazooka -> Worm
        if ((fixB.getUserData() == "Bazooka" && fixA.getUserData() == "Worm")) {

        } else if ((fixB.getUserData() == "Worm" && fixA.getUserData() == "Bazooka")) {

        }
		
		
		// Projectile -> Ground
		if ((fixB.getUserData() == "Projectile" && o1 instanceof Ground)) {
			
			Projectile projectile = (Projectile) o2;
			if (projectile.explodeOnCollision()) {
                projectile.explode();
            }
			System.out.println("Projectile collided with Ground");
			
		} else if ((o2 instanceof Ground && fixA.getUserData() == "Projectile")) {
		
			Projectile projectile = (Projectile) o1;
			if (projectile.explodeOnCollision()) {
                projectile.explode();
            }
			System.out.println("Projectile collided with Ground");
		}
	}
	
	/**
    * treats the contact of fixtures which separate from each other
    */
	@Override
	public void endContact(Contact contact) {

		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		if (fixA == null || fixB == null) {
			return;
		}
		
		Object o1 = fixA.getBody().getUserData();
		Object o2 = fixB.getBody().getUserData();

		if (o1 == null || o2 == null) {
			return;
		}

        /**
         * Worm -> Ground
         * Worm isn't in contact with the ground
         */
		if ((fixA.getUserData() == "WormFoot" && o2 instanceof Ground)) {
			Worm worm = (Worm) o1;
			worm.endContact();
		} else if ((fixB.getUserData() == "WormFoot" && o1 instanceof Ground)) {
			Worm worm = (Worm) o2;
			worm.endContact();
		}

		if (fixA.getUserData() == "WormFoot" && fixB.getUserData() == "Worm") {
			Worm worm = (Worm)o1;
			worm.endContact();
		}
		else if (fixB.getUserData() == "WormFoot" && fixA.getUserData() == "Worm") {
			Worm worm = (Worm)o2;
			worm.endContact();
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) { }

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) { }
}
