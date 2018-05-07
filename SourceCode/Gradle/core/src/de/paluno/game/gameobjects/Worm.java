package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.paluno.game.AnimatedSprite;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.screens.PlayScreen;

public class Worm implements Updatable, PhysicsObject, Renderable {
	private boolean standsOnGround;
	private PlayScreen screen;
	private int playerNumber;
	
	private Body body;
	//private Fixture fixture;
	//private Texture texture;
	
	private Vector2 spawnPosition;
	
	private GameState currentState;
	private AnimatedSprite idleAnimation;
	private AnimatedSprite walkAnimation;
	private AnimatedSprite equipGunAnimation;
	
	private int movement = 0;
	private boolean jump = false;

	private boolean gunEquipped;
	private boolean gunUnequipping;

	private int health;

	public Worm(int num, PlayScreen screen, Vector2 position) {
		this.playerNumber = num;
		this.screen = screen;
		this.spawnPosition = position;
		
		//this.setupBody();
		
		//this.texture = new Texture(Gdx.files.internal("waccuse1_blank.png"));
		// TODO: load assets with AssetManager
		idleAnimation = new AnimatedSprite(Gdx.files.internal("wbrth1.xml"));
		walkAnimation = new AnimatedSprite(Gdx.files.internal("wwalk.xml"));
		equipGunAnimation = new AnimatedSprite(Gdx.files.internal("whgnlnk.xml"));

		gunEquipped = false;
		gunUnequipping = false;

		health = Constants.WORM_MAX_HEALTH;
	}
	
	public void update(float delta, GameState state) {
		if(this.body == null) return;
		
		this.currentState = state;

        Vector2 currentPos = body.getWorldCenter();

		if(this.jump && this.canJump()) {
		    // TODO: maybe jump and landing animations
			this.body.applyLinearImpulse(0.0f, body.getMass() * Constants.JUMP_VELOCITY,
					currentPos.x, currentPos.y, true);
			this.jump = false;
		}

		// http://www.iforce2d.net/b2dtut/constant-speed
		Vector2 currentVel = body.getLinearVelocity();
		float desiredVel = 0.0f;

		switch (movement) {
            case Constants.MOVEMENT_LEFT:
                desiredVel = -Constants.MOVE_VELOCITY;
                break;
            case Constants.MOVEMENT_NO_MOVEMENT:
                desiredVel = 0.0f;
                break;
            case Constants.MOVEMENT_RIGHT:
                desiredVel = Constants.MOVE_VELOCITY;
                break;
        }

        float velChange = desiredVel - currentVel.x;
		float impulse = body.getMass() * velChange;
		this.body.applyLinearImpulse(impulse, 0.0f, currentPos.x, currentPos.y, true);
		
		//TODO: boundary check?
		if (!screen.getWorldBounds().contains(body.getPosition()))
			die();

		// Old version
		/*if(this.movement != Constants.MOVEMENT_NO_MOVEMENT) {
			Vector2 currentPos = this.body.getPosition();
			Vector2 currentVel = this.body.getLinearVelocity();

			float impulse = body.getMass() * 10.0f * Constants.WORLD_SCALE;
			
			if(movement == Constants.MOVEMENT_RIGHT && currentVel.x < Constants.MOVE_VELOCITY) {
				if(currentPos.x > 0) this.body.applyLinearImpulse(impulse, 0.0f, currentPos.x, currentPos.y, true);
				else this.body.setLinearVelocity(0.0f, currentVel.y);
			}
			else if(movement == Constants.MOVEMENT_LEFT && currentVel.x > -Constants.MOVE_VELOCITY) {
				if(currentPos.x < 100) this.body.applyLinearImpulse(-impulse, 0.0f, currentPos.x, currentPos.y, true);
				else this.body.setLinearVelocity(0.0f, currentVel.y);
			}
		}*/
	}
	
	public void render(SpriteBatch batch, float delta) {
		if(this.body == null) return;
		
		Vector2 currentPos = Constants.getScreenSpaceVector(this.body.getPosition());

        AnimatedSprite sprite = null;

        if (movement == Constants.MOVEMENT_NO_MOVEMENT) {
            if (gunUnequipping && equipGunAnimation.isAnimationFinished()) {
                gunEquipped = false;
                gunUnequipping = false;
                equipGunAnimation.reset();
            }

            if (gunEquipped) {
                sprite = equipGunAnimation;
            }
            else {
                sprite = idleAnimation;
            }
        }
        else {
            sprite = walkAnimation;
            // walk animation is only for moving left, so flip it
            walkAnimation.setFlipX(movement == Constants.MOVEMENT_RIGHT);
        }

        sprite.setPosition(currentPos);
        sprite.draw(batch, delta);
		//batch.draw(this.texture, currentPos.x, currentPos.y);
	}
	
	public void setupBody() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(this.spawnPosition.x, this.spawnPosition.y);
		bodyDef.type = BodyType.DynamicBody;
		
		this.body = this.screen.getWorld().createBody(bodyDef);
		body.setFixedRotation(true);
		
		PolygonShape bodyRect = new PolygonShape();
		// TODO: hardcoded worm hitbox size
		bodyRect.setAsBox(18.0f / 2 * Constants.WORLD_SCALE, 25.0f / 2.0f * Constants.WORLD_SCALE);
		// TODO: Maybe finetune hitbox
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = bodyRect;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 0.0f;
		
		Fixture fix = this.body.createFixture(fixtureDef);
		// CollisionHandler Identifier
		fix.setUserData("Worm");
		
		bodyRect.dispose();
	}
	
	public Body getBody() {
		return this.body;
	}
	
	public void setBodyToNullReference() {
		//this.fixture = null;
		this.body = null;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public void equipGun() {
	    gunEquipped = true;
    }

    public void unequipGun() {
        gunUnequipping = true;
        equipGunAnimation.reverse();
    }

	public void takeDamage(int damage) {
		health -= damage;

		if (health <= 0) {
			health = 0;
			die();
		}
	}

	public int getHealth() {
		return health;
	}

	public void die() {
	    // TODO: proper object removing through PlayScreen
		screen.forgetAfterUpdate(this);
		screen.wormDied(this); // TODO: use EventManager to handle worm death
		/*this.screen.getWorld().destroyBody(this.body);
		this.setBodyToNullReference();
		if(this.playerNumber == 1) {
			this.screen.setGameState(GameState.GAMEOVERPLAYERTWOWON);
		} else {
			this.screen.setGameState(GameState.GAMEOVERPLAYERONEWON);
		}*/
	}
	
	public boolean canJump() {
		return this.isStandsOnGround() && (
				(this.playerNumber == Constants.PLAYER_NUMBER_1 && this.currentState == GameState.PLAYERONETURN)
				||
				(this.playerNumber == Constants.PLAYER_NUMBER_2 && this.currentState == GameState.PLAYERTWOTURN));
	}
	
	public boolean isStandsOnGround() {
		return this.standsOnGround;
	}
	
	public void setStandsOnGround(boolean onGround) {
		this.standsOnGround = onGround;
	}
	
	public void setMovement(int newMovementCode) {
	    if (movement == newMovementCode)
	        return;
		this.movement = newMovementCode;

		// reset all animations since they will change
		idleAnimation.reset();
		walkAnimation.reset();
		equipGunAnimation.reset();
		// TODO: proper handling of animation switches and resets

		if (Constants.MOVEMENT_NO_MOVEMENT != movement) {
		    idleAnimation.setFlipX(movement == Constants.MOVEMENT_RIGHT);
		    walkAnimation.setFlipX(movement == Constants.MOVEMENT_RIGHT);
		    equipGunAnimation.setFlipX(movement == Constants.MOVEMENT_RIGHT);
        }
	}

	public int getMovement() {
	    return movement;
    }

	public void setJump(boolean newJump) {
		this.jump = newJump;
	}
	
	public boolean getJump() {
		return this.jump;
	}
}
