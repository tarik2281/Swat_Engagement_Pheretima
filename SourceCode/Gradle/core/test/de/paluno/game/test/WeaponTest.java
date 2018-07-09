package de.paluno.game.test;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.audio.Sound;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.Projectile;
import de.paluno.game.gameobjects.Weapon;
import de.paluno.game.gameobjects.WeaponIndicator;
import de.paluno.game.gameobjects.WeaponType;
import de.paluno.game.gameobjects.Worm;

public class WeaponTest {
	Weapon weaponToTest;
	Player player_Mock;
	Worm worm_Mock;
	WeaponIndicator indicator_Mock;
	Sound gunShot;
	
	@Before
	public void setUp() {
		player_Mock = mock(Player.class);
		worm_Mock = mock(Worm.class);
		indicator_Mock = mock(WeaponIndicator.class);
	}

	@Test
	public void testShoot() {
		List<Projectile> output_Test = new ArrayList<Projectile>();
		weaponToTest = new Weapon(WeaponType.WEAPON_GUN);
		weaponToTest.shoot(worm_Mock, indicator_Mock, output_Test);
		assertTrue("Die Ammo-Anzahl stimmt nicht.", -2 == weaponToTest.getCurrentAmmo());
	}

}
