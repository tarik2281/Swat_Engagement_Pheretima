package de.paluno.game.gameobjects;

import com.badlogic.gdx.math.Vector2;
import de.paluno.game.*;

public class Weapon {

	private Player player;

	private WeaponType type;
	private int currentAmmo;
	
	private AnimationData animationSet;

	/**
	 * Overloaded constructor - Only WeaponType
	 * @param type - WeaponType of the weapon, to determine it's behavior
	 */
	public Weapon(Player player, WeaponType type) {
		this.player = player;

		this.type = type;

		animationSet = player.getAssets().get(type.getWeaponAsset());
	}

	/**
	 * Method to generate a projectile if allowed to
	 */
	public void shoot(Worm worm, float angle) {
		if (type.getMaxAmmo() == Constants.WEAPON_AMMO_INF || currentAmmo > 0) {
			Vector2 direction = new Vector2(1, 0).rotate(angle);

			Projectile projectile = new Projectile(player.getWorld(), worm,
					this.type, worm.getBody().getPosition(), direction);

			player.getWorld().spawnProjectile(projectile);

			currentAmmo--;
		}
	}

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

	public AnimatedSprite createAnimatedSprite() {
		return new AnimatedSprite(animationSet);
	}

	public void setCloningParameters(Weapon clone) {

	}
}
