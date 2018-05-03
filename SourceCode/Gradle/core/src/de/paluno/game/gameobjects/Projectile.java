package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.screens.PlayScreen;

public class Projectile implements Updatable, PhysicsObject, Renderable {

    // in meters
    private static final float PROJECTILE_RADIUS = 3.0f;
    private static final float PROJECTILE_DENSITY = 10.0f;

    private PlayScreen playScreen;
    private Vector2 origin;
    private Vector2 direction;

    private Body body;

    private Texture texture;
    private Sprite sprite;

    public Projectile(PlayScreen playScreen, Vector2 origin, Vector2 direction) {
        this.playScreen = playScreen;
        this.origin = origin;
        this.direction = direction;

        texture = new Texture(Gdx.files.internal("Projectile.png"));
        sprite = new Sprite(texture);

        sprite.setOriginCenter();
    }

    @Override
    public void update(float delta, GameState gameState) {

    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        Vector2 position = Constants.getScreenSpaceVector(body.getPosition());

        sprite.setOriginBasedPosition(position.x, position.y);
        sprite.draw(batch);
    }

    @Override
    public void setupBody() {
        World world = playScreen.getWorld();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Vector2 position = Constants.getWorldSpaceVector(origin);
        bodyDef.position.set(position.x, position.y);
        bodyDef.bullet = true;

        CircleShape shape = new CircleShape();
        shape.setRadius(PROJECTILE_RADIUS * Constants.WORLD_SCALE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = PROJECTILE_DENSITY;

        body = world.createBody(bodyDef);

        body.createFixture(fixtureDef);

        body.setGravityScale(0.0f);
        body.setUserData(this);

        body.applyLinearImpulse(direction, body.getPosition(), true);

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

    }
}
