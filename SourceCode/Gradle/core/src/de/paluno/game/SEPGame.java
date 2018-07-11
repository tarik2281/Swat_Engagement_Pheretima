package de.paluno.game;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.sun.deploy.jcp.controller.Network;
import de.paluno.game.screens.*;

public class SEPGame extends Game {

    private AssetManager assetManager;

    private EventManager.Listener listener = (type, data) -> {
        switch (type) {
            case GameOver:
                setGameOver((WinningPlayer) data);
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

        setStartScreen();
    }

    @Override
    public void render() {
        EventManager.getInstance().processEvents();

        super.render();
    }

    public void setStartScreen() {
        setNextScreen(new ModiScreen(this));
    }


    public void setLoginScreen(NetworkClient client){
        setNextScreen(new LoginScreen(this, client));
    }



    public void setPlayScreen(int mapNumber, int numWorms) {
        setNextScreen(new PlayScreen(this, mapNumber, numWorms));
    }



    public void setLobbyScreen(NetworkClient client){
        setNextScreen(new LobbyScreen(this, client));
    }

    public void setPlayerLobbyScreen(NetworkClient client, int lobbyId){
        setNextScreen(new PlayerLobbyScreen(this, client, lobbyId));
    }

    public void setModiScreen(){
        setNextScreen(new ModiScreen(this));
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
