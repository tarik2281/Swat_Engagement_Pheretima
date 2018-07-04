package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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

	private Player player;
	
	private Sound gunShotSound;
	private Sound bazookaShotSound;
	private Sound airstrikeSound;
	private Sound noAmmoSound;
	private Sound throwSound;
	private Sound targetSound;
	
	private WeaponType type;
	private int currentAmmo;
	//private Vector2 curserPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
	
	private AnimationData animationSet;

	/**
	 * Constructor
	 * @param player - Reference to the player we belong to
	 * @param type - WeaponType of the weapon, to determine it's behavior
	 */
	public Weapon(WeaponType type) {

		this.type = type;

		this.currentAmmo = type.getMaxAmmo();
	}

	public void setupAssets(AssetManager manager) {
		animationSet = manager.get(type.getWeaponAsset());
		gunShotSound = manager.get(Assets.gunShotSound);
		bazookaShotSound = manager.get(Assets.bazookaShotSound);
		airstrikeSound = manager.get(Assets.airstrikeSound);
		noAmmoSound = manager.get(Assets.noAmmo);
		throwSound = manager.get(Assets.throwSound);
		targetSound = manager.get(Assets.targetSound);
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

	public void shoot(Worm worm, WeaponIndicator indicator, List<Projectile> output) {
		if (type.getMaxAmmo() == Constants.WEAPON_AMMO_INF || currentAmmo > 0) {
			if (getWeaponType() == WeaponType.WEAPON_AIRSTRIKE) {
				targetSound.play(0.3f);
				airstrikeSound.play(0.4f);
				
				Vector2 position = indicator.getPosition();
				
				Projectile projectile = new Projectile(worm, 
						this.type, Constants.AIRSTRIKE_SPAWNPOS, 
						 new Vector2(-Constants.AIRSTRIKE_SPAWNPOS.x + position.x, 
								 		-Constants.AIRSTRIKE_SPAWNPOS.y + position.y).nor());
				
				Projectile projectile2 = new Projectile(worm, 
						this.type, Constants.AIRSTRIKE_SPAWNPOS2 , 
						 new Vector2(-Constants.AIRSTRIKE_SPAWNPOS.x + position.x, 
								 		-Constants.AIRSTRIKE_SPAWNPOS.y + position.y).nor());
				
				Projectile projectile3 = new Projectile(worm, 
						this.type, Constants.AIRSTRIKE_SPAWNPOS3, 
						 new Vector2(-Constants.AIRSTRIKE_SPAWNPOS.x + position.x, 
								 		-Constants.AIRSTRIKE_SPAWNPOS.y + position.y).nor());
				
				output.add(projectile);
				output.add(projectile2);
				output.add(projectile3);
			}else if(getWeaponType() == WeaponType.WEAPON_TURRET) {
				Projectile projectile = new Projectile(worm,
						this.type, Constants.AIRSTRIKE_SPAWNPOS,
						Constants.AIRSTRIKE_SPAWNPOS2);
				
				output.add(projectile);
			}
			
			else {
				Vector2 direction = new Vector2(1, 0).rotate(indicator.getAngle());
				Projectile projectile = new Projectile(worm, type, worm.getPosition(), direction);
				output.add(projectile);
				
				switch(projectile.getWeaponType()){
					case WEAPON_GUN:
						gunShotSound.play(0.7f);
						break;
					case WEAPON_BAZOOKA:
						bazookaShotSound.play(0.2f);
						break;
					case WEAPON_SPECIAL:
						throwSound.play(0.4f);
						break;
					case WEAPON_GRENADE:
						throwSound.play(0.4f);
						break;
				}
			}

			currentAmmo--;
		}else {
			noAmmoSound.play(0.2f);
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
