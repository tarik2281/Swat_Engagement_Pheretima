package de.paluno.game.gameobjects;

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

	private ArrayList<Worm> worms;
	private int numWormsAlive;
	private ArrayList<Weapon> weapons;
	private Weapon currentWeapon;
	private int turn = -1;
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

	public void addWeapon(Weapon weapon) {
		weapons.add(weapon);
	}

	public Worm addWorm(int wormNumber) {
		Worm worm = new Worm(this, wormNumber);
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
	public Worm getCurrentWorm() {
		if (turn == -1)
			return null;

		return this.worms.get(this.turn);
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
	 * Method to generate a given debug test Airdrop
	 * @param keycode - The key registered for a certain weapon type
	 */
	public void spawnDebugDrop(int keycode) {
		WeaponType dropWeapon;
		switch(keycode) {
			default: case Constants.KEY_DEBUG_DROP_TURRET: dropWeapon = WeaponType.WEAPON_SPECIAL;
		}
		world.spawnDebugDrop(dropWeapon, this.getCurrentWorm().getBody().getPosition().x);
	}

	/**
	 * Method to add one shot to a given picked up weapon
	 * @param weapon - The WeaponType of the weapon
	 */
	public void addAmmo(WeaponType weapon) {
		for(Weapon check : this.weapons) {
			if(check.getWeaponType() == weapon) {
				check.addAmmo(1);
				break;
			}
		}
	}
}
