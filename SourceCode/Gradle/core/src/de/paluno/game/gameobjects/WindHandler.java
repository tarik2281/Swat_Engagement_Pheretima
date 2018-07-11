package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Random;


public class WindHandler extends WorldObject {

	public static class SnapshotData {
		private int x;
	}

    private Random rand = new Random();

	private int windX;
    private Vector2 wind = new Vector2();

    private List<Projectile> projectiles;
   private WeaponType type;
   public WindHandler() {

    }

    public WindHandler(SnapshotData data) {
    	//this.x = data.x;
    }

    @Override
    public void update(float delta) {
        // magnitude and a direction

        //wind = new Vector2(this.x, 0);

        // scl = magnitude which manages the speed
        //wind.scl(0.0001f);

        // apply wind (force) to the center of the projectile
        for (Projectile projectile : projectiles) {
            if (projectile.getBody() != null && type!= WeaponType.WEAPON_TURRET )
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
        wind.set(x, 0).scl(0.0001f);
    }

    // generates the random wind (x - coordinate)
    public void nextWind() {
        setWind(rand.nextInt(10) - 5);
    }

    public int getWind() {
        return windX;
    }

    public SnapshotData makeSnapshot() {
    	SnapshotData data = new SnapshotData();

    	data.x = windX;

    	return data;
    }
}
