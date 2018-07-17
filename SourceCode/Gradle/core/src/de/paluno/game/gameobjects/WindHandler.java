package de.paluno.game.gameobjects;

import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Random;


public class WindHandler extends WorldObject {

    private Random rand = new Random();

	private int windX;
    private Vector2 wind = new Vector2();

    private List<Projectile> projectiles;

    public WindHandler() {

    }

    @Override
    public void update(float delta) {

        // apply wind (force) to the center of the projectile
        for (Projectile projectile : projectiles) {
            if (projectile.getBody() != null && projectile.getWeaponType() != WeaponType.WEAPON_TURRET_PROJECTILE)
                projectile.getBody().applyForceToCenter(wind, true);
            else {
                return;
            }
        }
    }

    /**
     * @return x - coordinate positive or negative? -> flip sprite
     */
    public boolean flipped() {
        return windX < 0;
    }


    public void setProjectiles(List<Projectile> projectiles) {
        this.projectiles = projectiles;
    }

    public void setWind(int x) {
        windX = x;
        // scl = magnitude which manages the speed
        wind.set(x, 0).scl(0.0001f);
    }

    // generates the random wind (x - coordinate)
    public void nextWind() {
        // magnitude and a direction
        setWind(rand.nextInt(10) - 5);
    }

    public int getWind() {
        return windX;
    }
}
