package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

import com.badlogic.gdx.utils.Disposable;
import de.paluno.game.*;

import java.util.ArrayList;
import java.util.List;

public class Player implements Disposable {

	/**
     * Inner class to create a copy of the data necessary for the replay
     */
	public static class SnapshotData {
		private int playerNumber;
		public Worm.SnapshotData[] wormData;
		private Weapon.SnapshotData[] weaponData;

		public int getPlayerNumber() {
			return playerNumber;
		}
	}

	private int playerNum;

	private int numCharacters;

	private String name;
	private ArrayList<Worm> worms;
	private int numWormsAlive;
	private ArrayList<Weapon> weapons;
	private Weapon currentWeapon;
	private int turn = 0;
	private boolean isRoundEnded = false;

	private int clientId;

	private EventManager.Listener eventListener = (eventType, data) -> {
		switch (eventType) {
			case WormDied: {
				Worm.DeathEvent deathEvent = (Worm.DeathEvent)data;
				if (deathEvent.getWorm().getPlayerNumber() == playerNum && !isDefeated() && --numWormsAlive <= 0) {
					EventManager.getInstance().queueEvent(EventManager.Type.PlayerDefeated, this);
				}
				break;
			}
		}
	};

	public Player(int playerNumber) {
		this.playerNum = playerNumber;

		worms = new ArrayList<>();
		weapons = new ArrayList<>();
	}

	public void show() {
		EventManager.getInstance().addListener(eventListener, EventManager.Type.WormDied);
	}

	public void hide() {
		EventManager.getInstance().removeListener(eventListener, EventManager.Type.WormDied);
	}

	@Override
	public void dispose() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addWeapon(Weapon weapon) {
		weapons.add(weapon);
	}

	public Worm addWorm(int wormNumber, String name) {
		Worm worm = new Worm(this, wormNumber, name);
		worms.add(wormNumber, worm);
		numWormsAlive++;
		return worm;
	}

	private void removeWorm(Worm worm) {
		int index;
		for (index = 0; index < worms.size(); index++) {
			if (worms.get(index) == worm)
				break;
		}

		if (turn > 0 && turn >= index)
			turn--;

		worms.remove(index);
	}

	public List<Worm> getWorms() {
		return worms;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public int getClientId() {
		return clientId;
	}

	public void setTurn(int wormNumber) {
		int turn = -1;
		for (int i = 0; i < worms.size(); i++) {
			if (worms.get(i).getCharacterNumber() == wormNumber) {
				turn = i;
				break;
			}
		}

		if (turn == -1)
			throw new IllegalArgumentException("Worm with the given wormNumber=" + wormNumber +" is not in the list");

		this.turn = turn;
	}

	/**
	 * Soft getter method to get a certain character
	 * @param characterNumber - Number of the character to fetch
	 * @return Worm behind given number
	 */
	public Worm getWormByNumber(int characterNumber) {
		for (Worm worm : worms)
			if (worm.getCharacterNumber() == characterNumber)
				return worm;

		return null;
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
	 * Soft setter method for the character's turn
	 * Shift through all still available characters to find the next one whose turn it is
	 */
	public void shiftTurn() {
	    // Noone left - shouldn't happen, nothing to do
		if (isDefeated())
	        return;

		do {
			if (++turn >= worms.size()) {
				turn = 0;
				isRoundEnded = true;
			}
		} while (worms.get(turn).isDead());

		/*turn++;
		if (turn >= worms.size()) {
			// Handle int-overflow (i.e. higher number than possible characters)
			turn = 0;
			isRoundEnded = true;
		}
		// This character isn't alive anymore? Choose another one
		if (worms.get(turn) == null) shiftTurn();*/
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
	/*public AssetManager getAssets() {
		return world.getAssetManager();
	}*/

	/**
	 * Getter method for the world we are playing in
	 * @return world
	 */
	/*public World getWorld() {
		return this.world;
	}*/
	
	/**
	 * Soft setter method for characterNumber - Set Character as KIA and remove it
	 * @param charNum - The number of the character that died
	 */
	protected void characterDied(int charNum) {
		// No characters anymore or this one allready dead? Nothing to do here.
		if(this.numCharacters <= 0 || this.worms.get(charNum) == null)
			return;

		// It was this Worm's turn? The game must go on!
		//if (getCurrentWorm() != null && getCurrentWorm().isPlaying())
		    //world.advanceGameState();

		// Farewell, rendering reference...!
		//world.forgetAfterUpdate(characters[charNum]);
		//this.characters[charNum] = null;
		this.numCharacters--;

		// It was this Worm's turn? So, someone else must play next time
		if (charNum == turn)
		    shiftTurn();
		
		// And finally - is ded
		//world.setWormDied(true);
	}
	
	/**
	 * Soft getter method for number of still alive characters
	 * @return Any characters left?
	 */
	public boolean isDefeated() {
		return numWormsAlive <= 0;
	}
	
	/**
	 * Getter method to get a Weapon out of our arsenal, based on a selected WeaponType
	 * @param type - The WeaponType of the Weapon we want
	 * @return That Weapon
	 */
    public Weapon getWeapon(WeaponType type) {
    	if (type == WeaponType.WEAPON_GUN)
    		System.out.println("Equipping gun");

		for (Weapon weapon : weapons)
			if (weapon.getWeaponType() == type)
				return weapon;

		return null;
	}

	public Weapon getCurrentWeapon() {
    	return currentWeapon;
	}

    /**
     * Passthrough method to make our current Worm equip a given Weapon
     * @param weaponType - The WeaponType of the Weapon to select
     */
	public void equipWeapon(WeaponType weaponType) {
		currentWeapon = getWeapon(weaponType);
		getCurrentWorm().equipWeapon(currentWeapon);
	}

	/**
	 * Getter method for player's turn status
	 * @return Is it this player's turn?
	 */
	public boolean isPlayerTurn() {return true;
		//return world.getCurrentPlayer() == this;
	}
	/**
	 * Passthrough setter method to set all Worms static or not
	 * @param isStatic - Shall they be rocks or no?
	 */
	public void setWormsStatic(boolean isStatic) {
		for (Worm worm : worms) {
			if (worm != null) {
				worm.setIsStatic(isStatic);
			}
		}
	}
	
	/**
	 * Getter method for the character whose turn it currently is
	 * @return Worm
	 */
	public Worm getCurrentWorm() {return this.worms.get(this.turn);}
	
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
		//if(getCurrentWorm() != null)
		//	getCurrentWorm().shoot(getWeaponIndicator().getAngle());
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

		data.wormData = new Worm.SnapshotData[worms.size()];
		data.weaponData = new Weapon.SnapshotData[weapons.size()];

		for (int i = 0; i < worms.size(); i++)
			if (worms.get(i) != null)
				data.wormData[i] = worms.get(i).makeSnapshot();

		for (int i = 0; i < weapons.size(); i++)
			data.weaponData[i] = weapons.get(i).makeSnapshot();

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
