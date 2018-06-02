package de.paluno.game;

import com.badlogic.gdx.math.Vector2;
import de.paluno.game.gameobjects.Projectile;
import de.paluno.game.gameobjects.Updatable;

import java.util.Random;


public class WindHandler implements Updatable {
    private Random rand = new Random();

    private Projectile projectile;

    public int getX() {
        return x;
    }

    private int x = rand.nextInt(12) - 6;
    private float degreeWind;
    private  Vector2 wind;

    // generates the random wind (x - coordinate)
    public void setNextWind() {
        this.x = rand.nextInt(12) - 6;
    }

    public void setProjectile(Projectile projectile) {
        this.projectile = projectile;
    }

    @Override
    public void update(float delta, GameState gamestate) {
        // magnitude and a direction
        wind = new Vector2(this.x, 0);

        // scl = magnitude which manages the speed
        wind.scl(0.0001f);
        // System.out.println("Angle: " + degreeWind);
        //System.out.println("X: " + this.x);

        // angle in degree of this vector 0° || 180°
        // for indicator to rotate the arrow in the direction the wind is applied
        degreeWind = wind.angle();

        // apply wind (force) to the center of the projectile
        if (projectile != null && projectile.getBody() != null) {
            this.projectile.getBody().applyForceToCenter(wind, true);
        }

    }


    public float getDegreeWind() {
        return degreeWind;
    }

}
