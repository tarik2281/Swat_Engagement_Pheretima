package de.paluno.game.test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.assets.AssetManager;

import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.GameWorld;
import de.paluno.game.gameobjects.Worm;

public class WormTest {
	
	Worm wormToTest;
	Player player_Mock;
	
	@Before
	public void setUp(){
		player_Mock = mock(Player.class);
	}
	
	@Test
	public void testTakeDamage(){
		wormToTest = new Worm(player_Mock, 2, "Spieler");
		wormToTest.takeDamage(10, 1);
		assertTrue("Damage falsch kalkuliert", 90 == wormToTest.getHealth());
	}

}
