package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
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

public class Worm implements Updatable, PhysicsObject, Renderable {

	private int characterNumber;
	
	private World world;
	private Body body;
	private PlayScreen screen;
	private Player player;
	private AssetManager assets;

	private Vector2 spawnPosition;
	
	private GameState currentState;

	private AnimatedSprite currentAnimation;
	
	private int movement = Constants.MOVEMENT_NO_MOVEMENT;
	private boolean moveLeft = false;
	private boolean moveRight = false;
	private int orientation = Constants.WORM_DIRECTION_LEFT;
	private boolean jump = false;
	private boolean standsOnGround;

	private boolean gunEquipped = false;
	private boolean gunUnequipping = false;

	private int health;

	private Weapon[] weapons;
	private Weapon currentWeapon = null;

	/*public Worm(int num, PlayScreen screen, Vector2 position) {
		// Set given starting parameters
		this.playerNumber = num;
		this.screen = screen;
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
	 * Constructor
	 * @param player - reference to the player we belong to
	 * @param charNum - Our character number
	 */
	public Worm(Player player, int charNum) {
		// Link references
		this.player = player;
		this.characterNumber = charNum;
		this.world = player.getWorld();
		this.assets = player.getAssets();

		// Get our spawning position
		this.spawnPosition = world.generateSpawnposition();

		// Generate our physics body
		this.setupBody();

		// Generate our deadly arsenal
		this.setupWeapons();

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
		
		// Update gamestate
		this.currentState = state;
		this.player.setGameState(state);

        // Now we apply movements - therefor we need our current position
		Vector2 currentPos = body.getWorldCenter();

		if(this.jump && this.canJump()) {
		    // We shall jump - AND are allowed to - so let's apply some vertical impulse
			// TODO: maybe jump and landing animations
			this.body.applyLinearImpulse(0.0f, body.getMass() * Constants.JUMP_VELOCITY,
					currentPos.x, currentPos.y, true);
		}
		// Whether or not we actually jumped, remove the order.
		if(getJump()) this.jump = false;

		// Now we calculate the new movement speed based on current movement impulses
		// http://www.iforce2d.net/b2dtut/constant-speed
		Vector2 currentVel = body.getLinearVelocity();
		float desiredVel = 0.0f;

		/*switch (movement) {
            case Constants.MOVEMENT_LEFT:
                desiredVel = -Constants.MOVE_VELOCITY;
                break;
            case Constants.MOVEMENT_NO_MOVEMENT: default:
                desiredVel = 0.0f;
                break;
            case Constants.MOVEMENT_RIGHT:
                desiredVel = Constants.MOVE_VELOCITY;
                break;
        }*/
		if(moveLeft) desiredVel = -Constants.MOVE_VELOCITY;
		else if(moveRight) desiredVel = Constants.MOVE_VELOCITY;

        float velChange = desiredVel - currentVel.x;
        // Finally we calculate the actual impulse force, based on body mass
		float impulse = body.getMass() * velChange;
		this.body.applyLinearImpulse(impulse, 0.0f, currentPos.x, currentPos.y, true);
		
		// Worm fell off the world rim? Is ded.
		if (!screen.getWorldBounds().contains(body.getPosition())) die();
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

        if (gunUnequipping && currentAnimation.isAnimationFinished()) {
			// The weapon should be uneuqipping and is finished with that - update
        	gunEquipped = false;
        	gunUnequipping = false;
        	updateAnimation();
		}

        // And finally draw it
		currentAnimation.setPosition(currentPos);
		currentAnimation.draw(batch, delta);
	}

	/**
	 * Method to setup our actual physics body
	 */
	public void setupBody() {
		// Blueprint with spawning position and BodyType
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(this.spawnPosition.x, this.spawnPosition.y);
		bodyDef.type = BodyType.DynamicBody;
		
		// Create the actual physics body in our current game world
		this.body = this.world.createBody(bodyDef);
		//body.setFixedRotation(true);
		
		// Now we add some hitboxes - Worm is easy, just a rectangle
		PolygonShape bodyRect = new PolygonShape();
		bodyRect.setAsBox(Constants.WORM_WIDTH / 2.0f, Constants.WORM_HEIGHT / 2.0f);

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
	
	protected void setupWeapons() {
		// TODO: Outsource weapon stats and make retrieval more dynamic
		/*Object[][][] weaponStats = new Object[][][] {
			{
				{"Rakentenwerfer",
				 "Verschie� eine langsame, ungelenkte Rakete, die von der Schwerkraft und von Wind beeinfluss wird und explosiven Fl�chenschaden an allen Objekten verursacht.",
				 null}
			},
			{
				{"Sturmgewehr",
				 "Verschie�t ein schnelles, aber daf�r schwaches Projektil, dass stur geradeaus fliegt und nur Schaden an W�rmern verursacht.",
				 null}
			},
			{
				{"Handgranate",
				 "Eine geworfene Handgranate, die umherh�pft und nach "+Constants.WEAPON_THROWABLE_TIMER+" Sekunden detoniert.",
				 null}
			}
		};
		int i, j = 0, k = 0;
		WeaponType type;

		this.weapons = new Weapon[Constants.WEAPON_ARSENAL_SIZE];
		for(i = 0; i < Constants.WEAPON_ARSENAL_SIZE && k < 3; i++) {
			switch(k) {
				default: case 0: type = WeaponType.WEAPON_PROJECTILE; break;
				case 1: type = WeaponType.WEAPON_PROJECTILE; break;
				case 2: type = WeaponType.WEAPON_PROJECTILE; break;
			}
		}*/
		this.weapons = new Weapon[Constants.WEAPON_ARSENAL_SIZE];
		this.weapons[0] = new Weapon(this, WeaponType.WEAPON_PROJECTILE, "Raketenwerfer", "Verschie� eine langsame, ungelenkte Rakete, die von der Schwerkraft und von Wind beeinfluss wird und explosiven Fl�chenschaden an allen Objekten verursacht.", null);
		this.weapons[1] = new Weapon(this, WeaponType.WEAPON_RIFLE, "Sturmgewehr", "Verschie�t ein schnelles, aber daf�r schwaches Projektil, dass stur geradeaus fliegt und nur Schaden an W�rmern verursacht.", null, Constants.WEAPON_AMMO_RIFLE);
		this.weapons[2] = new Weapon(this, WeaponType.WEAPON_THROWABLE, "Handgranate", "Eine geworfene Handgranate, die umherh�pft und nach "+Constants.WEAPON_THROWABLE_TIMER+" Sekunden detoniert.", null, Constants.WEAPON_AMMO_THROWABLE);
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
	 * Soft setter method for animation state - set animation to equipGun
	 */
	public void equipGun() {
		gunEquipped = true;
		updateAnimation();
    }
	/**
	 * Soft setter method for animation state - reverse gun animation
	 */
    public void unequipGun() {
        if (gunEquipped) {
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
		this.setBodyToNullReference();
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
				this.characterNumber == this.player.getTurn() &&
				this.currentState = GameState.SHOOTING;
	}
	
	/**
	 * Getter method for character's ground status
	 * @return standsOnGround
	 */
	public boolean isStandsOnGround() {return this.standsOnGround;}
	/**
	 * Setter method for character's ground status
	 * @param onGround - Is the character on the ground now?
	 */
	public void setStandsOnGround(boolean onGround) {this.standsOnGround = onGround;}
	/**
	 * Setter method for character's movement orders and orientation
	 * @param newMovementCode - Constant based movement code for left, right or stop
	 */
	public void setMovement(int newMovementCode) {
		// Same code? Nothing to go here!
		if (movement == newMovementCode) return;

		this.movement = newMovementCode;

		if (movement != Constants.MOVEMENT_NO_MOVEMENT) {
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
	public void setMovement(int newMovementCode, boolean set) {
		switch(newMovementCode) {
			case Constants.MOVEMENT_LEFT:
				if(set) {
					if(moveLeft) return;
					moveLeft = true;
					orientation = Constants.WORM_DIRECTION_LEFT;
				} else {
					if(!moveLeft) return;
					moveLeft = false;
				}
				break;
			case Constants.MOVEMENT_RIGHT:
				if(set) {
					if(moveRight) return;
					moveRight = true;
					orientation = Constants.WORM_DIRECTION_RIGHT;
				} else {
					if(!moveRight) return;
					moveRight = false;
				}
				break;
		}

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

		if(!isStandsOnGround) currentAnimation = this.assets.getAnimation('jump');
		else if(moveLeft || moveRight) currentAnimation = this.assets.getAnimation('walk');
		else {
			if(gunEquipped) currentAnimation = getCurrentWeapon().getAnimation();
			else currentAnimation = this.assets.getAnimation('idle');
		}

		// flip the animation if needed since the animations are only for the left direction
		currentAnimation.setOrientation(orientation);
		currentAnimation.reset();

		if (currentAnimation.isAnimationType('weapon') && gunUnequipping)
			currentAnimation.reverse(true);
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
	public AssetManager getAssets() {return this.assets;}
	/**
	 * Soft setter method for the character's currently selected weapon
	 * @param weaponId - int 0 - WEAPON_ARSENAL_SIZE-1 number of the weapon to select
	 */
	public void setCurrentWeapon(int weaponId) {
		if(weaponId < 0) weaponId = 0;
		else if(weaponId >= Constants.WEAPON_ARSENAL_SIZE) weaponId = Constants.WEAPON_ARSENAL_SIZE - 1;
		this.currentWeapon = weapons[weaponId];
	}
	/**
	 * Getter method for the currently selected weapon
	 * @return current Weapon
	 */
	public Weapon getCurrentWeapon() {return currentWeapon;}

	/**
	 * Passthrough method to give the shoot order to the currently selected weapon, if any and allowed
	 */
	public void shoot() {if(canShoot() && currentWeapon != null) currentWeapon.shoot();}
}
