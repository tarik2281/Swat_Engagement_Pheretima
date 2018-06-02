package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.paluno.game.Constants;
import de.paluno.game.GameState;

public class Projectile implements Updatable, PhysicsObject, Renderable {

    // in meters
    private static final float PROJECTILE_RADIUS = 0.03f;
    private static final float PROJECTILE_DENSITY = 0.1f;

    private World world;
    private Vector2 position;
    private Vector2 direction;

    private Body body;

    private Texture texture;
    private Sprite sprite;

    private boolean exploded = false;

    private Worm shootingWorm;
    private boolean wormContactEnded = false;

    public Projectile(World world, Worm shootingWorm, Vector2 position, Vector2 direction) {
        this.world = world;
        this.position = position;
        this.direction = direction;

        texture = new Texture(Gdx.files.internal("Projectile.png"));
        sprite = new Sprite(texture);

        sprite.setOriginCenter();

        this.shootingWorm = shootingWorm;
    }

    public Worm getShootingWorm() {
        return shootingWorm;
    }

    public boolean isWormContactEnded() {
        return wormContactEnded;
    }

    @Override
    public void update(float delta, GameState gameState) {
        // check if the projectile is inside our world - if not, destroy it
        if (!world.getWorldBounds().contains(body.getPosition()))
            explode();

        if (!wormContactEnded) {
            float distanceSQ = shootingWorm.getBody().getPosition().dst2(body.getPosition());
            if (distanceSQ > Constants.WORM_RADIUS_SQUARE + PROJECTILE_RADIUS * PROJECTILE_RADIUS)
                wormContactEnded = true;
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        Vector2 position = Constants.getScreenSpaceVector(body.getPosition());

        sprite.setOriginBasedPosition(position.x, position.y);
        sprite.draw(batch);
    }

    @Override
    public void setupBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y);
        bodyDef.bullet = true;

        CircleShape shape = new CircleShape();
        shape.setRadius(PROJECTILE_RADIUS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = PROJECTILE_DENSITY;

        body = world.createBody(bodyDef);

        Fixture fix = body.createFixture(fixtureDef);

        // the projectile should not be affected by gravity
        body.setGravityScale(0.0f);

        // apply an impulse to the body so it flies in the direction we chose
        Vector2 impulse = new Vector2(direction).scl(0.001f);
        body.applyLinearImpulse(impulse, body.getPosition(), true);

        // CollisionHandler Identifier
        fix.setUserData("Projectile");
        shape.dispose();
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
        if (!exploded) {
            exploded = true;
            world.addExplosion(body.getPosition(), 0.2f);

            world.forgetAfterUpdate(this);
            world.advanceGameState();
        }
    }
}
