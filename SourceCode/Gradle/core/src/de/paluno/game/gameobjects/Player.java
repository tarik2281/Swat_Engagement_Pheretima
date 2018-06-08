package de.paluno.game.gameobjects;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;

import de.paluno.game.*;
import de.paluno.game.screens.PlayScreen;
import de.paluno.game.screens.WeaponUI;

public class Player implements Updatable {

	public class SnapshotData {
		private int playerNumber;
		private Worm.SnapshotData[] wormData;
	}

	private int playerNum;
	
	private int numCharacters;
	public Worm[] characters;
	private Weapon[] weapons;
	private ShotDirectionIndicator shotDirectionIndicator;
	private WindDirectionIndicator windDirectionIndicator;
	private int turn = 1;
	
	private World world;

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
                case Input.Keys.F1:
                    getCurrentWorm().equipWeapon(weapons[0]);
                    break;
                case Input.Keys.F2:
                    getCurrentWorm().equipWeapon(weapons[1]);
                    break;
                case Input.Keys.F3:
                    getCurrentWorm().equipWeapon(weapons[2]);
                    break;
			}
		}
		else {
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
	 * @param charNum - Number of characters this player begins with
	 * @param playerNum - Player Number of this player
	 * @param world - Reference to the world we are playing in
	 * @param assets - Reference to the global Asset Manager
	 */
	public Player(int playerNum, World world) {
		
		this.numCharacters = Constants.MAX_CHAR_NUM;
		this.playerNum = playerNum;
		this.world = world;

        setupWeapons();
        setupWorms();
		this.shotDirectionIndicator = new ShotDirectionIndicator(playerNum, world);
		//this.windDirectionIndicator = new WindDirectionIndicator(playerNum,world,windHandler);
	}

	public void setWindHandler(WindHandler windHandler) {
		windDirectionIndicator = new WindDirectionIndicator(playerNum, world, windHandler);
	}
	public void setWeaponUI(WeaponUI weaponUI){
	}


	public Player(SnapshotData data, World world) {

	}

	public ShotDirectionIndicator getShotDirectionIndicator() {
		return shotDirectionIndicator;
	}
	public WindDirectionIndicator getWindDirectionIndicator(){ return windDirectionIndicator; }

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

	private void setupWeapons() {
        weapons = new Weapon[WeaponType.NUM_WEAPONS];

        weapons[0] = new Weapon(this, WeaponType.WEAPON_GUN);
        weapons[1] = new Weapon(this, WeaponType.WEAPON_GRENADE);
        weapons[2] = new Weapon(this, WeaponType.WEAPON_BAZOOKA);
    }

	/**
	 * Handler for GameLoop's update cycle - needed from Interface updatable
	 * This dependency is just to update the GameState for the players directly, instead of having 5 cross-communications with it's Worms
	 * @param delta - Time since last update in seconds
	 * @param state - Current GameState
	 */
	public void update(float delta, GameState state) {}

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
	 * Hard setter method for the character's turn 
	 * @param turn - int 1 - MAX_CHAR_NUM: Character number whose turn it is now
	 */
	protected void setTurn(int turn) {
		if(turn < 1) this.turn = 1;
		else if(this.turn > Constants.MAX_CHAR_NUM) this.turn = Constants.MAX_CHAR_NUM;
		else this.turn = turn;
	}

	public void onBeginTurn() {
		world.registerAfterUpdate(getShotDirectionIndicator());
		world.registerAfterUpdate(getWindDirectionIndicator());
		getShotDirectionIndicator().attachToWorm(getCurrentWorm());
		getWindDirectionIndicator().attachToWorm(getCurrentWorm());
		getCurrentWorm().equipWeapon(weapons[2]);
		getCurrentWorm().setIsPlaying(true);

		InputHandler input = InputHandler.getInstance();
		input.registerKeyListener(Constants.KEY_MOVE_LEFT, keyListener);
		input.registerKeyListener(Constants.KEY_MOVE_RIGHT, keyListener);
		input.registerKeyListener(Constants.KEY_DO_ACTION, keyListener);
		input.registerKeyListener(Constants.KEY_JUMP, keyListener);
		input.registerKeyListener(Constants.KEY_ROTATE_INDICATOR_DOWN, keyListener);
		input.registerKeyListener(Constants.KEY_ROTATE_INDICATOR_UP, keyListener);
		input.registerKeyListener(Input.Keys.F1, keyListener);
		input.registerKeyListener(Input.Keys.F2, keyListener);
		input.registerKeyListener(Input.Keys.F3, keyListener);
	}

	public void onEndTurn() {
        world.forgetAfterUpdate(getShotDirectionIndicator());
        world.forgetAfterUpdate(getWindDirectionIndicator());

        getShotDirectionIndicator().attachToWorm(null);
        getWindDirectionIndicator().attachToWorm(null);

		getShotDirectionIndicator().setRotationMovement(Constants.MOVEMENT_NO_MOVEMENT);
		if (getCurrentWorm() != null) {
            getCurrentWorm().setMovement(Constants.MOVEMENT_NO_MOVEMENT);
            getCurrentWorm().unequipWeapon();
            getCurrentWorm().setIsPlaying(false);
        }

        shiftTurn();

		InputHandler input = InputHandler.getInstance();
		input.unregisterKeyListener(Constants.KEY_MOVE_LEFT, keyListener);
		input.unregisterKeyListener(Constants.KEY_MOVE_RIGHT, keyListener);
		input.unregisterKeyListener(Constants.KEY_DO_ACTION, keyListener);
		input.unregisterKeyListener(Constants.KEY_JUMP, keyListener);
		input.unregisterKeyListener(Constants.KEY_ROTATE_INDICATOR_DOWN, keyListener);
		input.unregisterKeyListener(Constants.KEY_ROTATE_INDICATOR_UP, keyListener);
        input.unregisterKeyListener(Input.Keys.F1, keyListener);
        input.unregisterKeyListener(Input.Keys.F2, keyListener);
        input.unregisterKeyListener(Input.Keys.F3, keyListener);
	}

	/**
	 * Soft setter method for the character's turn
	 * Shift through all still available characters to find the next one whose turn it is
	 */
	protected void shiftTurn() {
	    if (numCharacters == 0)
	        return;

		turn++;
		if (turn == Constants.MAX_CHAR_NUM)
			turn = 0;
		if (characters[turn] == null) shiftTurn();

		//if(numCharacters == 0) return;
		//this.turn++;
		//if(turn > numCharacters) turn = 1;
		//if(characters[turn-1] == null) shiftTurn();
	}
	/**
	 * Getter Method for the reference to the Asset Manager
	 * @return AssetManager
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

		if (getCurrentWorm().isPlaying())
		    world.advanceGameState();

		world.forgetAfterUpdate(characters[charNum]);
		this.characters[charNum] = null;
		this.numCharacters--;

		if (charNum == turn)
		    shiftTurn();

		if (numCharacters <= 0)
			world.playerDefeated(this);
	}

	public Weapon[] getWeapons() {
	    return weapons;
    }

    public void equipWeapon(Weapon weapon) {
	    getCurrentWorm().equipWeapon(weapon);
    }

	/**
	 * Getter method for player's turn status
	 * @return Is it this player's turn?
	 */
	public boolean isPlayerTurn() {
		return world.getCurrentPlayer() == this;
	}

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
	}
	
	/**
	 * Method to return a clone of this object
	 * @return clone
	 */
	public Player clone() {
		Player clone = new Player();
		clone.setCloningParameters(this);
		return clone;
	}
	/**
	 * Method to copy over all variables from a second Worm - used for cloning
	 * @param copy - The reference to the Worm to copy from
	 */
	public void setCloningParameters(Player copy) {
		this.playerNum = copy.playerNum;

		this.numCharacters = copy.numCharacters;
		this.characters = copy.characters;
		this.turn = copy.turn;
		
		this.world = copy.world;
	}

	public SnapshotData makeSnapshot() {
		SnapshotData data = new SnapshotData();
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
