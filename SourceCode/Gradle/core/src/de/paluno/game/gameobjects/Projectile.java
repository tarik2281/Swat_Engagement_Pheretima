package de.paluno.game.gameobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.WeaponType;

public class Projectile implements Updatable, PhysicsObject, Renderable {

    // in meters
    private static final float PROJECTILE_RADIUS = 0.03f;
    private static final float PROJECTILE_DENSITY = 0.1f;

    private World world;
    private Vector2 position;
    private Vector2 direction;
    private WeaponType weaponType;
    private Body body;

    private Texture texture;
    private Sprite sprite;

    private boolean exploded = false;

    private Worm shootingWorm;
    private boolean wormContactEnded = false;

    private float timer = 0.0f;

    public Projectile(World world, Worm shootingWorm, WeaponType weaponType, Vector2 position, Vector2 direction) {
        this.world = world;
        this.position = position;
        this.direction = direction;

        this.weaponType = weaponType;

        texture = world.getAssetManager().get(weaponType.getProjectileAsset());
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
        timer += delta;

        // check if the projectile is inside our world - if not, destroy it
        if (!world.getWorldBounds().contains(body.getPosition()) || (weaponType == WeaponType.WEAPON_GRENADE && timer >= 3.0f))
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

        switch (weaponType) {
            case WEAPON_BAZOOKA:
                Vector2 direction = new Vector2(-body.getLinearVelocity().x, body.getLinearVelocity().y);
                float angle = direction.angle(new Vector2(0, 1));
                sprite.setRotation(angle);
                break;
            case WEAPON_GRENADE:
                sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
                break;
        }

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

        if (weaponType == WeaponType.WEAPON_GUN) {
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


        } else if (weaponType == WeaponType.WEAPON_BAZOOKA) {
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 2;
            body = world.createBody(bodyDef);
            Fixture fix = body.createFixture(fixtureDef);
            body.setGravityScale(1.0f);
            Vector2 impulse = new Vector2(direction).scl(body.getMass() * 7.0f);
            body.applyLinearImpulse(impulse, body.getPosition(), true);
            fix.setUserData("Projectile");

        } else if (weaponType == WeaponType.WEAPON_GRENADE) {

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 9;
            fixtureDef.friction = 0.0f;
            fixtureDef.restitution = 0.5f;
            body = world.createBody(bodyDef);
            Fixture fix = body.createFixture(fixtureDef);
            body.setGravityScale(1.0f);
            body.setAngularDamping(2.0f);
            Vector2 impulse = new Vector2(direction).scl(5.0f * body.getMass());
            body.applyLinearImpulse(impulse, body.getPosition(), true);
            body.applyAngularImpulse(-0.01f * body.getMass(), true);
            fix.setUserData("Projectile");
        }
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

    public boolean explodeOnCollision() {
        return weaponType != WeaponType.WEAPON_GRENADE;
    }

    public void explode() {
        if (!exploded) {
            exploded = true;
            world.addExplosion(body.getPosition(), 0.3f);

            world.forgetAfterUpdate(this);
            world.advanceGameState();
        }
    }

	public void setCloningParameters(Projectile clone) {
		// TODO Auto-generated method stub

		this.body = clone.body;
		this.world = clone.world;

		this.texture= clone.texture;
		this.sprite= clone.sprite;
		this.position= clone.position;
		this.direction= clone.direction;
	}
}
