package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.paluno.game.Constants;
import de.paluno.game.SEPGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1350;
		config.height = 900;
		config.foregroundFPS = 144;
		Constants.REFRESH_RATE = 1.0f / 144.0f;
		new LwjglApplication(new SEPGame(), config);
	}
}
