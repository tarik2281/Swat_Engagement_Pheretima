package de.paluno.game;

import com.badlogic.gdx.math.Vector2;
import de.paluno.game.gameobjects.Projectile;
import de.paluno.game.gameobjects.Updatable;

import java.util.Random;


public class WindHandler implements Updatable {
    private Random rand = new Random();
    private Vector2 wind;
    private Projectile projectile;
    private int x = rand.nextInt(10) - 5;


    @Override
    public void update(float delta, GameState gamestate) {
        // magnitude and a direction
        wind = new Vector2(this.x, 0);

        // scl = magnitude which manages the speed
        wind.scl(0.0001f);

        // apply wind (force) to the center of the projectile
        if (projectile != null && projectile.getBody() != null) {
            this.projectile.getBody().applyForceToCenter(wind, true);
        }

    }

    /**
     * @return x - coordinate positive or negative? -> flip sprite
     */
    public boolean flipped() {
        if (x < 0) {
            return true;
        } else {
            return false;
        }
    }


    public void setProjectile(Projectile projectile) {
        this.projectile = projectile;
    }

    // generates the random wind (x - coordinate)
    public void setNextWind() {
        this.x = rand.nextInt(10) - 5;
    }


    public int getX() {
        return x;
    }
}
