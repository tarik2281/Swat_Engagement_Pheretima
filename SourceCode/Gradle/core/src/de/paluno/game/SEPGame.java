package de.paluno.game;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import de.paluno.game.screens.*;

public class SEPGame extends Game {

	private AssetManager assetManager;

	public SEPGame() {
		assetManager = new AssetManager();
        assetManager.setLoader(AnimationData.class, new AnimationData.Loader(new InternalFileHandleResolver()));
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
	}

	@Override
	public void create() {
		setMenuScreen();
	}

    @Override
    public void render() {
        super.render();
    }

    public void setMenuScreen() {
		setNextScreen(new TeamMenuScreen(this));
	}

    public void setPlayScreen(int mapNumber, int numWorms) {
		setNextScreen(new PlayScreen(this, mapNumber, numWorms));
	}

    public void setGameOver(WinningPlayer winningPlayer) {
	    setNextScreen(new GameOverScreen(this, winningPlayer));
    }

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public void setNextScreen(Screen screen) {
		// TODO: maybe loading screen
		assetManager.clear();

		if (screen instanceof Loadable) {
			((Loadable) screen).load(assetManager);
			assetManager.finishLoading();
		}

		setScreen(screen);
	}
}
