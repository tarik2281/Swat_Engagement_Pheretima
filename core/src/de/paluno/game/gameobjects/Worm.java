package de.paluno.game.gameobjects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.paluno.game.*;
import de.paluno.game.EventManager.Type;
import de.paluno.game.UserData.ObjectType;

public class Worm extends WorldObject {

    /**
     * Inner class to create a copy of the data necessary for the replay
     */
	public static class SnapshotData {
        public int characterNumber;
        private Vector2 position;
        private int health;
        private int orientation;
        private boolean isInfected;
        private boolean isDead;
        private String name;
    }

    public static class DamageEvent {
		private Worm worm;
		private int damage;
		private int damageType;

		private DamageEvent(Worm worm, int damage, int damageType) {
			this.worm = worm;
			this.damage = damage;
			this.damageType = damageType;
		}

		public Worm getWorm() {
			return worm;
		}

		public int getDamage() {
			return damage;
		}

		public int getDamageType() {
			return damageType;
		}
	}

	public static class DeathEvent {
		private Worm worm;
		private int deathType;

		public DeathEvent(Worm worm, int deathType) {
			this.worm = worm;
			this.deathType = deathType;
		}

		public Worm getWorm() {
			return worm;
		}

		public int getDeathType() {
			return deathType;
		}
	}

    private int characterNumber;
	private Player player;

	private AnimatedSprite currentAnimation;
	private AnimatedSprite idleAnimation;
	private AnimatedSprite walkAnimation;
	private AnimatedSprite flyAnimation;
	private AnimatedSprite weaponAnimation;
	
	private int movement = Constants.MOVEMENT_NO_MOVEMENT;
	private int orientation;
	private boolean jump = false;

	private int numContacts = 0;
	private boolean isStatic = false;
	private boolean isPlaying;
	private boolean isInfected = false;
	private boolean isDead = false;
	private boolean createVirusFixture = false;
	private Fixture virusFixture;

	private Weapon currentWeapon = null;
	private boolean gunUnequipping = false;

	private String name;
	private int health;

	private boolean animationInvalidated;

	/**
	 * Empty constructor for testing purposes
	 */
	public Worm() {}

	/**
	 * Constructor
	 * @param player - reference to the player we belong to
	 * @param charNum - Our character number
	 */
	public Worm(Player player, int charNum, String name) {
	    characterNumber = charNum;
	    this.name = name;

		this.player = player;

		this.orientation = Constants.WORM_DIRECTION_LEFT;
		this.health = Constants.WORM_MAX_HEALTH;
		this.animationInvalidated = true;

		addChild(new HealthBar(this));
	}

	public void setFromSnapshot(SnapshotData data) {
		this.characterNumber = data.characterNumber;
		setPosition(data.position);
		this.health = data.health;
		this.orientation = data.orientation;
		this.isInfected = data.isInfected;
		this.isDead = data.isDead;
		this.name = data.name;
	}


	@Override
	public void setupAssets(AssetManager manager) {
		this.walkAnimation = new AnimatedSprite(manager.get(Assets.wormWalk));
		this.idleAnimation = new AnimatedSprite(manager.get(Assets.wormBreath));
		this.flyAnimation = new AnimatedSprite(manager.get(Assets.wormFly));
	}

	public String getName() {
		return name;
	}

	/**
	 * Handler method for Game Loop's Update phase
	 * @param delta - Time since last update in seconds
	 * @param state - GameState we are in this round
	 */
	@Override
	public void update(float delta) {
		// No body anymore? Shouldn't happen, catch
		if(this.getBody() == null) return;

		// Are we supposed to be the new host of a super deadly virus? Create it!
		if (createVirusFixture)
			createVirusFixture(getBody());

        // Now we apply movements - therefore we need our current position
		Vector2 currentPos = getPosition();

		if(this.jump) {
			if (canJump()) {
				EventManager.getInstance().queueEvent(EventManager.Type.WormJumped, this);
				// We shall jump - AND are allowed to - so let's apply some vertical impulse
				this.getBody().applyLinearImpulse(0.0f, getBody().getMass() * Constants.JUMP_VELOCITY,
						currentPos.x, currentPos.y, true);
			}
		}
		// Whether or not we actually jumped, remove the order.
		if(getJump()) this.jump = false;

		// Now we calculate the new movement speed based on current movement impulses
		// http://www.iforce2d.net/b2dtut/constant-speed
        if (isPlaying) {
            Vector2 currentVel = getBody().getLinearVelocity();
            float desiredVel = 0.0f;

            switch (movement) {
                case Constants.MOVEMENT_LEFT:
                    desiredVel = -Constants.MOVE_VELOCITY;
                    break;
                case Constants.MOVEMENT_NO_MOVEMENT:
                default:
                    desiredVel = 0.0f;
                    break;
                case Constants.MOVEMENT_RIGHT:
                    desiredVel = Constants.MOVE_VELOCITY;
                    break;
            }

            float velChange = desiredVel - currentVel.x;
            // Finally we calculate the actual impulse force, based on getBody mass
            float impulse = getBody().getMass() * velChange;
            this.getBody().applyLinearImpulse(impulse, 0.0f, currentPos.x, currentPos.y, true);
        }
		
		// Worm fell off the world rim? Is ded.
		if (getWorld().isUnderWater(this)) {
			die(Constants.DEATH_TYPE_FALL_DOWN);
		}
	}
	
	/**
	 * Handler method for Game Loop's Render phase
	 * @param batch - Reference to our SpriteBatch used for rendering
	 * @param delta - Time since last update in seconds
	 */
	@Override
	public void render(SpriteBatch batch, float delta) {
		// No body? Shouldn't happen, catch
		if(this.getBody() == null) return;

		if (animationInvalidated) {
			updateAnimation();

			if (currentAnimation != null)
				animationInvalidated = false;
		}
		
		// Again we need the current position, so we know where to draw our animations
		// (Based on screen size <-> world size calculations)
		Vector2 currentPos = Constants.getScreenSpaceVector(this.getBody().getPosition());

		if (currentAnimation != null) {
			if (gunUnequipping && currentAnimation.isAnimationFinished()) {
				// The weapon should be unequipping and is finished with that - update
				currentWeapon = null;
				gunUnequipping = false;
				updateAnimation();
			}

			// And finally draw it
			currentAnimation.setPosition(currentPos);
			// Are we infected? Let's show it in a neat, dangerously looking green shade!
			if (isInfected)
				batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
			currentAnimation.draw(batch, delta);
			// In either case, reset our coloring to the default
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
	}

	/**
	 * Method to setup our actual physics body
	 */
	@Override
	public Body onSetupBody(World world) {
		// Blueprint with spawning position and BodyType
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(getPosition());
		bodyDef.type = isStatic ? BodyType.StaticBody : BodyType.DynamicBody;
		
		// Create the actual physics body in our current game world
		Body body = world.createBody(bodyDef);
		body.setFixedRotation(true);
		
		// Now we add some hitboxes - Let's get fancy: two parter with body and feet shape
		CircleShape bodyRect = new CircleShape();
		bodyRect.setRadius(Constants.WORM_RADIUS);
		bodyRect.setPosition(new Vector2(0 * Constants.WORLD_SCALE, -3 * Constants.WORLD_SCALE));
		PolygonShape footRect = new PolygonShape();
		footRect.setAsBox(Constants.WORM_WIDTH / 4.0f, Constants.WORM_WIDTH / 4.0f, new Vector2(0.0f, -Constants.WORM_HEIGHT / 2.0f), 0.0f);
		CircleShape headshot = new CircleShape();
		headshot.setRadius(Constants.HEAD_AREA_RADIUS);
		headshot.setPosition(new Vector2(1 * Constants.WORLD_SCALE, 5 * Constants.WORLD_SCALE));

		// And some physics settings
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = bodyRect;
		fixtureDef.density = 4.5f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 0.0f;
		
		// Create, apply, done
		Fixture fix = body.createFixture(fixtureDef);
		// CollisionHandler Identifier
		fix.setUserData(new UserData(UserData.ObjectType.Worm, this));
		
		// Same for the feet - but they don't act as actual body, but rather as hitbox sensor for movement
		fixtureDef.shape = footRect;
		fixtureDef.isSensor = true;
		fixtureDef.density = 0.0f;
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.0f;
		fix = body.createFixture(fixtureDef);
		fix.setUserData(new UserData(UserData.ObjectType.WormFoot, this));

		// headshot fixture - ibo
		fixtureDef.shape = headshot;
		fixtureDef.isSensor = true;
		fixtureDef.density = 0.0f;
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 0.0f;
		fix = body.createFixture(fixtureDef);
		fix.setUserData(new UserData(UserData.ObjectType.Headshot, this));

		// Infected this round - breed the devastating virus!
		if (isInfected)
			createVirusFixture(body);

		// Get rid of temporary material properly
		bodyRect.dispose();
		footRect.dispose();
		headshot.dispose();

		return body;
	}

	public void invalidateAnimation() {
		animationInvalidated = true;
	}
	
	/**
	 * Method to setup the Fixture of our Virus hitbox sensor for further infection spreading
	 */
	private void createVirusFixture(Body body) {
		CircleShape circle = new CircleShape();
		circle.setRadius(Constants.VIRUS_RADIUS);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.isSensor = true;

		virusFixture = body.createFixture(fixtureDef);
		virusFixture.setUserData(new UserData(ObjectType.Virus, this));
		circle.dispose();
		createVirusFixture = false;
	}

	public boolean isDead() {
		return isDead;
	}

	/**
	 * Setter method for our current playstate - if this worm is playing right now or not
	 * @param isPlaying - Is this worm playing right now?
	 */
	public void setIsPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;

		if (isPlaying) {
			// We're playing now, so wake up!
			setIsStatic(false);
			//if (isInfected)
				// This worm is entering another turn - let the infection do it's thing
			//	takeDamage(Constants.VIRUS_DAMAGE);
		}
	}
	/**
	 * Getter method for current playstate
	 * @return Is this worm playing?
	 */
	public boolean isPlaying() {
		return isPlaying;
	}
	
	/**
	 * Setter method for the static-state of this worm
	 * @param isStatic - Is this worm on pause right now?
	 */
	public void setIsStatic(boolean isStatic) {
		this.isStatic = isStatic;

		// If we're static now, we don't need to listen to any ground contacts anymore
		if (isStatic)
			numContacts = 0;

		if (getBody() != null)
			getBody().setType(isStatic ? BodyType.StaticBody : BodyType.DynamicBody);

		invalidateAnimation();
		//updateAnimation();
	}
	/**
	 * Getter method for current static-state
	 * @return Is this worm on pause right now?
	 */
	public boolean isStatic() {
		return isStatic;
	}
	
	/**
	 * Setter method for the current infection state
	 * @param isInfected - Is this worm infected now?
	 */
	public void setIsInfected(boolean isInfected) {
		if (!this.isInfected && isInfected) {
            EventManager.getInstance().queueEvent(EventManager.Type.WormInfected, this);
            createVirusFixture = true;
		}

		this.isInfected = isInfected;
	}
	/**
	 * Getter method for the current infection state of this worm
	 * @return Is this worm infected?
	 */
	public boolean isInfected() {
		return isInfected;
	}
	
	/**
	 * Method to equip a given weapon
	 * @param weapon - The weapon to equip, handled in Player
	 */
	public void equipWeapon(Weapon weapon) {
		EventManager.getInstance().queueEvent(Type.WormEquipWeapon, weapon);

		currentWeapon = weapon;
		weaponAnimation = weapon.createAnimatedSprite();

		gunUnequipping = false;
		updateAnimation();
	}


	/**
	 * Soft setter method for animation state - reverse gun animation
	 */
	public void unequipWeapon() {
		if (currentWeapon != null) {
			gunUnequipping = true;
			updateAnimation();
		}
	}

	/**
	 * Getter reference to parents (player) player Number
	 * @return player.playerNumber
	 */
	public int getPlayerNumber() {
		return this.player.getPlayerNumber();
	}
	/**
	 * Getter method for this Worm's charNum
	 * @return charNum
	 */
	public int getCharacterNumber() {
		return this.characterNumber;
	}

    /**
     * Damage handler method - calculate remaining life and death
     * @param damage - The damage taken as integer
     */
	public void takeDamage(int damage, int damageType) {
		health -= damage;

		//HoverText info = new HoverText(this.world, this, "-"+damage, Color.RED);
		//world.registerAfterUpdate(info);

		EventManager.getInstance().queueEvent(EventManager.Type.WormTookDamage, new DamageEvent(this, damage, damageType));

		if (health <= 0) {
			// Is dead, kill it
			health = 0;
			die(Constants.DEATH_TYPE_NO_HEALTH);
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
	public void die(int deathType) {
		if (!isDead) {
			EventManager.getInstance().queueEvent(EventManager.Type.WormDied, new DeathEvent(this, deathType));
			isDead = true;
		}
	}
	
	/**
	 * Getter method for character's jump status
	 * @return Character on ground and allowed to move?
	 */
	public boolean canJump() {
		return this.isStandsOnGround();
	}

	/**
	 * Getter method for character's ground status
	 * @return Is this Worm considered "on ground"?
	 */
	public boolean isStandsOnGround() {
	    if (getBody() == null) return true;
	    return numContacts > 0 || isStatic();
	}
	/**
	 * Method to begin a new contact with a new ground piece
	 */
	public void beginContact() {
		if (numContacts++ == 0) {
			EventManager.getInstance().queueEvent(EventManager.Type.FeetCollision, null);
			invalidateAnimation();
		}
	}
	/**
	 * Method to end one ground contact, if any
	 */
	public void endContact() {
		if (--numContacts == 0)
			invalidateAnimation();
	}

	public void setNumContacts(int numContacts) {
		if ((this.numContacts == 0 && numContacts > 0) || (this.numContacts > 0 && numContacts == 0))
			invalidateAnimation();

		this.numContacts = numContacts;
	}

	public int getNumContacts() {
		return numContacts;
	}

	/**
	 * Setter method for character's movement orders and orientation
	 * @param newMovementCode - Constant based movement code for left, right or stop
	 */
	public void setMovement(int newMovementCode) {
	    if (newMovementCode != Constants.MOVEMENT_LEFT && newMovementCode != Constants.MOVEMENT_RIGHT && newMovementCode != Constants.MOVEMENT_NO_MOVEMENT)
	        throw new IllegalArgumentException("Movement code must be either MOVEMENT_LEFT, MOVEMENT_RIGHT or MOVEMENT_NO_MOVEMENT");

		// Same code? Nothing to go here!
		if (movement == newMovementCode) return;

		this.movement = newMovementCode;
		invalidateAnimation();

		EventManager.getInstance().queueEvent(EventManager.Type.WormMovement, this);

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
		//updateAnimation();
	}

	/**
	 * Method to update our character's animation state
	 */
	private void updateAnimation() {
		if (!isStandsOnGround()) 								currentAnimation = flyAnimation;
		else if (movement != Constants.MOVEMENT_NO_MOVEMENT) 	currentAnimation = walkAnimation;
		else if (getCurrentWeapon() != null) 					currentAnimation = weaponAnimation;
		else 													currentAnimation = idleAnimation;

		// flip the animation if needed since the animations are only for the left direction
		if (currentAnimation != null) {
			currentAnimation.setOrientation(orientation);
			currentAnimation.reset();

			// We're holding a gun... but aren't supposed to anymore. Reverse "draw weapon" animation
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
	 * Getter method for the currently selected weapon
	 * @return current Weapon
	 */
	public Weapon getCurrentWeapon() {return currentWeapon;}

	/**
	 * Method to generate and fill a SnapshotData object
	 */
	public SnapshotData makeSnapshot() {
		SnapshotData data = new SnapshotData();

		data.characterNumber = characterNumber;
		data.health = health;
		data.position = new Vector2(getPosition());
		data.orientation = orientation;
		data.isInfected = isInfected;
		data.isDead = isDead;
		data.name = name;

		return data;
	}
}
