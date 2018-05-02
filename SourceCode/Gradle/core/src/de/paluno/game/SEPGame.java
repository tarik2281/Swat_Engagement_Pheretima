package de.paluno.game;

import com.badlogic.gdx.Game;

import de.paluno.game.screens.PlayScreen;
import de.paluno.game.screens.GameOverScreen;
import de.paluno.game.screens.WinningPlayer;

public class SEPGame extends Game {
	
	public SEPGame() {

	}

	@Override
	public void create() {
		setScreen(new GameOverScreen(this, WinningPlayer.PLAYERONE));
	}
}
