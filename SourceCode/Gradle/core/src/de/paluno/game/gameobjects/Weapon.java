package de.paluno.game.gameobjects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.*;

import java.util.List;

public class Weapon {

	/**
     * Inner class to create a copy of the data necessary for the replay
     */
	public class SnapshotData {
		private WeaponType type;
		private int currentAmmo;
	}

	private WeaponType type;
	private int currentAmmo;
	
	private AnimationData animationSet;

	/**
	 * Constructor
	 * @param player - Reference to the player we belong to
	 * @param type - WeaponType of the weapon, to determine it's behavior
	 */
	public Weapon(WeaponType type) {

		this.type = type;

		this.currentAmmo = type.getMaxAmmo();

		//if (player.getAssets() != null)
		//animationSet = player.getAssets().get(type.getWeaponAsset());
	}

	public void setupAssets(AssetManager manager) {
		animationSet = manager.get(type.getWeaponAsset());
	}
	/**
	 * Constructor to create a new Weapon from given data
	 * @param player - Reference to the (copied) Player we belong to
	 * @param data - SnapshotData object to copy from
	 */
	public Weapon(Player player, SnapshotData data) {

		this.type = data.type;

		this.currentAmmo = data.currentAmmo;

		//animationSet = player.getAssets().get(type.getWeaponAsset());
	}

	/**
	 * Method to generate a projectile if allowed to
	 */
	public void shoot(Worm worm, float angle) {
		if (type.getMaxAmmo() == Constants.WEAPON_AMMO_INF || currentAmmo > 0) {
			Vector2 direction = new Vector2(1, 0).rotate(angle);

			if (worm.getBody() != null) {
				/*Projectile projectile = new Projectile(player.getWorld(), worm,
						this.type, worm.getBody().getPosition(), direction);

				player.getWorld().spawnProjectile(projectile);*/
			}

			currentAmmo--;
		}
	}

	public void shoot(Worm worm, WeaponIndicator indicator, List<Projectile> output) {
		if (type.getMaxAmmo() == Constants.WEAPON_AMMO_INF || currentAmmo > 0) {
			Vector2 direction = new Vector2(1, 0).rotate(indicator.getAngle());
			Projectile projectile = new Projectile(worm, type, worm.getPosition(), direction);
			output.add(projectile);

			currentAmmo--;
		}
	}
	
	/**
	 * Getter method to get our current ammo
	 * @return currentAmmo
	 */
	public int getCurrentAmmo() {
		return currentAmmo;
	}

	/**
	 * Getter method to get the WeaponType reference this weapon is based on
	 * @return WeaponType
	 */
	public WeaponType getWeaponType() {
		return type;
	}

	/**
	 * Getter method for the weapon's availability
	 * @return selectable
	 */
	public boolean getSelectable() {
		return type.getMaxAmmo() == Constants.WEAPON_AMMO_INF || currentAmmo > 0;
	}
	
	/**
	 * Soft getter method for this weapon's animation set
	 * @return Animation set as AnimatedSprite, generated from the Asset reference
	 */
	public AnimatedSprite createAnimatedSprite() {
		return new AnimatedSprite(animationSet);
	}
	
	/**
	 * Method to create and fill our SnapshotData copy
	 * @return SnapshotData
	 */
	public SnapshotData makeSnapshot() {
		SnapshotData data = new SnapshotData();

		data.currentAmmo = currentAmmo;
		data.type = type;

		return data;
	}
}
