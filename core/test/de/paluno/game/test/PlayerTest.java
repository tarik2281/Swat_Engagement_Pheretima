package de.paluno.game.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.Weapon;
import de.paluno.game.gameobjects.WeaponType;

public class PlayerTest {
	
	private Player playerTest;
	private Weapon weapon_Mock;

	@Before
	public void setUp(){
		playerTest = new Player(1);
		weapon_Mock = new Weapon(WeaponType.WEAPON_AIRSTRIKE);
		playerTest.addWeapon(weapon_Mock);
	}

	@Test
	public void testGetWeapon() {
		Weapon weapon = playerTest.getWeapon(weapon_Mock.getWeaponType());
		assertTrue("Falsche Waffe wurde angewählt", weapon.getWeaponType() == WeaponType.WEAPON_AIRSTRIKE);
	}

}
