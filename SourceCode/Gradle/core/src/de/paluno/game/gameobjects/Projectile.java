package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.UserData;

import java.util.ArrayList;

public class Projectile implements Updatable, PhysicsObject, Renderable {

    public static class SnapshotData {

        private Vector2 position;
        private Vector2 direction;
        private WeaponType weaponType;

        private int playerNumber;
        private int wormNumber;

        public Vector2 getPosition() {
            return position;
        }
    }

    // in meters
    private static final float PROJECTILE_RADIUS = 0.03f;
    private static final float PROJECTILE_DENSITY = 0.1f;
    private static final float BAZOOKA_DENSITY = 0.07f;
    private static final float GRENADE_DENSITY = 0.2f;


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

    private float explosionTimer = 0.0f;

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

    public Projectile(World world, SnapshotData data) {
        this.world = world;
        this.position = data.position;
        this.direction = data.direction;

        this.weaponType = data.weaponType;

        texture = world.getAssetManager().get(weaponType.getProjectileAsset());
        sprite = new Sprite(texture);

        sprite.setOriginCenter();

        this.shootingWorm = world.getWormForPlayer(data.playerNumber, data.wormNumber);
    }

    @Override
    public void update(float delta, GameState gameState) {
        explosionTimer += delta;

        // check if the projectile is inside our world - if not, destroy it
        if (!world.isInWorldBounds(body) || (weaponType == WeaponType.WEAPON_GUN && !world.getWorldBounds().contains(body.getPosition())) ||
                (weaponType.getExplosionTime() > 0.0f && explosionTimer >= weaponType.getExplosionTime()))
            explode(null);

        if (!wormContactEnded) {//  
            float distance = shootingWorm.getBody().getPosition().dst(body.getPosition());
            if (distance > Constants.WORM_RADIUS + PROJECTILE_RADIUS)
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
            case WEAPON_SPECIAL:
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

        //Vector2 impulse = new Vector2(direction).scl(body.getMass() * weaponType.getShootingImpulse());

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
            fix.setUserData(new UserData(UserData.ObjectType.Projectile, this));
        } else if (weaponType == WeaponType.WEAPON_BAZOOKA) {
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = BAZOOKA_DENSITY;
            body = world.createBody(bodyDef);
            Fixture fix = body.createFixture(fixtureDef);
            body.setGravityScale(1.0f);
            Vector2 impulse = new Vector2(direction).scl(body.getMass() * 7.0f);
            body.applyLinearImpulse(impulse, body.getPosition(), true);
            fix.setUserData(new UserData(UserData.ObjectType.Projectile, this));
        } else if (weaponType == WeaponType.WEAPON_GRENADE || weaponType == WeaponType.WEAPON_SPECIAL) {
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = GRENADE_DENSITY;
            fixtureDef.friction = 0.2f;
            fixtureDef.restitution = 0.1f;
            body = world.createBody(bodyDef);
            Fixture fix = body.createFixture(fixtureDef);
            body.setGravityScale(1.0f);
            body.setAngularDamping(2.0f);
            Vector2 impulse = new Vector2(direction).scl(7.0f * body.getMass());
            body.applyLinearImpulse(impulse, body.getPosition(), true);
            body.applyAngularImpulse(-0.01f * body.getMass(), true);
            fix.setUserData(new UserData(UserData.ObjectType.Projectile, this));
        } else if (weaponType == WeaponType.TELEPORTER) {
            Vector3 vector3 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            world.getCamera().getWorldCamera().unproject(vector3);
            world.getCurrentPlayer().getCurrentWorm().getBody().setTransform(vector3.x, vector3.y, 0);
            world.registerAfterUpdate(world.getCurrentPlayer().getCurrentWorm());


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

    public Worm getShootingWorm() {
        return shootingWorm;
    }

    public boolean isWormContactEnded() {
        return wormContactEnded;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public boolean explodeOnCollision() {
        return weaponType.getExplosionTime() == 0.0f;
    }

    public void explode(Worm directHitWorm) {
        if (!exploded) {
            exploded = true;

            if (weaponType.getExplosionRadius() == 0.0f) {
                if (directHitWorm != null)
                    directHitWorm.takeDamage(Math.round(weaponType.getDamage()));
            } else {
                ArrayList<Worm> affectedWorms = world.addExplosion(new Explosion(body.getPosition(),
                        weaponType.getExplosionRadius(), weaponType.getExplosionBlastPower()));

                for (Worm worm : affectedWorms) {
                    worm.takeDamage((int) weaponType.getDamage());

                    if (weaponType == WeaponType.WEAPON_SPECIAL)
                        worm.setIsInfected(true);
                }
            }

            world.forgetAfterUpdate(this);
            world.advanceGameState();
        }
    }

    public SnapshotData makeSnapshot() {
        SnapshotData data = new SnapshotData();

        data.position = new Vector2(position);
        data.direction = new Vector2(direction);
        data.weaponType = weaponType;
        data.playerNumber = shootingWorm.getPlayerNumber();
        data.wormNumber = shootingWorm.getCharacterNumber();

        return data;
    }
}
