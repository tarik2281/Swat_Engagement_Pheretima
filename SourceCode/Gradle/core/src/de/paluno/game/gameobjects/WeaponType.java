package de.paluno.game.gameobjects;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import de.paluno.game.AnimationData;
import de.paluno.game.Assets;
import de.paluno.game.Constants;

public enum WeaponType {
	/**
	 * Long story short: Aaaaaaall the fixed Weapon stats
	 * MaxAmmo, Icon, Animation, Projectile Icon, shoot strength, explosion radius, explosion impulse, explosion time, damage
	 */
	WEAPON_BAZOOKA(Constants.WEAPON_AMMO_INF, Assets.iconBazooka, Assets.weaponBazooka, Assets.projectileBazooka,
			7.0f, 0.35f, 0.003f, 0.0f, 30, "Bazooka"),
	WEAPON_GUN(Constants.WEAPON_AMMO_INF, Assets.iconGun, Assets.weaponGun, Assets.projectileGun,
			7.0f, 0.0f, 0.0f, 0.0f, 40, "Pistole"),
	WEAPON_GRENADE(Constants.WEAPON_AMMO_INF, Assets.iconGrenade, Assets.weaponGrenade, Assets.projectileGrenade,
			7.0f, 0.5f, 0.003f, 3.0f, 50, "Granate"),
	WEAPON_SPECIAL(3, Assets.iconSpecial, Assets.weaponSpecial, Assets.projectileSpecial,
			7.0f, 0.35f, 0.0f, 0.0f, 0, "Virus");

	public static final int NUM_WEAPONS = 4;

	private final int maxAmmo;
	private final AssetDescriptor<Texture> icon;
	private final AssetDescriptor<AnimationData> weapon;
	private final AssetDescriptor<Texture> projectile;
	private final float shootingImpulse;
	private final float explosionRadius;
	private final float explosionBlastPower;
	private final float explosionTime;
	private final float damage;
	private final String name;

	/**
	 * Set Weapon parameters if created with this
	 * @param maxAmmo
	 * @param icon
	 * @param weapon
	 * @param projectile
	 * @param shootingImpulse
	 * @param explosionRadius
	 * @param explosionBlastPower
	 * @param explosionTime
	 * @param damage
	 * @param name
	 */
	WeaponType(int maxAmmo, AssetDescriptor<Texture> icon, AssetDescriptor<AnimationData> weapon, AssetDescriptor<Texture> projectile,
			   float shootingImpulse, float explosionRadius, float explosionBlastPower, float explosionTime, float damage, String name) {
		this.maxAmmo = maxAmmo;
		this.icon = icon;
		this.weapon = weapon;
		this.projectile = projectile;
		this.shootingImpulse = shootingImpulse;
		this.explosionRadius = explosionRadius;
		this.explosionBlastPower = explosionBlastPower;
		this.explosionTime = explosionTime;
		this.damage = damage;
		this.name = name;
	}

	/**
	 * Getter method for max ammo
	 * @return maxAmmo
	 */
	public int getMaxAmmo() {
		return maxAmmo;
	}

	public AssetDescriptor<Texture> getIconAsset() {
		return icon;
	}

	public AssetDescriptor<AnimationData> getWeaponAsset() {
		return weapon;
	}

	public AssetDescriptor<Texture> getProjectileAsset() {
		return projectile;
	}

	public float getShootingImpulse() {
		return shootingImpulse;
	}

	public float getExplosionRadius() {
		return explosionRadius;
	}

	public float getExplosionBlastPower() {
		return explosionBlastPower;
	}

	public float getExplosionTime() {
		return explosionTime;
	}

	public float getDamage() {
		 return damage;
	}
	
	public String getName() {return name;}
}
