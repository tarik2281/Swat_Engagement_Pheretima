package de.paluno.game.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import de.paluno.game.AnimatedSprite;
import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.Weapon;
import de.paluno.game.gameobjects.WeaponType;
import de.paluno.game.gameobjects.Worm;

public class WormTest2 {
	
	private Worm wormTest;
	private Weapon weapon_Mock;
	private Player player_Mock;

	@Before
	public void setUp() {
		player_Mock = mock(Player.class);
		
		weapon_Mock = mock(Weapon.class);
		when(weapon_Mock.getWeaponType()).thenReturn(WeaponType.WEAPON_GRENADE);
		when(weapon_Mock.createAnimatedSprite()).thenReturn(mock(AnimatedSprite.class));

		wormTest = new Worm(player_Mock, 2);
		wormTest.equipWeapon(weapon_Mock);
	}

	@Test
	public void testEquipWeapon() {
		Weapon weapon = weapon_Mock;
		assertTrue("Der Wurm wurde mit der falschen Waffe ausgerüstet.", 
							weapon.getWeaponType() == WeaponType.WEAPON_GRENADE);
	}
}
