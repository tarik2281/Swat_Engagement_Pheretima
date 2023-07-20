package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import de.paluno.game.Constants;
import de.paluno.game.SEPGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		int refreshRate = Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate;

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(600, 400);
		config.setTitle("SEP Game");
		config.setForegroundFPS(refreshRate);
		config.setHdpiMode(HdpiMode.Pixels);
//		config.useHDPI = true;
		Constants.REFRESH_RATE = 1.0f / (float)refreshRate;
		new Lwjgl3Application(new SEPGame(), config);
	}
}
