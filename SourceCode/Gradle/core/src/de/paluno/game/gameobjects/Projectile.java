package de.paluno.game.gameobjects;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.EventManager;
import de.paluno.game.GameState;
import de.paluno.game.UserData;
import de.paluno.game.screens.Loadable;

import java.util.ArrayList;

public class Projectile extends WorldObject {

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
    private static final float AIRSTRIKE_DENSITY = 1f;
    

    private int id;
    private Vector2 position;
    private Vector2 direction;
    private WeaponType weaponType;

    private Texture texture;
    private Sprite sprite;

    private boolean exploded = false;

    private Worm shootingWorm;
    private boolean wormContactEnded = false;

    private float explosionTimer = 0.0f;
    private Explosion explosion;

    public Projectile(Worm shootingWorm, WeaponType weaponType, Vector2 position, Vector2 direction) {
        this.position = position;
        this.direction = direction;

        this.weaponType = weaponType;

        this.shootingWorm = shootingWorm;
    }

	public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void setupAssets(AssetManager manager) {
        texture = manager.get(weaponType.getProjectileAsset());
        sprite = new Sprite(texture);
        sprite.setOriginCenter();
    }

    @Override
    public void update(float delta) {
        explosionTimer += delta;

        // check if the projectile is inside our world - if not, destroy it

        if ( //(weaponType == WeaponType.WEAPON_GUN && !world.getWorldBounds().contains(getBody().getPosition())) ||
                (weaponType.getExplosionTime() > 0.0f && explosionTimer >= weaponType.getExplosionTime()))
            explode(null, true, false);
            
        if(!getWorld().isInWorldBounds(getBody())) {
        	explode(null, false, false);
        }
        if (!wormContactEnded) {//  
        	if (shootingWorm != null) {
	            float distance = shootingWorm.getPosition().dst(getPosition());
	            if (distance > Constants.WORM_RADIUS + PROJECTILE_RADIUS)
	                wormContactEnded = true;
        	}
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        Vector2 position = Constants.getScreenSpaceVector(getPosition());

        sprite.setOriginBasedPosition(position.x, position.y);

        switch (weaponType) {
            case WEAPON_BAZOOKA:
            case WEAPON_AIRSTRIKE:
                Vector2 direction = new Vector2(-getVelocity().x, getVelocity().y);
                float angle = direction.angle(new Vector2(0, 1));
                sprite.setRotation(angle);
                break;
            case WEAPON_GRENADE:
            case WEAPON_SPECIAL:
                sprite.setRotation(getBody().getAngle() * MathUtils.radiansToDegrees);
                break;
        }
        sprite.draw(batch);
    }

    @Override
    public Body onSetupBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y);
        bodyDef.bullet = true;

        CircleShape shape = new CircleShape();
        shape.setRadius(PROJECTILE_RADIUS);

        //Vector2 impulse = new Vector2(direction).scl(body.getMass() * weaponType.getShootingImpulse());

        Body body = null;
        if (weaponType == WeaponType.WEAPON_GUN) {
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = PROJECTILE_DENSITY;

            body = world.createBody(bodyDef);

            Fixture fix = body.createFixture(fixtureDef);

            // the projectile should not be affected by gravity
            body.setGravityScale(0.0f);

            // apply an impulse to the body so it flies in the direction we chose
            Vector2 impulse = new Vector2(direction).scl(0.0015f);
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
        }
        else if (weaponType == WeaponType.WEAPON_AIRSTRIKE) {
        	FixtureDef fixtureDef = new FixtureDef();
        	fixtureDef.shape = shape;
        	fixtureDef.density = AIRSTRIKE_DENSITY;
        	body = world.createBody(bodyDef);
        	Fixture fix = body.createFixture(fixtureDef);
        	body.setGravityScale(0.0f);
        	body.setAngularDamping(2.0f);
        	Vector2 impulse = new Vector2(direction).scl(6.0f * body.getMass());
        	body.applyLinearImpulse(impulse, body.getPosition(), true);
        	body.applyAngularImpulse(-0.01f * body.getMass(),  true);
        	fix.setUserData(new UserData(UserData.ObjectType.Projectile, this));
        }
        
        shape.dispose();

        return body;
    }

    public Worm getShootingWorm() {
        return shootingWorm;
    }

    public boolean isWormContactEnded() {
        return wormContactEnded;
    }

    public WeaponType getWeaponType(){
        return weaponType;
    }

    public boolean explodeOnCollision() {
        return weaponType.getExplosionTime() == 0.0f;
    }

    public Explosion getExplosion() {
        return explosion;
    }

    public void explode(Worm directHitWorm, boolean collidedObject, boolean collidedHead) {
        if (!exploded) {
        	
            exploded = true;
            explosion = new Explosion(getPosition(), weaponType.getExplosionRadius(), weaponType.getExplosionBlastPower());
            EventManager.getInstance().queueEvent(EventManager.Type.ProjectileExploded, this);

            switch (weaponType) {
            case WEAPON_GUN:
            	break;
            case WEAPON_BAZOOKA:
            	break;
            case WEAPON_GRENADE:
            	break;
            case WEAPON_SPECIAL:
            	break;
            case WEAPON_AIRSTRIKE:
            	break;
            }
            
            if (weaponType.getExplosionRadius() == 0.0f) {
                if (directHitWorm != null)
                    directHitWorm.takeDamage(Math.round(weaponType.getDamage()), Constants.DAMAGE_TYPE_PROJECTILE);
                if(collidedHead == true)
                	directHitWorm.takeDamage(Constants.HEADSHOT_DAMAGE, Constants.DAMAGE_TYPE_PROJECTILE);
                if(directHitWorm == null)
                	EventManager.getInstance().queueEvent(EventManager.Type.AirBall, this);
                
            }
   
            else if (collidedObject) {
                ArrayList<Worm> affectedWorms = getWorld().addExplosion(explosion);
                
                if(affectedWorms.isEmpty()) {
                	EventManager.getInstance().queueEvent(EventManager.Type.AirBall, this);
                }
                
                for (Worm worm : affectedWorms) {
                    worm.takeDamage((int)weaponType.getDamage(), Constants.DAMAGE_TYPE_PROJECTILE);
                
                    if (weaponType == WeaponType.WEAPON_SPECIAL)
                        worm.setIsInfected(true);
                }
            }

            //removeFromWorld();
            //getWorld().forgetAfterUpdate(this);
            //world.advanceGameState();
        }
    }

    public SnapshotData makeSnapshot() {
		SnapshotData data = new SnapshotData();

		data.position= new Vector2(position);
		data.direction= new Vector2(direction);
		data.weaponType = weaponType;
		data.playerNumber = shootingWorm.getPlayerNumber();
		data.wormNumber = shootingWorm.getCharacterNumber();

		return data;
	}
}
