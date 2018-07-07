package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.paluno.game.Constants;
import de.paluno.game.SEPGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		int refreshRate = LwjglApplicationConfiguration.getDesktopDisplayMode().refreshRate;

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1350;
		config.height = 900;
		config.foregroundFPS = refreshRate;
		Constants.REFRESH_RATE = 1.0f / (float)refreshRate;
		new LwjglApplication(new SEPGame(), config);
	}
}
