package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.paluno.game.AnimatedSprite;
import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.WeaponType;
import de.paluno.game.screens.PlayScreen;

import java.util.ArrayList;

public class Worm implements Updatable, PhysicsObject, Renderable {

    private int characterNumber;

	private World world;
	private Body body;
	private Player player;

	private Vector2 spawnPosition;

	private AnimatedSprite currentAnimation;
	private AnimatedSprite idleAnimation;
	private AnimatedSprite walkAnimation;
	private AnimatedSprite jumpAnimation;
	private AnimatedSprite weaponAnimation;
	
	private int movement = Constants.MOVEMENT_NO_MOVEMENT;
	private int orientation;
	private boolean jump = false;
	private int groundContacts = 0;

	private boolean gunUnequipping = false;

	private int health;

	private Weapon currentWeapon = null;

	/*public Worm(int num, PlayScreen screen, Vector2 position) {
		// Set given starting parameters
		this.playerNumber = num;
		this.world = world;
		this.spawnPosition = position;
		
		// Body will be setup from PlayScreen
		
		// Setup animation Sprites once for later use
		idleAnimation = new AnimatedSprite(screen.getAssetManager().get(Assets.wormBreath));
		walkAnimation = new AnimatedSprite(screen.getAssetManager().get(Assets.wormWalk));
		equipGunAnimation = new AnimatedSprite(screen.getAssetManager().get(Assets.wormEquipGun));

		// By default no Worm has a weapon equipped, until it's officially his turn
		gunEquipped = false;
		gunUnequipping = false;

		// And of course we have limited health
		health = Constants.WORM_MAX_HEALTH;

		// setup the animation
		updateAnimation();
	}*/
	/**
	 * Empty constructor for cloning purposes
	 */
	public Worm() {}
	/**
	 * Constructor
	 * @param player - reference to the player we belong to
	 * @param charNum - Our character number
	 */
	public Worm(Player player, int charNum) {
	    characterNumber = charNum;

		// Link references
		this.player = player;
		this.world = player.getWorld();

		// Set a random spawning direction, so not every Worm looks the same
		int o = Math.round((float)Math.random());
		if(o == 0) this.orientation = Constants.WORM_DIRECTION_LEFT;
		else this.orientation = Constants.WORM_DIRECTION_RIGHT;

		// Load animations
		this.walkAnimation = new AnimatedSprite(player.getAssets().get(Assets.wormWalk));
		this.idleAnimation = new AnimatedSprite(player.getAssets().get(Assets.wormBreath));
		//this.jumpAnimation = new AnimatedSprite(this.assets.get(Assets.wormJump));

		// Get our spawning position
		this.spawnPosition = world.generateSpawnPosition();

		// Health is limited
		this.health = Constants.WORM_MAX_HEALTH;

		// Finally setup Animations
		updateAnimation();
	}
	
	/**
	 * Handler method for Game Loop's Update phase
	 * @param delta - Time since last update in seconds
	 * @param state - GameState we are in this round
	 */
	public void update(float delta, GameState state) {
		// No body anymore? Shouldn't happen, catch
		if(this.body == null) return;

        // Now we apply movements - therefor we need our current position
		Vector2 currentPos = body.getWorldCenter();

		if(this.jump) {
			if (canJump()) {
				// We shall jump - AND are allowed to - so let's apply some vertical impulse
				// TODO: maybe jump and landing animations
				this.body.applyLinearImpulse(0.0f, body.getMass() * Constants.JUMP_VELOCITY,
						currentPos.x, currentPos.y, true);
			}
		}
		// Whether or not we actually jumped, remove the order.
		if(getJump()) this.jump = false;

		// Now we calculate the new movement speed based on current movement impulses
		// http://www.iforce2d.net/b2dtut/constant-speed
		Vector2 currentVel = body.getLinearVelocity();
		float desiredVel = 0.0f;

		switch (movement) {
            case Constants.MOVEMENT_LEFT:
                desiredVel = -Constants.MOVE_VELOCITY;
                break;
            case Constants.MOVEMENT_NO_MOVEMENT:
            default:
                //if (isStandsOnGround())
                    desiredVel = 0.0f;
                //else
                    //desiredVel = (orientation == Constants.WORM_DIRECTION_LEFT) ? -Constants.MOVE_VELOCITY : Constants.MOVE_VELOCITY;
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
		if (!world.getWorldBounds().contains(body.getPosition())) die();
	}
	
	/**
	 * Handler method for Game Loop's Render phase
	 * @param batch - Reference to our SpriteBatch used for rendering
	 * @param delta - Time since last update in seconds
	 */
	public void render(SpriteBatch batch, float delta) {
		// No body? Shouldn't happen, catch
		if(this.body == null) return;
		
		// Again we need the current position, so we know where to draw our animations
		// (Based on screen size <-> world size calculations)
		Vector2 currentPos = Constants.getScreenSpaceVector(this.body.getPosition());

		if (currentAnimation != null) {
			if (gunUnequipping && currentAnimation.isAnimationFinished()) {
				// The weapon should be uneuqipping and is finished with that - update
				currentWeapon = null;
				gunUnequipping = false;
				updateAnimation();
			}

			// And finally draw it
			currentAnimation.setPosition(currentPos);
			currentAnimation.draw(batch, delta);
		}
	}

	/**
	 * Method to setup our actual physics body
	 */
	@Override
	public void setupBody() {
		// Blueprint with spawning position and BodyType
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(this.spawnPosition.x, this.spawnPosition.y);
		bodyDef.type = BodyType.DynamicBody;
		
		// Create the actual physics body in our current game world
		this.body = world.createBody(bodyDef);
		body.setFixedRotation(true);
		
		// Now we add some hitboxes - Worm is easy, just a rectangle
        CircleShape shape = new CircleShape();
        shape.setRadius(Constants.WORM_HEIGHT / 2.0f);
		PolygonShape bodyRect = new PolygonShape();
		bodyRect.setAsBox(Constants.WORM_WIDTH / 2.0f, Constants.WORM_HEIGHT / 2.0f);

		// And some physics settings
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = bodyRect;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.0f;
		
		// Create, apply, done
		Fixture fix = this.body.createFixture(fixtureDef);
		// CollisionHandler Identifier
		fix.setUserData("Worm");
		
		// Get rid of temporary material properly
		bodyRect.dispose();
	}

	public void equipWeapon(Weapon weapon) {
	    currentWeapon = weapon;
	    weaponAnimation = weapon.createAnimatedSprite();

	    gunUnequipping = false;
	    updateAnimation();
    }

	/**
	 * Getter method for our physics body
	 * @return body
	 */
	public Body getBody() {return this.body;}
	/**
	 * Soft setter method for our physics body - set to null on death
	 */
	public void setBodyToNullReference() {this.body = null;}
	/**
	 * Getter reference to parents (player) player Number
	 * @return player.playerNumber
	 */
	public int getPlayerNumber() {return this.player.getPlayerNumber();}
	/**
	 * Soft setter method for animation state - reverse gun animation
	 */
    public void unequipGun() {
        if (currentWeapon != null) {
			gunUnequipping = true;
			updateAnimation();
		}
    }

    /**
     * Damage handler method - calculate remaining life and death
     * @param damage - The damage taken as integer
     */
	public void takeDamage(int damage) {
		health -= damage;

		if (health <= 0) {
			// Is ded, kill it
			health = 0;
			die();
		}
	}
	/**
	 * Getter method for our character's health
	 * @return health
	 */
	public int getHealth() {return health;}

	/**
	 * Method to handle characters death - cleanup and stuff
	 */
	public void die() {
		//Tell the screen - there, where all the magic happens - that this character is no more
		//screen.forgetAfterUpdate(this);
		//screen.wormDied(this);
		this.player.characterDied(this.characterNumber);
		//this.setBodyToNullReference();
	}
	
	/**
	 * Getter method for character's jump status
	 * @return Character on ground and allowed to move?
	 */
	public boolean canJump() {
		return this.isStandsOnGround() &&
				this.player.isPlayerTurn() &&
				this.characterNumber == this.player.getTurn();
	}
	/**
	 * Getter method for character's shoot status
	 * @return Character allowed to shoot?
	 */
	public boolean canShoot() {
		return this.player.isPlayerTurn() &&
				this.characterNumber == this.player.getTurn();
	}
	
	/**
	 * Getter method for character's ground status
	 * @return standsOnGround
	 */
	public boolean isStandsOnGround() {
	    if (getBody() == null) return true;
	    return getBody().getLinearVelocity().y > -0.1f && getBody().getLinearVelocity().y < 0.1f;
	}

	public void beginContact() {
		boolean stoodOnGround = isStandsOnGround();

		groundContacts++;

		if (!stoodOnGround)
			updateAnimation();
	}

	public void endContact() {
	    if (--groundContacts < 0)
	        throw new IllegalStateException("Already ended all contacts");

		if (!isStandsOnGround())
			updateAnimation();
	}

	/**
	 * Setter method for character's movement orders and orientation
	 * @param newMovementCode - Constant based movement code for left, right or stop
	 */
	public void setMovement(int newMovementCode) {
		// Same code? Nothing to go here!
		if (movement == newMovementCode) return;

		this.movement = newMovementCode;

		if (isStandsOnGround() && movement != Constants.MOVEMENT_NO_MOVEMENT) {
			// The new movementCode is a move-order? Update orientation
			switch (movement) {
				case Constants.MOVEMENT_LEFT:
					orientation = Constants.WORM_DIRECTION_LEFT;
					break;
				case Constants.MOVEMENT_RIGHT:
					orientation = Constants.WORM_DIRECTION_RIGHT;
					break;
			}
		}

		// If we got this far - something WILL change, so reset the animations just in case
		updateAnimation();
	}

	/**
	 * Method to update our character's animation state
	 */
	private void updateAnimation() {
		/*switch (movement) {
			case Constants.MOVEMENT_LEFT:
			case Constants.MOVEMENT_RIGHT:
				// We have some movement, show that
				currentAnimation = walkAnimation;
				break;
			case Constants.MOVEMENT_NO_MOVEMENT:
				// No movement order - that means we either just idle or, if it's our turn, have a weapon equipped
				if (gunEquipped)
					// Gun should be equipped? Show that animation
					currentAnimation = equipGunAnimation;
				else
					// No weapon? Idle it is then
					currentAnimation = this.assets.getAnimation('idle');
				break;
		}*/

		if(!isStandsOnGround()) currentAnimation = jumpAnimation;
		else if(movement != Constants.MOVEMENT_NO_MOVEMENT) currentAnimation = walkAnimation;
		else {
			if(getCurrentWeapon() != null) currentAnimation = weaponAnimation;
			else currentAnimation = idleAnimation;
		}

		// flip the animation if needed since the animations are only for the left direction
		if (currentAnimation != null) {
			currentAnimation.setOrientation(orientation);
			currentAnimation.reset();

			if (currentWeapon != null && gunUnequipping)
				currentAnimation.reverse();
		}
	}

	/**
	 * Getter method for character's current movement order
	 * @return movement
	 */
	public int getMovement() {return movement;}
	/**
	 * Setter method for character's jump order
	 * @param newJump - Shall character jump?
	 */
	public void setJump(boolean newJump) {this.jump = newJump;}
	/**
	 * Getter method for character's current jump order
	 * @return jump
	 */
	public boolean getJump() {return this.jump;}
	/**
	 * Getter method for character's current orientation
	 * @return orientation
	 */
	public int getOrientation() {return this.orientation;}
	/**
	 * Getter method for global Asset Manager
	 * @return AssetManager
	 */
	public AssetManager getAssets() {return player.getAssets();}
	/**
	 * Getter method for the currently selected weapon
	 * @return current Weapon
	 */
	public Weapon getCurrentWeapon() {return currentWeapon;}

	/**
	 * Passthrough method to give the shoot order to the currently selected weapon, if any and allowed
	 */
	public void shoot() {
	    if(canShoot() && currentWeapon != null)
	        currentWeapon.shoot();
	}
	
	/**
	 * Method to return a clone of this object
	 * @return clone
	 */
	public Worm clone() {
		Worm clone = new Worm();
		clone.setCloningParameters(this);
		return clone;
	}
	/**
	 * Method to copy over all variables from a second Worm - used for cloning
	 * @param copy - The reference to the Worm to copy from
	 */
	public void setCloningParameters(Worm copy) {
		this.characterNumber = copy.characterNumber;

		this.world = copy.world;
		this.body = copy.body;
		this.player = copy.player;

		this.spawnPosition = copy.spawnPosition;

		this.currentAnimation = copy.currentAnimation;
		this.idleAnimation = copy.idleAnimation;
		this.walkAnimation = copy.walkAnimation;
		this.jumpAnimation = copy.jumpAnimation;

		this.movement = copy.movement;
		this.orientation = copy.orientation;
		this.jump = copy.jump;

		this.gunUnequipping = copy.gunUnequipping;

		this.health = copy.health;

		this.currentWeapon = copy.currentWeapon;
}

    /**
	 * Method to call the WeaponsSelector's show() method, to make it visible and be able to select weapons
	 */
	public void showWeaponSelector() {
		//this.selector.show();
	}
}
