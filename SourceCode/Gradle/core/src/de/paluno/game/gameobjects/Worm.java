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
	
	private Vector2 spawnPosition;
	
	private GameState currentState;
	private AnimatedSprite idleAnimation;
	private AnimatedSprite walkAnimation;
	private AnimatedSprite equipGunAnimation;
	
	private int movement = 0;
	private int orientation = Constants.WORM_DIRECTION_LEFT;
	private boolean jump = false;

	private boolean gunEquipped;
	private boolean gunUnequipping;

	private int health;

	public Worm(int num, PlayScreen screen, Vector2 position) {
		// Set given starting parameters
		this.playerNumber = num;
		this.screen = screen;
		this.spawnPosition = position;
		
		// Body will be setup from PlayScreen
		
		// Setup animation Sprites once for later use
		// TODO: load assets with AssetManager
		idleAnimation = new AnimatedSprite(Gdx.files.internal("wbrth1.xml"));
		walkAnimation = new AnimatedSprite(Gdx.files.internal("wwalk.xml"));
		equipGunAnimation = new AnimatedSprite(Gdx.files.internal("whgnlnk.xml"));

		// By default no Worm has a weapon equipped, until it's officially his turn
		gunEquipped = false;
		gunUnequipping = false;

		// And of course we have limited health
		health = Constants.WORM_MAX_HEALTH;
	}
	
	public void update(float delta, GameState state) {
		/** Update method - apply all logic and physics changes */
		// No body anymore? Shouldn't happen, catch
		if(this.body == null) return;
		
		// Update gamestate
		this.currentState = state;

        // Now we apply movements - therefor we need our current position
		Vector2 currentPos = body.getWorldCenter();

		if(this.jump && this.canJump()) {
		    // We shall jump - AND are allowed to - so let's apply some vertical impulse
			// TODO: maybe jump and landing animations
			this.body.applyLinearImpulse(0.0f, body.getMass() * Constants.JUMP_VELOCITY,
					currentPos.x, currentPos.y, true);
			// Jumping is a one-time action, so remove the order
			this.jump = false;
		}

		// Now we calculate the new movement speed based on current movement impulses
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
        // Finally we calculate the actual impulse force, based on body mass
		float impulse = body.getMass() * velChange;
		this.body.applyLinearImpulse(impulse, 0.0f, currentPos.x, currentPos.y, true);
		
		// Worm fell off the world rim? Is ded.
		if (!screen.getWorldBounds().contains(body.getPosition())) die();
	}
	
	public void render(SpriteBatch batch, float delta) {
		/** render method - make it nicely visual */
		// No body? Shouldn't happen, catch
		if(this.body == null) return;
		
		// Again we need the current position, so we know where to draw our animations
		// (Based on real size <-> world size calculations)
		Vector2 currentPos = Constants.getScreenSpaceVector(this.body.getPosition());

        AnimatedSprite sprite = null;

        if (movement == Constants.MOVEMENT_NO_MOVEMENT) {
            // No movement order - that means we either just idle or, if it's our turn, have a weapon equipped
        	if (gunUnequipping && equipGunAnimation.isAnimationFinished()) {
                // The weapon should be uneuqipping and is finished with that - update
        		gunEquipped = false;
                gunUnequipping = false;
                equipGunAnimation.reset();
            }

            if (gunEquipped) {
                // Gun should be equipped? Show that animation
            	sprite = equipGunAnimation;
            }
            else {
                // No weapon? Idle it is then
            	sprite = idleAnimation;
            }
        }
        // Or we have some movement, show that
        else sprite = walkAnimation;

        // And finally draw it
        sprite.setPosition(currentPos);
        sprite.draw(batch, delta);
	}
	
	public void setupBody() {
		/** Setup method - create the physics body of the character */
		// Blueprint with spawning position and BodyType
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(this.spawnPosition.x, this.spawnPosition.y);
		bodyDef.type = BodyType.DynamicBody;
		
		// Create the actual physics body in our current game world
		this.body = this.screen.getWorld().createBody(bodyDef);
		body.setFixedRotation(true);
		
		// Now we add some hitboxes - Worm is easy, just a rectangle
		PolygonShape bodyRect = new PolygonShape();
		// TODO: hardcoded worm hitbox size
		bodyRect.setAsBox(18.0f / 2 * Constants.WORLD_SCALE, 25.0f / 2.0f * Constants.WORLD_SCALE);
		// TODO: Maybe finetune hitbox
		
		// And some physics settings
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = bodyRect;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 0.0f;
		
		// Create, apply, done
		Fixture fix = this.body.createFixture(fixtureDef);
		// CollisionHandler Identifier
		fix.setUserData("Worm");
		
		// Get rid of temporary material properly
		bodyRect.dispose();
	}
	
	public Body getBody() {
		/** Getter method - Body */
		return this.body;
	}
	
	public void setBodyToNullReference() {
		/** Null Setter Method - Body (if it's dead, remove it) */
		this.body = null;
	}

	public int getPlayerNumber() {
		/** Getter Method - playerNumber */
		return playerNumber;
	}

	public void equipGun() {
	    /** True Setter Method - equipGun animation */
		gunEquipped = true;
    }

    public void unequipGun() {
        /** Setter Method - unequipGun trigger */
    	gunUnequipping = true;
        equipGunAnimation.reverse();
    }

	public void takeDamage(int damage) {
		/** Calculate new health after hit */
		health -= damage;

		if (health <= 0) {
			// Is ded, kill it
			health = 0;
			die();
		}
	}

	public int getHealth() {
		/** Getter Method - health */
		return health;
	}

	public void die() {
	    /** Death handler */
		//Tell the screen - there, where all the magic happens - that this character is no more
		screen.forgetAfterUpdate(this);
		screen.wormDied(this); // TODO: use EventManager to handle worm death
	}
	
	public boolean canJump() {
		/** Getter Method - Can player jump right now? */
		return this.isStandsOnGround() && (
				(this.playerNumber == Constants.PLAYER_NUMBER_1 && this.currentState == GameState.PLAYERONETURN)
				||
				(this.playerNumber == Constants.PLAYER_NUMBER_2 && this.currentState == GameState.PLAYERTWOTURN));
	}
	
	public boolean isStandsOnGround() {
		/** Getter Method - Does this player stand on the ground right now? */
		return this.standsOnGround;
	}
	
	public void setStandsOnGround(boolean onGround) {
		/** Setter Method - Does player stand on ground? */
		this.standsOnGround = onGround;
	}
	
	public void setMovement(int newMovementCode) {
	    /** Setter Method - apply new movement order and pre-handle animation changes */
		// Same code? Nothing to go here!
		if (movement == newMovementCode) return;
		this.movement = newMovementCode;

		// If we got this far - something WILL change, so reset the animations just in case
		idleAnimation.reset();
		walkAnimation.reset();
		equipGunAnimation.reset();
		// TODO: proper handling of animation switches and resets

		if (Constants.MOVEMENT_NO_MOVEMENT != movement) {
		    // The new movementCode is a move-order? Update orientation and - if needed - flip animations
			if(this.orientation != this.movement) {
				idleAnimation.setFlipX(orientation == Constants.WORM_DIRECTION_RIGHT);
			    walkAnimation.setFlipX(orientation == Constants.WORM_DIRECTION_RIGHT);
			    equipGunAnimation.setFlipX(orientation == Constants.WORM_DIRECTION_RIGHT);
		    }
		    this.orientation = this.movement;
        }
	}

	public int getMovement() {
	    /** Getter Method - Current movement order */
		return movement;
    }

	public void setJump(boolean newJump) {
		/** Setter Method - New jump order */
		this.jump = newJump;
	}
	
	public boolean getJump() {
		/** Getter Method - Current jump order */
		return this.jump;
	}
	
	public int getOrientation() {
		/** Getter Method - Current orientation */
		return this.orientation;
	}
}
