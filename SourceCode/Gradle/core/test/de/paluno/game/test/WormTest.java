package de.paluno.game.test;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.assets.AssetManager;

import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.World;
import de.paluno.game.gameobjects.Worm;

class WormTest {
	
	Worm wormToTest;
	Player player_Mock;
	
	@Before
	public void setUp(){
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
	}
	
	@Test
	void testTakeDamage(){
		setUp();
		wormToTest = new Worm(player_Mock, 2);
		wormToTest.takeDamage(10);
		assertTrue("Damage falsch kalkuliert", 90 == wormToTest.getHealth());
	}

}
