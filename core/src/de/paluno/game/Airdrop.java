package de.paluno.game;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;

import de.paluno.game.gameobjects.AirdropChute;
import de.paluno.game.gameobjects.AirdropCrate;
import de.paluno.game.gameobjects.GameWorld;
import de.paluno.game.gameobjects.WeaponType;

public class Airdrop implements GameEvent {

	//private World world;
	
	/**
	 * Empty contructor
	 */
	public Airdrop() {}
	
	/**
	 * Method to generate a new event result (here: spawn a new crate on drop event)
	 * @param world - Reference to the world this happens in
	 */
	@Override
	public Object trigger(GameWorld world) {
		//this.world = world;
		
		// Get a new random spawnpoint
		Vector2 spawn = null;//world.generateDropPosition();
		
		// Select a new random drop content
		WeaponType[] possibleDrops = WeaponType.values();
		int i = ThreadLocalRandom.current().nextInt(0, possibleDrops.length);
		WeaponType drop = possibleDrops[i];
		System.out.println("Crate spawned - i = "+i+" - Weapon is "+drop.getName());
		
		// Create the Airdrop
		AirdropCrate crate = new AirdropCrate( spawn, drop);
		world.registerAfterUpdate(crate);
		AirdropChute chute = new AirdropChute(crate);
		crate.setChute(chute);
		world.registerAfterUpdate(chute);
		//world.getCamera().setCameraFocus(crate);
		return crate;
	}
	/**
	 * Debug function to generate given drops at given coordinates
	 * @param world - Reference to the world this happens in
	 * @param spawn - Given spawn coordinates
	 * @param drop - Given loot content
	 */
	/*public void debugSpawn(World world, Vector2 spawn, WeaponType drop) {
		System.out.println("Debug crate spawn called!");
		this.world = world;
		
		// Create the Airdrop
		AirdropCrate crate = new AirdropCrate(spawn, drop);
		world.registerAfterUpdate(crate);
		AirdropChute chute = new AirdropChute(crate);
		crate.setChute(chute);
		world.registerAfterUpdate(chute);
		world.getCamera().setCameraFocus(crate);
	}*/
}
