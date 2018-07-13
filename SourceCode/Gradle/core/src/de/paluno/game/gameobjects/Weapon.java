package de.paluno.game.gameobjects;

import com.badlogic.gdx.audio.Sound;
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

	private Player player;

	private Sound noAmmoSound;

	private WeaponType type;
	private int currentAmmo;
	private float airstrikeHeight;
	//private Vector2 curserPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());

	private AnimationData animationSet;

	/**
	 * Constructor
	 * @param type - WeaponType of the weapon, to determine it's behavior
	 */
	public Weapon(WeaponType type) {
		this.type = type;

		this.currentAmmo = type.getMaxAmmo();
	}

	public void setupAssets(AssetManager manager) {
		animationSet = manager.get(type.getWeaponAsset());
		noAmmoSound = manager.get(Assets.noAmmo);
	}

	public void setAirstrikeHeight(float airstrikeHeight) {
		this.airstrikeHeight = airstrikeHeight;
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
			EventManager.getInstance().queueEvent(EventManager.Type.WeaponShoot, type);
			if (getWeaponType() == WeaponType.WEAPON_AIRSTRIKE) {
				Vector2 position = indicator.getPosition();

				Projectile projectile = new Projectile(worm,
						this.type, Constants.AIRSTRIKE_SPAWNPOS,
						 new Vector2(-Constants.AIRSTRIKE_SPAWNPOS.x + position.x,
								 		-airstrikeHeight + position.y).nor());

				Projectile projectile2 = new Projectile(worm,
						this.type, Constants.AIRSTRIKE_SPAWNPOS2 ,
						 new Vector2(-Constants.AIRSTRIKE_SPAWNPOS.x + position.x,
								 		-airstrikeHeight + position.y).nor());

				Projectile projectile3 = new Projectile(worm,
						this.type, Constants.AIRSTRIKE_SPAWNPOS3,
						 new Vector2(-Constants.AIRSTRIKE_SPAWNPOS.x + position.x,
								 		-airstrikeHeight + position.y).nor());

				output.add(projectile);
				output.add(projectile2);
				output.add(projectile3);
			}
			else if(getWeaponType() == WeaponType.WEAPON_TURRET) {
				Vector2 position = new Vector2(worm.getPosition().x + worm.getOrientation() * 0.5f, worm.getPosition().y);
				Turret turret = new Turret(worm, type, position, new Vector2());

				output.add(turret);
			}
			// setTransform Method spawns the current Worm to the selected position
			else if (type == WeaponType.WEAPON_TELEPORTER){
				EventManager.getInstance().queueEvent(EventManager.Type.TeleporterUse, type);
                worm.setPosition(indicator.getPosition());
            }
			else {
				Vector2 direction;
				if (indicator != null)
					direction = new Vector2(1, 0).rotate(indicator.getAngle());
				else
					direction = new Vector2();

				Projectile projectile = new Projectile(worm, type, worm.getPosition(), direction);
				output.add(projectile);
			}

			currentAmmo--;
		}else {
			noAmmoSound.play();
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

	/**
	 * Method to increase the amount of available ammo
	 * @param amount - The amount to increase
	 */
	public void addAmmo(int amount) {this.currentAmmo += amount;}
}
