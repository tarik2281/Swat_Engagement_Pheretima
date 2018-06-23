package de.paluno.game.gameobjects;

import com.badlogic.gdx.assets.AssetManager;

import de.paluno.game.*;
import de.paluno.game.screens.WeaponUI;

public class Player implements Updatable {

	/**
     * Inner class to create a copy of the data necessary for the replay
     */
	public static class SnapshotData {
		private int playerNumber;
		private Worm.SnapshotData[] wormData;
		private Weapon.SnapshotData[] weaponData;

		public int getPlayerNumber() {
			return playerNumber;
		}
	}

	private int playerNum;
	private WeaponUI weaponUI;

	private int numCharacters;
	private Worm[] characters;
	private Weapon[] weapons;
	private ShotDirectionIndicator shotDirectionIndicator;
	private WindDirectionIndicator windDirectionIndicator;
	private int turn = 0;
	private boolean isRoundEnded = false;

	private World world;

	/**
	 * Inner KeyListener object to dynamically register reactions to certain input keys pressed
	 */
	private InputHandler.KeyListener keyListener = (keyCode, keyDown) -> {
		if (keyDown) {
			switch (keyCode) {
				case Constants.KEY_MOVE_LEFT:
					getCurrentWorm().setMovement(Constants.MOVEMENT_LEFT);
					break;
				case Constants.KEY_MOVE_RIGHT:
					getCurrentWorm().setMovement(Constants.MOVEMENT_RIGHT);
					break;
				case Constants.KEY_JUMP:
					getCurrentWorm().setJump(true);
					break;
                case Constants.KEY_DO_ACTION:
                    shoot();
                    break;
				case Constants.KEY_ROTATE_INDICATOR_DOWN:
					getShotDirectionIndicator().setRotationMovement(Constants.MOVEMENT_DOWN);
					break;
				case Constants.KEY_ROTATE_INDICATOR_UP:
					getShotDirectionIndicator().setRotationMovement(Constants.MOVEMENT_UP);
					break;
                case Constants.KEY_SELECT_WEAPON_1:
                	equipWeapon(WeaponType.WEAPON_GUN);
                    break;
                case Constants.KEY_SELECT_WEAPON_2:
					equipWeapon(WeaponType.WEAPON_GRENADE);
                    break;
                case Constants.KEY_SELECT_WEAPON_3:
					equipWeapon(WeaponType.WEAPON_BAZOOKA);
                    break;
				case Constants.KEY_SELECT_WEAPON_4:
					equipWeapon(WeaponType.WEAPON_SPECIAL);
					break;
			}
		}
		else {
			// And the same when the key is released
			switch (keyCode) {
				case Constants.KEY_MOVE_LEFT:
					if (getCurrentWorm().getMovement() == Constants.MOVEMENT_LEFT)
						getCurrentWorm().setMovement(Constants.MOVEMENT_NO_MOVEMENT);
					break;
				case Constants.KEY_MOVE_RIGHT:
					if (getCurrentWorm().getMovement() == Constants.MOVEMENT_RIGHT)
						getCurrentWorm().setMovement(Constants.MOVEMENT_NO_MOVEMENT);
					break;
				case Constants.KEY_ROTATE_INDICATOR_DOWN:
					if (getShotDirectionIndicator().getRotationMovement() == Constants.MOVEMENT_DOWN)
						getShotDirectionIndicator().setRotationMovement(Constants.MOVEMENT_NO_MOVEMENT);
					break;
				case Constants.KEY_ROTATE_INDICATOR_UP:
					if (getShotDirectionIndicator().getRotationMovement() == Constants.MOVEMENT_UP)
						getShotDirectionIndicator().setRotationMovement(Constants.MOVEMENT_NO_MOVEMENT);
					break;
			}
		}

		return true;
	};
	
	/**
	 * Empty constructor for cloning purposes
	 */
	public Player() {}
	/**
	 * Constructor
	 * @param playerNum - Player Number of this player
	 * @param numWorms - Number of characters this player begins with
	 * @param world - Reference to the world we are playing in
	 */
	public Player(int playerNum, int numWorms, World world) {
		
		this.numCharacters = numWorms;
		this.playerNum = playerNum;
		this.world = world;

        setupWeapons();
        setupWorms();
		this.shotDirectionIndicator = new ShotDirectionIndicator(playerNum, world);

	}
	/**
	 * Constructor to create a new Player from existing data - for replay purposes
	 * @param data - The SnpashotData to copy from
	 * @param world - The reference to the (copied) world
	 */
	public Player(SnapshotData data, World world) {
		this.playerNum = data.playerNumber;
		this.world = world;

		setupWorms(data.wormData);
		setupWeapons(data.weaponData);

		this.shotDirectionIndicator = new ShotDirectionIndicator(playerNum, world);
	}
	
	/**
	 * Getter method to get this player's ShotDirectionIndicator
	 * @return shotDirectionIndicator
	 */
	public ShotDirectionIndicator getShotDirectionIndicator() {
		return shotDirectionIndicator;
	}
	/**
	 * Getter method to get this player's WindDirectionIndicator
	 * @return windDirectionIndicator
	 */
	public WindDirectionIndicator getWindDirectionIndicator() {
		return windDirectionIndicator;
	}
	/**
	 * Soft setter method to set this player's WindHandler
	 * @param windHandler - The WindHandler to use for the creation of our windDirectionIndicator
	 */
	public void setWindHandler(WindHandler windHandler) {
		windDirectionIndicator = new WindDirectionIndicator(playerNum, world, windHandler);
	}


	/**
	 * Method to generate and setup all of our characters
	 */
	private void setupWorms() {
        this.characters = new Worm[numCharacters];

        for(int i = 0; i < numCharacters; i++) {
            characters[i] = new Worm(this, i);
            HealthBar healthBar = new HealthBar(world, characters[i]);
            world.registerAfterUpdate(characters[i]);
            world.registerAfterUpdate(healthBar);
            world.registerAfterUpdate(windDirectionIndicator);
        }
    }
	/**
	 * Method to generate all our characters from existing SnapshotData
	 * @param data - Array of Worm-SnapshotData to use for generation
	 */
	private void setupWorms(Worm.SnapshotData[] data) {
		this.characters = new Worm[data.length];

		for (int i = 0; i < data.length; i++) {
			if (data[i] != null) {
				characters[i] = new Worm(this, data[i]);
				HealthBar healthBar = new HealthBar(world, characters[i]);
            	world.registerAfterUpdate(characters[i]);
            	world.registerAfterUpdate(healthBar);
            	numCharacters++;
			}
		}
	}
	/**
	 * Method to generate and setup all our deadly weapons
	 */
	private void setupWeapons() {
        weapons = new Weapon[WeaponType.NUM_WEAPONS];

        weapons[0] = new Weapon(this, WeaponType.WEAPON_GUN);
        weapons[1] = new Weapon(this, WeaponType.WEAPON_GRENADE);
        weapons[2] = new Weapon(this, WeaponType.WEAPON_BAZOOKA);
        weapons[3] = new Weapon(this, WeaponType.WEAPON_SPECIAL);
        weapons[4] = new Weapon(this, WeaponType.WEAPON_AIRSTRIKE);
    }
	/**
	 * Method to generate all our weapons from existing SnapshotData
	 * @param data - Array of Weapon-SnapshotData to use for generation
	 */
	private void setupWeapons(Weapon.SnapshotData[] data) {
		weapons = new Weapon[WeaponType.NUM_WEAPONS];

        weapons[0] = new Weapon(this, data[0]);
        weapons[1] = new Weapon(this, data[1]);
        weapons[2] = new Weapon(this, data[2]);
	}
	/**
	 * Soft getter method to get a certain character
	 * @param characterNumber - Number of the character to fetch
	 * @return Worm behind given number
	 */
	public Worm getWormByNumber(int characterNumber) {
		return characters[characterNumber];
	}

	/**
	 * Handler for GameLoop's update cycle - needed from Interface updatable
	 * This dependency is just to update the GameState for the players directly, instead of having 5 cross-communications with it's Worms
	 * @param delta - Time since last update in seconds
	 * @param state - Current GameState
	 */
	public void update(float delta, GameState state) {}
	
	/**
	 * Getter method for all our worms
	 * @return Array of Worms
	 */
	public Worm[] getCharacters() {return characters;}

	/**
	 * Getter method for this player's player number
	 * @return - player number
	 */
	public int getPlayerNumber() {return this.playerNum;}
	/**
	 * Getter method for the character's turn
	 * @return turn
	 */
	public int getTurn() {return this.turn;}
	
	/**
	 * Method to register all objects and handlers when it becomes this player's turn
	 */
	public void onBeginTurn() {
		// Register indicators for drawing
		world.registerAfterUpdate(getShotDirectionIndicator());
		world.registerAfterUpdate(getWindDirectionIndicator());
		// Attach indicators to the characters whose turn it is
		getShotDirectionIndicator().attachToWorm(getCurrentWorm());
		getWindDirectionIndicator().attachToWorm(getCurrentWorm());
		// Default weapon
		equipWeapon(WeaponType.WEAPON_BAZOOKA);
		// Worm's turn it is
		getCurrentWorm().setIsPlaying(true);
		
		// All Keys to listen to
		InputHandler input = InputHandler.getInstance();
		input.registerKeyListener(Constants.KEY_MOVE_LEFT, keyListener);
		input.registerKeyListener(Constants.KEY_MOVE_RIGHT, keyListener);
		input.registerKeyListener(Constants.KEY_DO_ACTION, keyListener);
		input.registerKeyListener(Constants.KEY_JUMP, keyListener);
		input.registerKeyListener(Constants.KEY_ROTATE_INDICATOR_DOWN, keyListener);
		input.registerKeyListener(Constants.KEY_ROTATE_INDICATOR_UP, keyListener);
		input.registerKeyListener(Constants.KEY_SELECT_WEAPON_1, keyListener);
		input.registerKeyListener(Constants.KEY_SELECT_WEAPON_2, keyListener);
		input.registerKeyListener(Constants.KEY_SELECT_WEAPON_3, keyListener);
		input.registerKeyListener(Constants.KEY_SELECT_WEAPON_4, keyListener);
	}
	/**
	 * Method to deregister all objects and handlers when this player's turn is over
	 */
	public void onEndTurn() {
        // No need to draw our indicators anymore
		world.forgetAfterUpdate(getShotDirectionIndicator());
        world.forgetAfterUpdate(getWindDirectionIndicator());
        // No attachment anymore - Worm might not even exist anymore
        getShotDirectionIndicator().attachToWorm(null);
        getWindDirectionIndicator().attachToWorm(null);
        // Reset angle of indicator to default
		getShotDirectionIndicator().setRotationMovement(Constants.MOVEMENT_NO_MOVEMENT);
		// If Worm still alive, force stop and reset
		if (getCurrentWorm() != null) {
            getCurrentWorm().setMovement(Constants.MOVEMENT_NO_MOVEMENT);
            getCurrentWorm().unequipWeapon();
            getCurrentWorm().setIsPlaying(false);
        }
		
		// Next time it's another Worm's turn
        shiftTurn();
        
        // No need to listen to these Keys anymore
		InputHandler input = InputHandler.getInstance();
		input.unregisterKeyListener(Constants.KEY_MOVE_LEFT, keyListener);
		input.unregisterKeyListener(Constants.KEY_MOVE_RIGHT, keyListener);
		input.unregisterKeyListener(Constants.KEY_DO_ACTION, keyListener);
		input.unregisterKeyListener(Constants.KEY_JUMP, keyListener);
		input.unregisterKeyListener(Constants.KEY_ROTATE_INDICATOR_DOWN, keyListener);
		input.unregisterKeyListener(Constants.KEY_ROTATE_INDICATOR_UP, keyListener);
        input.unregisterKeyListener(Constants.KEY_SELECT_WEAPON_1, keyListener);
        input.unregisterKeyListener(Constants.KEY_SELECT_WEAPON_2, keyListener);
        input.unregisterKeyListener(Constants.KEY_SELECT_WEAPON_3, keyListener);
        input.unregisterKeyListener(Constants.KEY_SELECT_WEAPON_4, keyListener);
	}

	/**
	 * Soft setter method for the character's turn
	 * Shift through all still available characters to find the next one whose turn it is
	 */
	protected void shiftTurn() {
	    // Noone left - shouldn't happen, nothing to do
		if (numCharacters <= 0)
	        return;

		turn++;
		if (turn == characters.length) {
			// Handle int-overflow (i.e. higher number than possible characters)
			turn = 0;
			isRoundEnded = true;
		}
		// This character isn't alive anymore? Choose another one
		if (characters[turn] == null) shiftTurn();
	}
	
	/**
	 * Getter method to determine if this player's turn is the last one in the current gameround
	 * @return After this we need to do things like lower level?
	 */
	public boolean isRoundEnded() {
		return isRoundEnded;
	}
	/**
	 * Setter method to determine if this player's turn is the last one in the current gameround
	 * @param isRoundEnded - After this we need to do things like lower level?
	 */
	public void setIsRoundEnded(boolean isRoundEnded) {
		this.isRoundEnded = isRoundEnded;
	}

	/**
	 * Getter Method for the reference to the Asset Manager
	 * @return world.AssetManager
	 */
	public AssetManager getAssets() {
		return world.getAssetManager();
	}

	/**
	 * Getter method for the world we are playing in
	 * @return world
	 */
	public World getWorld() {
		return this.world;
	}
	
	/**
	 * Soft setter method for characterNumber - Set Character as KIA and remove it
	 * @param charNum - The number of the character that died
	 */
	protected void characterDied(int charNum) {
		// No characters anymore or this one allready dead? Nothing to do here.
		if(this.numCharacters <= 0 || this.characters[charNum] == null)
			return;

		// It was this Worm's turn? The game must go on!
		if (getCurrentWorm() != null && getCurrentWorm().isPlaying())
		    world.advanceGameState();

		// Farewell, rendering reference...!
		world.forgetAfterUpdate(characters[charNum]);
		this.characters[charNum] = null;
		this.numCharacters--;

		// It was this Worm's turn? So, someone else must play next time
		if (charNum == turn)
		    shiftTurn();
		
		// And finally - is ded
		world.setWormDied(true);
	}
	
	/**
	 * Soft getter method for number of still alive characters
	 * @return Any characters left?
	 */
	public boolean isDefeated() {
		return numCharacters <= 0;
	}
	
	/**
	 * Getter method to get a Weapon out of our arsenal, based on a selected WeaponType
	 * @param type - The WeaponType of the Weapon we want
	 * @return That Weapon
	 */
    public Weapon getWeapon(WeaponType type) {
		for (Weapon weapon : weapons)
			if (weapon.getWeaponType() == type)
				return weapon;

		return null;
	}
    /**
     * Passthrough method to make our current Worm equip a given Weapon
     * @param weaponType - The WeaponType of the Weapon to select
     */
	public void equipWeapon(WeaponType weaponType) {
		getCurrentWorm().equipWeapon(getWeapon(weaponType));
	}

	/**
	 * Getter method for player's turn status
	 * @return Is it this player's turn?
	 */
	public boolean isPlayerTurn() {
		return world.getCurrentPlayer() == this;
	}
	/**
	 * Passthrough setter method to set all Worms static or not
	 * @param isStatic - Shall they be rocks or no?
	 */
	public void setWormsStatic(boolean isStatic) {
		for (Worm worm : characters) {
			if (worm != null) {
				worm.setIsStatic(isStatic);
			}
		}
	}
	
	/**
	 * Getter method for the character whose turn it currently is
	 * @return Worm
	 */
	public Worm getCurrentWorm() {return this.characters[this.turn];}
	
	/**
	 * Passthrough method to give move order to the currently movable worm
	 * @param code - Constants.MOVEMENT_... integer for the movement code
	 */
	public void setMovement(int code) {if(getCurrentWorm() != null) getCurrentWorm().setMovement(code);}

	/**
	 * Passthrough method to give a jump order to the currently movable worm
	 */
	public void jump() {if(getCurrentWorm() != null) getCurrentWorm().setJump(true);}

	/**
	 * Passthrough method to give a shoot order to the currently movable worm
	 */
	public void shoot() {
		if(getCurrentWorm() != null)
			getCurrentWorm().shoot(getShotDirectionIndicator().getAngle());
		// We're shooting. That means, someone could die.
		// That means, we want to see him suffer again and again in slow motion.
		// That means: Capture that shit!
		makeSnapshot();
	}

	/**
	 * Method to create and fill a SnapshotData clone
	 */
	public SnapshotData makeSnapshot() {
		SnapshotData data = new SnapshotData();

		data.playerNumber = playerNum;

		data.wormData = new Worm.SnapshotData[characters.length];
		data.weaponData = new Weapon.SnapshotData[weapons.length];

		for (int i = 0; i < characters.length; i++)
			if (characters[i] != null)
				data.wormData[i] = characters[i].makeSnapshot();

		for (int i = 0; i < weapons.length; i++)
			data.weaponData[i] = weapons[i].makeSnapshot();

		return data;
	}

	/**
	 * Method to handle the DO_ACTION Key being pressed, based on current game situation
	 * @param keycode - Forced parameter, in this case not used.
	 */
	public void handleAction(int keycode) {
		this.shoot();
	}
}
