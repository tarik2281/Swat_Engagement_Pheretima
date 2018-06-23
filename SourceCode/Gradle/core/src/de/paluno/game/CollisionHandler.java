package de.paluno.game;

import com.badlogic.gdx.physics.box2d.*;

import de.paluno.game.gameobjects.Projectile;
import de.paluno.game.gameobjects.WeaponType;
import de.paluno.game.gameobjects.Worm;
import de.paluno.game.screens.PlayScreen;

public class CollisionHandler implements ContactListener {

    // TODO: 11.06.2018 explodeOnCollision Bazooka ? , preSolve, postSolve?
    // Weapon Special on hit
    // Weapon Special close by

    private boolean listenForCollisions; // TODO: CollisionHandler listenForCollisions

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
        if (UserData.getType(fixA) == UserData.ObjectType.WormFoot && UserData.getType(fixB) == UserData.ObjectType.Worm) {
            ((Worm) o1).beginContact();
            System.out.println("Worms Foots -> Worm");
        } else if (UserData.getType(fixB) == UserData.ObjectType.WormFoot && UserData.getType(fixA) == UserData.ObjectType.Worm) {
            ((Worm) o2).beginContact();
            System.out.println("Worms Foots -> Worm");
        }

        /**
         * Worms Foot -> Ground
         * Worm stands on ground
         */
        if ((UserData.getType(fixA) == UserData.ObjectType.WormFoot && UserData.getType(fixB) == UserData.ObjectType.Ground)) {

            ((Worm) o1).beginContact();
            System.out.println("Worm -> Ground");

        } else if ((UserData.getType(fixB) == UserData.ObjectType.WormFoot && UserData.getType(fixA) == UserData.ObjectType.Ground)) {
            ((Worm) o2).beginContact();
            System.out.println("Worm -> Ground");
        }

        /**
         * Infected Worm -> Worm
         */
        if ((UserData.getType(fixA) == UserData.ObjectType.Virus && UserData.getType(fixB) == UserData.ObjectType.Worm)) {
            System.out.println("Worm infected");
            ((Worm) o2).setIsInfected(true);
        } else if ((UserData.getType(fixB) == UserData.ObjectType.Virus && UserData.getType(fixA) == UserData.ObjectType.Worm)) {
            ((Worm) o1).setIsInfected(true);
            System.out.println("Worm infected");
        }


        // Projectile -> Worm

        if (UserData.getType(fixA) == UserData.ObjectType.Projectile && UserData.getType(fixB) == UserData.ObjectType.Worm) {

            if (((Projectile) o1).explodeOnCollision()) {
                ((Projectile) o1).explode((Worm) o2);
            }
            System.out.println("Projectile -> Worm");
            System.out.println("Worms life = " + ((Worm) o2).getHealth());
        } else if (UserData.getType(fixB) == UserData.ObjectType.Projectile && UserData.getType(fixA) == UserData.ObjectType.Worm) {

            if (((Projectile) o2).explodeOnCollision()) {
                ((Projectile) o2).explode((Worm) o1);
            }
            System.out.println("Projectile -> Worm");
            System.out.println("Worms life = " + ((Worm) o1).getHealth());
        }



        // Projectile -> Ground
        if (UserData.getType(fixA) == UserData.ObjectType.Projectile && UserData.getType(fixB) == UserData.ObjectType.Ground) {
            if (((Projectile) o1).explodeOnCollision()) {
                ((Projectile) o1).explode(null);
            }
            System.out.println("Projectile collided with Ground");

        } else if (UserData.getType(fixB) == UserData.ObjectType.Projectile && UserData.getType(fixA) == UserData.ObjectType.Ground) {
            if (((Projectile) o2).explodeOnCollision()) {
                ((Projectile) o2).explode(null);
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
         * Worms Foot -> Ground
         * Worm isn't in contact with the ground
         */

        if (UserData.getType(fixA) == UserData.ObjectType.WormFoot && UserData.getType(fixB) == UserData.ObjectType.Ground) {
            ((Worm) o1).endContact();
            System.out.println("Worm isn't in contact with the ground");
        } else if (UserData.getType(fixB) == UserData.ObjectType.WormFoot && UserData.getType(fixA) == UserData.ObjectType.Ground) {
            ((Worm) o2).endContact();
            System.out.println("Worm isn't in contact with the ground");
        }

        /**
         * Worms Foot -> Worm
         * Worm isn't in contact with worm
         */
        if (UserData.getType(fixA) == UserData.ObjectType.WormFoot && UserData.getType(fixB) == UserData.ObjectType.Worm) {
            ((Worm) o1).endContact();
            System.out.println("Worm isn't in contact with the worm");
        } else if (UserData.getType(fixB) == UserData.ObjectType.WormFoot && UserData.getType(fixA) == UserData.ObjectType.Worm) {
            ((Worm) o2).endContact();
            System.out.println("Worm isn't in contact with the worm");
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
