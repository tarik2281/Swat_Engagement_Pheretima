package de.paluno.game.test;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.Body;

import de.paluno.game.gameobjects.WeaponType;
import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.Weapon;
import de.paluno.game.gameobjects.World;
import de.paluno.game.gameobjects.Worm;

class WeaponTest {
	
	Weapon weaponToTest;
	Player player_Mock;
	Worm worm_Mock;
	
	@Before
	public void setUp() {
		player_Mock = new Player() {
			@Override
			public World getWorld() {
				return null;
			}
			
			@Override
			public AssetManager getAssets() {
				return null;
			}
		};
		
		worm_Mock = new Worm() {
			@Override
			public Body getBody() {
				return null;
			}
		};
	}

	@Test
	void testShoot() {
		setUp();
		weaponToTest = new Weapon(player_Mock, WeaponType.WEAPON_GUN);
		weaponToTest.shoot(worm_Mock, 2.713f);
		assertTrue("Die Ammo-Anzahl stimmt nicht", -2 == weaponToTest.getCurrentAmmo());
	}

}
