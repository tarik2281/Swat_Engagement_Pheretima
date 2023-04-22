package de.paluno.game;

import com.badlogic.gdx.physics.box2d.*;

import de.paluno.game.gameobjects.AirdropCrate;
import de.paluno.game.gameobjects.Projectile;
import de.paluno.game.gameobjects.WeaponType;
import de.paluno.game.gameobjects.Worm;
import de.paluno.game.screens.PlayScreen;

public class CollisionHandler implements ContactListener {

    private PlayScreen playScreen;

    private boolean listenForCollisions; // TODO: CollisionHandler listenForCollisions

    public CollisionHandler() {
    }


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

            Projectile projectile = UserData.getObject(fixA);
            Worm worm = UserData.getObject(fixB);

            if (projectile.getWeaponType() == WeaponType.WEAPON_MINE && projectile.getShootingWorm() != worm) {
                projectile.explode(worm, true, false);
            } else if (projectile.getWeaponType() != WeaponType.WEAPON_MINE && projectile.explodeOnCollision()) {
                projectile.explode(worm, true, false);
            }

            System.out.println("Projectile -> Worm");
            System.out.println("Worms life = " + ((Worm) o2).getHealth());
        } else if (UserData.getType(fixB) == UserData.ObjectType.Projectile && UserData.getType(fixA) == UserData.ObjectType.Worm) {

            Projectile projectile = UserData.getObject(fixB);
            Worm worm = UserData.getObject(fixA);

            if (projectile.getWeaponType() == WeaponType.WEAPON_MINE && projectile.getShootingWorm() != worm) {
                projectile.explode(worm, true, false);
            } else if (projectile.getWeaponType() != WeaponType.WEAPON_MINE && projectile.explodeOnCollision()) {
                projectile.explode(worm, true, false);
            }
            System.out.println("Projectile -> Worm");
            System.out.println("Worms life = " + ((Worm) o1).getHealth());
        }


        // Projectile -> Ground
        if (UserData.getType(fixA) == UserData.ObjectType.Projectile && UserData.getType(fixB) == UserData.ObjectType.Ground) {
            Projectile projectile = UserData.getObject(fixA);

            if (projectile.getWeaponType() != WeaponType.WEAPON_MINE && projectile.explodeOnCollision()) {
                projectile.explode(null, true, false);
            } else {
                EventManager.getInstance().queueEvent(EventManager.Type.GrenadeCollision, null);
            }

            System.out.println("Projectile collided with Ground");

        } else if (UserData.getType(fixB) == UserData.ObjectType.Projectile && UserData.getType(fixA) == UserData.ObjectType.Ground) {
            Projectile projectile = UserData.getObject(fixB);

            if (projectile.getWeaponType() != WeaponType.WEAPON_MINE && projectile.explodeOnCollision()) {
                projectile.explode(null, true, false);
            } else {
                EventManager.getInstance().queueEvent(EventManager.Type.GrenadeCollision, null);
            }
            System.out.println("Projectile collided with Ground");
        }

        // Worm Headshot
        if (UserData.getType(fixA) == UserData.ObjectType.Headshot && UserData.getType(fixB) == UserData.ObjectType.Projectile) {
            if (((Projectile) o2).explodeOnCollision()) {
                ((Projectile) o2).explode((Worm) o1, true, true);
                if (((Projectile) o2).getWeaponType() == WeaponType.WEAPON_GUN)
                    EventManager.getInstance().queueEvent(EventManager.Type.Headshot, null);
            }
        } else if (UserData.getType(fixB) == UserData.ObjectType.Headshot && UserData.getType(fixA) == UserData.ObjectType.Projectile) {
            if (((Projectile) o1).explodeOnCollision()) {
                ((Projectile) o1).explode((Worm) o2, true, true);
                if (((Projectile) o1).getWeaponType() == WeaponType.WEAPON_GUN)
                    EventManager.getInstance().queueEvent(EventManager.Type.Headshot, null);
            }
        }


        // Turret -> Ground
        if (UserData.getType(fixA) == UserData.ObjectType.Turret && UserData.getType(fixB) == UserData.ObjectType.Ground) {

            Projectile projectile = UserData.getObject(fixA);

            if (projectile.getWeaponType() != WeaponType.WEAPON_TURRET && projectile.explodeOnCollision())
                projectile.explode(null, false, false);

            System.out.println("Turret collided with Ground");

        } else if (UserData.getType(fixB) == UserData.ObjectType.Turret && UserData.getType(fixA) == UserData.ObjectType.Ground) {
            Projectile projectile = UserData.getObject(fixB);

            if (projectile.getWeaponType() != WeaponType.WEAPON_TURRET && projectile.explodeOnCollision())
                projectile.explode(null, false, false);

            System.out.println("Turret collided with Ground");
        }

        //Turret -> Worm
        if (UserData.getType(fixA) == UserData.ObjectType.Turret && UserData.getType(fixB) == UserData.ObjectType.WormFoot) {
            Worm worm = UserData.getObject(fixB);
            worm.beginContact();

        } else if (UserData.getType(fixB) == UserData.ObjectType.Turret && UserData.getType(fixA) == UserData.ObjectType.WormFoot) {
            Worm worm = UserData.getObject(fixA);
            worm.beginContact();
        }

        // Projectile->Turret

        if (UserData.getType(fixA) == UserData.ObjectType.Turret && UserData.getType(fixB) == UserData.ObjectType.Projectile) {

            Projectile projectile = UserData.getObject(fixA);

            projectile.explode(null, true, false);

            System.out.println("Projectile collided with Turret");

        } else if (UserData.getType(fixB) == UserData.ObjectType.Turret && UserData.getType(fixA) == UserData.ObjectType.Projectile) {
            Projectile projectile = UserData.getObject(fixB);
            projectile.explode(null, true, false);

            System.out.println("Projectile collided with Turret");
        }

        // Worm <-> Crate
        if (UserData.getType(fixA) == UserData.ObjectType.Crate && UserData.getType(fixB) == UserData.ObjectType.Worm) {
            AirdropCrate crate = UserData.getObject(fixA);
            crate.pickup(UserData.getObject(fixB));
            System.out.println("Crate picked up!");
        } else if (UserData.getType(fixB) == UserData.ObjectType.Crate && UserData.getType(fixA) == UserData.ObjectType.Worm) {
            AirdropCrate crate = UserData.getObject(fixB);
            crate.pickup(UserData.getObject(fixA));
            System.out.println("Crate picked up!");
        }

        // Crate <-> Ground
        if (UserData.getType(fixA) == UserData.ObjectType.Crate && UserData.getType(fixB) == UserData.ObjectType.Ground) {
            System.out.println("Crate landed!");
            ((AirdropCrate) o1).land();
        } else if (UserData.getType(fixB) == UserData.ObjectType.Crate && UserData.getType(fixA) == UserData.ObjectType.Ground) {
            System.out.println("Crate landed!");
            ((AirdropCrate) o2).land();
        }

        //Crate <-> Crate
        if (UserData.getType(fixA) == UserData.ObjectType.Crate && UserData.getType(fixB) == UserData.ObjectType.Crate) {
            System.out.println("Crate landed!");
            ((AirdropCrate) o1).land();
            ((AirdropCrate) o2).land();
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

        if (UserData.getType(fixA) == UserData.ObjectType.WormFoot && UserData.getType(fixB) == UserData.ObjectType.Turret) {
            ((Worm) o1).endContact();
            System.out.println("Worm isn't in contact with the worm");
        } else if (UserData.getType(fixB) == UserData.ObjectType.WormFoot && UserData.getType(fixA) == UserData.ObjectType.Turret) {
            ((Worm) o2).endContact();
            System.out.println("Worm isn't in contact with the worm");
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture f1 = contact.getFixtureA(), f2 = contact.getFixtureB();
        Object o1 = f1.getBody().getUserData(), o2 = f2.getBody().getUserData();

        if (o1 == null || o2 == null) return;

        /*
         * When a crate has been picked up, it will slowly fade. During this time, no contacts with Worms are allowed
         */
        if (UserData.getType(f1) == UserData.ObjectType.Crate && UserData.getType(f2) == UserData.ObjectType.Worm) {
            AirdropCrate crate = (AirdropCrate) o1;
            if (!crate.getContact()) contact.setEnabled(false);
            System.out.println("Crate ignored!");
        } else if (UserData.getType(f2) == UserData.ObjectType.Crate && UserData.getType(f1) == UserData.ObjectType.Worm) {
            AirdropCrate crate = (AirdropCrate) o2;
            if (!crate.getContact()) contact.setEnabled(false);
            System.out.println("Crate ignored!");
        }

        // Crate <-> Chute
        if (UserData.getType(f1) == UserData.ObjectType.Chute && UserData.getType(f2) == UserData.ObjectType.Crate) {
            contact.setEnabled(false);
        } else if (UserData.getType(f1) == UserData.ObjectType.Crate && UserData.getType(f2) == UserData.ObjectType.Chute) {
            contact.setEnabled(false);
        }

        // Chute <-> Worm
        if (UserData.getType(f1) == UserData.ObjectType.Chute && UserData.getType(f2) == UserData.ObjectType.Worm) {
            contact.setEnabled(false);
        } else if (UserData.getType(f1) == UserData.ObjectType.Worm && UserData.getType(f2) == UserData.ObjectType.Chute) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
