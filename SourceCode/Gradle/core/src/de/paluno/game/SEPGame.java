package de.paluno.game;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import de.paluno.game.screens.*;

import java.util.ArrayList;
import java.util.List;

public class SEPGame extends Game {

    private AssetManager assetManager;

    public SEPGame() {
        assetManager = new AssetManager();
        assetManager.setLoader(AnimationData.class, new AnimationData.Loader(new InternalFileHandleResolver()));
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }

    @Override
    public void create() {
        setStartScreen();
    }

    @Override
    public void render() {
        super.render();
    }

    public void setStartScreen() {
        setNextScreen(new ModiScreen(this));
    }

    public void setTeamMenuScreen(int mapNumber, int numWorms,int playerNumber) {
        setNextScreen(new TeamMenuScreen(this, mapNumber, numWorms, playerNumber));
    }

    public void setMenuScreen(int playerNumber){
        setNextScreen(new MenuScreen(this,playerNumber));
    }

    public void setPlayScreen(int mapNumber, int numWorms,int playerNumber, int modi, List<String> names) {
        setNextScreen(new PlayScreen(this, mapNumber, numWorms, playerNumber, modi, names));
    }

    public void setPlayerMenuScreen( int mapNumber, int numWorms, int playerNumber ){
        setNextScreen(new PlayerMenuScreen(this, mapNumber,numWorms, playerNumber));
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
