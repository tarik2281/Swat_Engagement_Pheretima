package de.paluno.game;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import de.paluno.game.screens.*;

public class SEPGame extends Game {

	private AssetManager assetManager;

	private EventManager.Listener listener = (type, data) -> {
		switch (type) {
			case GameOver:
				setGameOver((String)data);
				break;
		}
	};

	public SEPGame() {
		assetManager = new AssetManager();
        assetManager.setLoader(AnimationData.class, new AnimationData.Loader(new InternalFileHandleResolver()));
        assetManager.setLoader(Map.class, new Map.Loader(new InternalFileHandleResolver()));
	}

	@Override
	public void create() {
		EventManager.getInstance().addListener(listener, EventManager.Type.GameOver);

		setMenuScreen();
	}

    @Override
    public void render() {
		EventManager.getInstance().processEvents();

        super.render();
    }

    public void setMenuScreen() {
		setNextScreen(new MenuScreen(this));
	}

    public void setPlayScreen(int mapNumber, int numWorms, int numPlayers) {
		setNextScreen(new PlayScreen(this, mapNumber, numWorms, numPlayers));
	}

    public void setGameOver(String name) {
	    setNextScreen(new GameOverScreen(this, name));
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
