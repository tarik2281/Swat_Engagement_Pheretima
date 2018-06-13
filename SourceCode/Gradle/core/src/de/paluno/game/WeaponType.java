package de.paluno.game;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;

public enum WeaponType {
	WEAPON_BAZOOKA(Constants.WEAPON_AMMO_INF, Assets.iconBazooka, Assets.weaponBazooka, Assets.projectileBazooka,
			7.0f, 0.35f, 0.003f, 0.0f, 30),
	WEAPON_GUN(Constants.WEAPON_AMMO_INF, Assets.iconGun, Assets.weaponGun, Assets.projectileGun,
			7.0f, 0.0f, 0.0f, 0.0f, 40),
	WEAPON_GRENADE(Constants.WEAPON_AMMO_INF, Assets.iconGrenade, Assets.weaponGrenade, Assets.projectileGrenade,
			7.0f, 0.5f, 0.003f, 3.0f, 50),
	WEAPON_SPECIAL(3, Assets.iconSpecial, Assets.weaponSpecial, Assets.projectileSpecial,
			7.0f, 0.35f, 0.0f, 0.0f, 0);

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

	WeaponType(int maxAmmo, AssetDescriptor<Texture> icon, AssetDescriptor<AnimationData> weapon, AssetDescriptor<Texture> projectile,
			   float shootingImpulse, float explosionRadius, float explosionBlastPower, float explosionTime, float damage) {
		this.maxAmmo = maxAmmo;
		this.icon = icon;
		this.weapon = weapon;
		this.projectile = projectile;
		this.shootingImpulse = shootingImpulse;
		this.explosionRadius = explosionRadius;
		this.explosionBlastPower = explosionBlastPower;
		this.explosionTime = explosionTime;
		this.damage = damage;
	}

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
}
