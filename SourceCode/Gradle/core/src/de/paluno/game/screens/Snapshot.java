package de.paluno.game.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import de.paluno.game.WeaponType;
import de.paluno.game.WindDirectionIndicator;
import de.paluno.game.gameobjects.Ground;
import de.paluno.game.gameobjects.HealthBar;
import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.Projectile;
import de.paluno.game.gameobjects.ShotDirectionIndicator;
import de.paluno.game.gameobjects.Virus;
import de.paluno.game.gameobjects.Weapon;
import de.paluno.game.gameobjects.Worm;

public class Snapshot implements Cloneable {
	World world;
    Ground ground;
    HealthBar healthbar;
    Player player;
    Projectile projectile;
    ShotDirectionIndicator shotdirectionindicator;
    Worm worm;
    Virus virus;
    Weapon weapon;
    WindDirectionIndicator winddirectionindicator;
    PlayScreen playscreen;
    WeaponType weapontype;
    int zahl;
    private Vector2 position;
    private Vector2 direction;
    private AssetManager assets;
    int playernum;
    
	public Snapshot(World world) {
		this.world=world;
	}
	
	public Ground makesnapshotground() {
		try {
			return (Ground)super.clone();
			
		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			return ground;
		}
		
		
	}
	public HealthBar makesnapshotHealhtbar() {
		try {
			return (HealthBar)super.clone();
			
		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			return healthbar;
		}
		
	}
	public Player makesnapshotplayer() {
		
		 Player clone = new Player(playernum,world,assets);
			clone.setCloningParameters(player);
		return clone;
	}
	public Projectile cloneprojectile() {
		 Projectile clone = new Projectile(playscreen,position,direction);
			clone.setCloningParameters(projectile);
		return clone;
		 
	
		
	}
	public ShotDirectionIndicator cloneshotdirectionindicator() {
		
		 ShotDirectionIndicator clone = new ShotDirectionIndicator(zahl,worm,playscreen);
			clone.setCloningParameters(shotdirectionindicator);
		return clone;
		 
	}
	 public Virus clonevirus() {
		 Virus clone = new Virus(worm,playscreen);
			clone.setCloningParameters(virus);
		return clone;
		 
	 }
	
	
	public WindDirectionIndicator  clonewinddirectionindicator() {
		 
			 WindDirectionIndicator clone = new WindDirectionIndicator();
				clone.setCloningParameters(winddirectionindicator);
			return clone;
			 
		 }
		
	
	 public Weapon cloneweapon() {
		 Weapon clone = new Weapon(worm,weapontype);
			clone.setCloningParameters(weapon);
		return clone;
		 
	 }
	
}	
