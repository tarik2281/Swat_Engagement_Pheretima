package de.paluno.game.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import de.paluno.game.GameState;
import de.paluno.game.screens.PlayScreen;

public class Projectile implements Updatable, PhysicsObject, Renderable {

    private PlayScreen playScreen;
    private Vector2 origin;
    private Vector2 direction;

    private Body body;

    public Projectile(PlayScreen playScreen, Vector2 origin, Vector2 direction) {
        this.playScreen = playScreen;
        this.origin = origin;
        this.direction = direction;
    }

    @Override
    public void update(float delta, GameState gameState) {

    }

    @Override
    public void render(SpriteBatch batch, float delta) {

    }

    @Override
    public void setupBody() {

    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public void setBodyToNullReference() {
        body = null;
    }

    public void explode() {

    }
}
