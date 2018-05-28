package de.paluno.game.screens;

import com.badlogic.gdx.physics.box2d.World;

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
		try {
			return (Player)super.clone();
			
		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			return player;
		}
		
	}
	public Projectile makesnapshotprojectile() {
		try {
			return (Projectile)super.clone();
			
		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			return projectile;
		}
	
		
	}
	public ShotDirectionIndicator makesnapshotshotdirectionindicator() {
		try {
			return (ShotDirectionIndicator)super.clone();
			
		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			return shotdirectionindicator;
		}
		
	}
	public Worm makesnapshotwurm() {
		return worm.clone;
		
		
		
	}
	public Virus makesnapshotvirus() {
		try {
			return (Virus)super.clone();
			
		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			return virus;
		}
		
	}
	public WindDirectionIndicator  makesnapshotwinddirectionindicator() {
		try {
			return (WindDirectionIndicator)super.clone();
			
		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			return winddirectionindicator;
		}
		
	}
	public Weapon makesnapshotweapon() {
		try {
			return (Weapon)super.clone();
			
		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
			return weapon;
		}
	

}
	
}	
