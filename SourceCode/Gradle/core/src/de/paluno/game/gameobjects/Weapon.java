package de.paluno.game.gameobjects;

import com.badlogic.gdx.graphics.g2d.Sprite;

import de.paluno.game.AnimatedSprite;
import de.paluno.game.Assets;
import de.paluno.game.Constants;
  

public class Weapon {
	private WeaponType type;
	private int maxAmmo;
	private int currentAmmo;
	
	private boolean affectedByGravity;
	private boolean bounce;
	private float speed;
	private boolean special;
	
	private AnimatedSprite animationSet;
	
	private String name;
	private String description;
	private Sprite icon;
	
	private Worm worm;
	private Projectile projectile = null;
	
	/**
	 * Overloaded constructor - Only WeaponType
	 * @param type - WeaponType of the weapon, to determine it's behavior
	 */
	public Weapon(Worm worm, WeaponType type) {
		this(worm, type, "Waffe", "Macht Schaden.", null, Constants.WEAPON_AMMO_INF);
	}
	/**
	 * Overloaded constructor - no ammo
	 * @param type - WeaponType of the weapon, to determine it's behavior
	 * @param name - Name of the weapon, for later selection ingame
	 * @param description - Description of the weapon, for later selection ingame
	 * @param icon - Sprite for the selection icon
	 */
	public Weapon(Worm worm, WeaponType type, String name, String description, Sprite icon) {
		this(worm, type, name, description, icon, Constants.WEAPON_AMMO_INF);
	}
	/**
	 * Constructor
	 * @param type - WeaponType of the weapon, to determine it's behavior
	 * @param name - Name of the weapon, for later selection ingame
	 * @param description - Description of the weapon, for later selection ingame
	 * @param icon - Sprite for the selection icon
	 * @param maxAmmo - Max ammo this weapon has
	 */
	public Weapon(Worm worm, WeaponType type, String name, String description, Sprite icon, int maxAmmo) {
		this.worm = worm;
		this.type = type;
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.maxAmmo = this.currentAmmo = maxAmmo;
		
		setupWeaponParameters();
	}
	
	/**
	 * Method to set the weapon behavior parameters based on it's type
	 */
	private void setupWeaponParameters() {
		// Switch through presets of weapon types and set behavior that way
		switch(this.type) {
			default: case WEAPON_RIFLE:
				this.speed = Constants.WEAPON_RIFLE_SPEED;
				this.bounce = false;
				this.affectedByGravity = false;
				this.special = false;
				
				this.animationSet = new AnimatedSprite(this.worm.getAssets().get(Assets.weaponRifle));
				break;
			case WEAPON_PROJECTILE:
				this.speed = Constants.WEAPON_PROJECTILE_SPEED;
				this.bounce = false;
				this.affectedByGravity = true;
				this.special = false;
				
				this.animationSet = new AnimatedSprite(this.worm.getAssets().get(Assets.weaponLauncher));
				break;
			case WEAPON_THROWABLE:
				this.speed = Constants.WEAPON_THROWABLE_SPEED;
				this.bounce = true;
				this.affectedByGravity = true;
				this.special = false;
				
				this.animationSet = new AnimatedSprite(this.worm.getAssets().get(Assets.weaponGrenade));
				break;
			case WEAPON_SPECIAL:
				this.speed = Constants.WEAPON_RIFLE_SPEED;
				this.bounce = false;
				this.affectedByGravity = false;
				this.special = true;
				
				this.animationSet = new AnimatedSprite(this.worm.getAssets().get(Assets.weaponRifle));
				break;
		}
	}
	
	/**
	 * Method to generate a projectile if allowed to
	 */
	public void shoot() {
		// There still is a projectile - don't spawn another one!
		if(this.projectile != null) return;
		
		// Only fire if we have ammo left!
		if(this.maxAmmo != Constants.WEAPON_AMMO_INF) {
			if(this.currentAmmo > 0) {
				this.projectile = new Projectile(null, null, null);
				this.currentAmmo--;
			}
		}
	}
	/**
	 * Null-setter Method to remove a projectile upon hit
	 */
	public void hit() {this.projectile = null;}
	
	/**
	 * Getter method for the projectile speed of this weapon
	 * @return speed
	 */
	public float getSpeed() {return this.speed;}
	/**
	 * Getter method for the projectile bounce of this weapon
	 * @return bounce
	 */
	public boolean getBounce() {return this.bounce;}
	/**
	 * Getter method for the projectile's gravity affection of this weapon
	 * @return affectedByGravity
	 */
	public boolean getAffectedByGravity() {return this.affectedByGravity;}
	/**
	 * Getter method for the projectile's special state of this weapon
	 * @return special
	 */
	public boolean getSpecialState() {return this.special;}
	/**
	 * Shortcut getter method to return all projectile stats in one mixed array
	 * @return Array<Mixed> speed, bounce, affectedByGravity, special
	 */
	public Object[] getProjectileStats() {return new Object[] {speed, bounce, affectedByGravity, special};}
	
	/**
	 * Getter method for the weapon's name
	 * @return name
	 */
	public String getName() {return this.name;}
	/**
	 * Getter method for the weapon's description
	 * @return description
	 */
	public String getDescription() {return this.description;}
	/**
	 * Getter method for the weapon's icon
	 * @return icon
	 */
	public Sprite getIcon() {return this.icon;}
	/**
	 * Shortcut getter method for every weapon stat
	 * @return Array<Mixed> name, description, icon
	 */
	public Object[] getWeaponStats() {return new Object[] {name, description, icon};}
	
	/**
	 * Getter method for this weapon's animation
	 * @return AnimatedSprite
	 */
	public AnimatedSprite getAnimation() {return this.animationSet;}
}
