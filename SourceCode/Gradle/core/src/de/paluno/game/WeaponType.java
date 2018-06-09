package de.paluno.game;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;

public enum WeaponType {
	WEAPON_BAZOOKA(Constants.WEAPON_AMMO_INF, Assets.iconBazooka,
			Assets.weaponBazooka, Assets.projectileBazooka),
	WEAPON_GUN(Constants.WEAPON_AMMO_INF, Assets.iconGun,
			Assets.weaponGun, Assets.projectileGun),
	WEAPON_GRENADE(Constants.WEAPON_AMMO_INF, Assets.iconGrenade,
			Assets.weaponGrenade, Assets.projectileGrenade),
	WEAPON_SPECIAL(Constants.WEAPON_AMMO_INF, Assets.iconSpecial, Assets.weaponSpecial, Assets.projectileSpecial);

	public static final int NUM_WEAPONS = 4;

	private final int maxAmmo;
	private final AssetDescriptor<Texture> icon;
	private final AssetDescriptor<AnimationData> weapon;
	private final AssetDescriptor<Texture> projectile;

	WeaponType(int maxAmmo, AssetDescriptor<Texture> icon, AssetDescriptor<AnimationData> weapon, AssetDescriptor<Texture> projectile) {
		this.maxAmmo = maxAmmo;
		this.icon = icon;
		this.weapon = weapon;
		this.projectile = projectile;
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
}
