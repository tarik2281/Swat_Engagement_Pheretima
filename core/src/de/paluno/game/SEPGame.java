package de.paluno.game;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import de.paluno.game.interfaces.UserName;
import de.paluno.game.screens.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class SEPGame extends Game {

    private AssetManager assetManager;
    private Music menuMusic;
    private Sound clickSound;

    private EventManager.Listener listener = (type, data) -> {
        switch (type) {
            case LeaveMatch:
                setModiScreen();
                break;
            case GameOver:
                setGameOver((String) data);
                break;
            case ClickSound:
            	clickSound.play();
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
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                handleUncaughtException(thread, ex);
            }
        });

    	EventManager.getInstance().addListener(listener, EventManager.Type.GameOver, EventManager.Type.ClickSound, EventManager.Type.LeaveMatch);
    	
        FileHandle configFileHandle = Gdx.files.local("config.xml");
        if (!configFileHandle.exists()) {
            try {
                Gdx.files.internal("config.xml").copyTo(configFileHandle);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        Config.loadConfig(configFileHandle);

        FileHandle licenseFileHandle = Gdx.files.local("LICENSE");
        if (!licenseFileHandle.exists()) {
            try {
                Gdx.files.internal("LICENSE").copyTo(licenseFileHandle);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

//        if (Config.fullscreen)
//            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("MenuScreen_ThemeSong.mp3"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("TickSound.mp3"));
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

    public void setPlayScreen(int mapNumber, int numWorms, Array<UserName> names) {
        setNextScreen(new PlayScreen(this, mapNumber, numWorms, names));
    }

    public void setLocalScreen() {
        setNextScreen(new LocalScreen(this));
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

    public void setGameOver(String name) {
        setNextScreen(new GameOverScreen(this, name));
    }


    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void setNextScreen(Screen screen) {
        assetManager.clear();
        
//        if(screen instanceof PlayScreen)
//        	menuMusic.stop();
//        else if (!(screen instanceof GameOverScreen))
//        	menuMusic.play();

        if (screen instanceof Loadable) {
            ((Loadable) screen).load(assetManager);
            assetManager.finishLoading();
        }

        setScreen(screen);
    }

    private void handleUncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy__HH_mm_ss");
        String fileName = "Crash_" + formatter.format(time) + ".txt";

        File logsDir = Gdx.files.local("logs").file();
        logsDir.mkdir();

        if (logsDir.exists()) {
            PrintStream stream = null;

            File file = new File(logsDir, fileName);
            if (!file.exists()) {
                try {
                    stream = new PrintStream(new FileOutputStream(file));

                    ex.printStackTrace(stream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (stream != null)
                        stream.close();
                }
            }
        }

        System.exit(1);
    }
}
