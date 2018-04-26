package de.paluno.game;

import com.badlogic.gdx.Game;
import de.paluno.game.screens.PlayScreen;

public class SEPGame extends Game {
	
	public SEPGame() {

	}

	@Override
	public void create() {
		setScreen(new PlayScreen(this));
	}
}
