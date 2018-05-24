package de.paluno.game;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import de.paluno.game.screens.GameOverScreen;
import de.paluno.game.screens.Loadable;
import de.paluno.game.screens.PlayScreen;
import de.paluno.game.screens.WinningPlayer;

public class SEPGame extends Game {

	private AssetManager assetManager;

	public SEPGame() {
		assetManager = new AssetManager();
        assetManager.setLoader(AnimationData.class, new AnimationData.Loader(new InternalFileHandleResolver()));
	}

	@Override
	public void create() {
		setNextScreen(new PlayScreen(this));
	}

    @Override
    public void render() {
        super.render();
    }

    public void setGameOver(WinningPlayer winningPlayer) {
	    setNextScreen(new GameOverScreen(this, winningPlayer));
    }

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public void setNextScreen(Screen screen) {
		if (screen instanceof Loadable) {
			((Loadable) screen).load(assetManager);
			assetManager.finishLoading();
		}

		setScreen(screen);
	}
}
