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

            Worm worm = (Worm) o1;
            worm.beginContact();
            System.out.println("Worm -> Ground");

        } else if ((UserData.getType(fixB) == UserData.ObjectType.WormFoot && UserData.getType(fixA) == UserData.ObjectType.Ground)) {
            Worm worm = (Worm) o2;
            worm.beginContact();
            System.out.println("Worm -> Ground");
        }


        // Gun -> Worm

        if (UserData.getType(fixA) == UserData.ObjectType.Projectile && UserData.getType(fixB) == UserData.ObjectType.Worm) {
            if (((Projectile) o1).getWeaponType().equals(WeaponType.WEAPON_GUN)) {

                Worm worm = (Worm) o2;
                Projectile projectile = (Projectile) o1;
                if (projectile.explodeOnCollision()) {
                    projectile.explode();
                    worm.takeDamage(Constants.PROJECTILE_DAMAGE);
                }
                System.out.println("Gun -> Worm");
                System.out.println("Worms life = " + worm.getHealth());
            }
        } else if (UserData.getType(fixB) == UserData.ObjectType.Projectile && UserData.getType(fixA) == UserData.ObjectType.Worm) {
            if (((Projectile) o2).getWeaponType().equals(WeaponType.WEAPON_GUN)) {

                Worm worm = (Worm) o1;
                Projectile projectile = (Projectile) o2;
                if (projectile.explodeOnCollision()) {
                    projectile.explode();
                    worm.takeDamage(Constants.PROJECTILE_DAMAGE);
                }
                System.out.println("Gun -> Worm");
                System.out.println("Worms life = " + worm.getHealth());
            }
        }


        //Bazooka -> Worm

        if (UserData.getType(fixA) == UserData.ObjectType.Projectile && UserData.getType(fixB) == UserData.ObjectType.Worm) {
            if (((Projectile) o1).getWeaponType().equals(WeaponType.WEAPON_BAZOOKA)) {
                System.out.println("Bazooka -> Worm");
            }
        } else if (UserData.getType(fixB) == UserData.ObjectType.Projectile && UserData.getType(fixA) == UserData.ObjectType.Worm) {
            if (((Projectile) o2).getWeaponType().equals(WeaponType.WEAPON_BAZOOKA)) {
                System.out.println("Bazooka -> Worm");
            }
        }

        //Grenade -> Worm

        if (UserData.getType(fixA) == UserData.ObjectType.Projectile && UserData.getType(fixB) == UserData.ObjectType.Worm) {
            if (((Projectile) o1).getWeaponType().equals(WeaponType.WEAPON_GRENADE)) {
                System.out.println("Grenade -> Worm");
            }
        } else if (UserData.getType(fixB) == UserData.ObjectType.Projectile && UserData.getType(fixA) == UserData.ObjectType.Worm) {
            if (((Projectile) o2).getWeaponType().equals(WeaponType.WEAPON_GRENADE)) {
                System.out.println("Grenade -> Worm");
            }
        }


        //Special Weapon -> Worm

//        if (UserData.getType(fixA) == UserData.ObjectType.Projectile && UserData.getType(fixB) == UserData.ObjectType.Worm) {
//            if (((Projectile) o1).getWeaponType().equals(WeaponType.WEAPON_SPECIAL)) {
//                System.out.println("Special Weapon");
//            }
//        } else if (UserData.getType(fixB) == UserData.ObjectType.Projectile && UserData.getType(fixA) == UserData.ObjectType.Worm) {
//            if (((Projectile) o2).getWeaponType().equals(WeaponType.WEAPON_SPECIAL)) {
//                System.out.println("Special Weapon");
//            }
//        }


        // Projectile -> Ground
        if (UserData.getType(fixA) == UserData.ObjectType.Projectile && UserData.getType(fixB) == UserData.ObjectType.Ground) {

            Projectile projectile = (Projectile) o1;
            if (projectile.explodeOnCollision()) {
                projectile.explode();
            }
            System.out.println("Projectile collided with Ground");

        } else if (UserData.getType(fixB) == UserData.ObjectType.Projectile && UserData.getType(fixA) == UserData.ObjectType.Ground) {

            Projectile projectile = (Projectile) o2;
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
         * Worms Foot -> Ground
         * Worm isn't in contact with the ground
         */
        if (UserData.getType(fixA) == UserData.ObjectType.WormFoot && UserData.getType(fixB) == UserData.ObjectType.Worm) {
            Worm worm = (Worm) o1;
            worm.endContact();
            System.out.println("Worm isn't in contact with the worm");
        } else if (UserData.getType(fixB) == UserData.ObjectType.WormFoot && UserData.getType(fixA) == UserData.ObjectType.Worm) {
            Worm worm = (Worm) o2;
            worm.endContact();
            System.out.println("Worm isn't in contact with the worm");
        }

        if (UserData.getType(fixA) == UserData.ObjectType.WormFoot && UserData.getType(fixB) == UserData.ObjectType.Ground) {
            Worm worm = (Worm) o1;
            worm.endContact();
            System.out.println("Worm isn't in contact with the ground");
        } else if (UserData.getType(fixB) == UserData.ObjectType.WormFoot && UserData.getType(fixA) == UserData.ObjectType.Ground) {
            Worm worm = (Worm) o2;
            worm.endContact();
            System.out.println("Worm isn't in contact with the ground");
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
