package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import de.paluno.game.*;
import de.paluno.game.gameobjects.GameWorld;
import de.paluno.game.interfaces.*;
import de.paluno.game.worldhandlers.*;

import java.util.List;

public class PlayScreen extends ScreenAdapter implements Loadable {

    private SEPGame game;
    private SpriteBatch spriteBatch;

	//private World replayWorld;
	private boolean disposeReplayAfterUpdate;
    //private World.SnapshotData worldSnapshot;
    private WorldHandler worldHandler;
    private ReplayWorldHandler replayWorldHandler;
    private UserWorldController worldController;

    private int winningPlayer = -2;

    private int mapNumber;
    private int numWorms;
    private PlayUILayer uiLayer;
    private WeaponUI weaponUI;
    private Array<UserName> names;

    private Sound mapSound;

    private GameSetupData gameSetupData;
    private GameSetupRequest gameSetupRequest;
    private NetworkClient client;
    private ChatWindow chatWindow;

    private PlayScreen(SEPGame game, int mapNumber) {
        this.game = game;
        this.mapNumber = mapNumber;
        spriteBatch = new SpriteBatch();
    }

    public PlayScreen(SEPGame game, int mapNumber, int numWorms, Array<UserName> userNames) {
//    public PlayScreen(SEPGame game, int mapNumber, int numWorms,int playerNumber, int modi, List<String> names) {
        this(game, mapNumber);

        this.numWorms = numWorms;
        this.names = userNames;
//        this.modi = modi;
//        this.names = names;
//        this.playerNumber = playerNumber;


    }

    public PlayScreen(SEPGame game, NetworkClient client, GameSetupRequest request) {
        this(game, request.getMapNumber());

        this.client = client;

        this.gameSetupRequest = request;
    }

    public PlayScreen(SEPGame game, NetworkClient client, GameSetupData data) {
        this(game, data.mapNumber);

        this.client = client;

        this.gameSetupData = data;
    }

    private EventManager.Listener eventListener = new EventManager.Listener() {
        @Override
        public void handleEvent(EventManager.Type eventType, Object data) {
            switch (eventType) {
                case Replay:
                    replayWorldHandler = new ReplayWorldHandler(PlayScreen.this, (Replay)data);
                    replayWorldHandler.initialize();

                    worldHandler.hide();
                    replayWorldHandler.show();
                    break;
                case ReplayEnded:
                    replayWorldHandler.dispose();
                    replayWorldHandler = null;
                    worldHandler.show();
                    break;
            }
        }
    };

    @Override
    public void resize(int width, int height) {
        if (worldHandler != null)
            worldHandler.updateViewport(width, height);
        if (replayWorldHandler != null)
            replayWorldHandler.updateViewport(width, height);
    }

    @Override
    public void show() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        uiLayer = new PlayUILayer(screenWidth, screenHeight);

        if (gameSetupRequest != null) {
            worldHandler = new NetworkWorldHandler(this, client, gameSetupRequest);
            chatWindow = new ChatWindow(client);
            chatWindow.initialize();
        }
        else if (gameSetupData != null) {
            worldHandler = new NetworkWorldHandler(this, client, gameSetupData);
            chatWindow = new ChatWindow(client);
            chatWindow.initialize();
        }
        else {
            worldHandler = new LocalWorldHandler(this, mapNumber, numWorms, names);
        }

		switch (mapNumber) {
		case 0:
			mapSound = getAssetManager().get(Assets.map1Sound);
			break;
		case 1:
			mapSound = getAssetManager().get(Assets.map2Sound);
			break;
		case 2:
			mapSound = getAssetManager().get(Assets.map3Sound);
			break;
		case 3:
			mapSound = getAssetManager().get(Assets.map4Sound);
			break;
		}
		mapSound.loop(0.2f);

        worldHandler.initialize();
        worldHandler.show();

        worldController = new UserWorldController();
        worldController.initialize(worldHandler);

        //chatWindow = new ChatWindow(client);
        //chatWindow.initialize();


        /*if (gameSetupRequest != null) {
            world.initializeRequest(mapNumber, numWorms, gameSetupRequest);

            int[] clientIds = new int[world.getPlayers().length];
            PlayerData[] players = new PlayerData[world.getPlayers().length];
            for (int i = 0; i < world.getPlayers().length; i++) {
                Player player = world.getPlayers()[i];
                WormData[] worms = new WormData[player.getCharacters().length];

                for (int j = 0; j < player.getCharacters().length; j++) {
                    Worm worm = player.getCharacters()[j];
                    WormData wormData = new WormData();
                    wormData.playerNumber = player.getPlayerNumber();
                    wormData.wormNumber = worm.getCharacterNumber();
                    wormData
                            .setOrientation(worm.getOrientation())
                            .setMovement(0)
                            .setPhysicsData(new PhysicsData().setPositionX(worm.spawnPosition.x).setPositionY(worm.spawnPosition.y));
                    worms[j] = wormData;
                }

                clientIds[i] = player.getClientId();
                players[i] = new PlayerData(player.getPlayerNumber(), worms);
            }

            GameSetupData data = new GameSetupData(clientIds, players);

            client.sendObject(data);
        }
        else if (gameSetupData != null) {
            world.initializeData(mapNumber, gameSetupData);
        }
        else {
            world.initializeNew(mapNumber, numWorms, names);
        }
*/
        weaponUI = new WeaponUI(this);
        weaponUI.setWorldHandler(worldHandler);
        //weaponUI.setPlayer(world.getCurrentPlayer());

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        if (chatWindow != null)
            inputMultiplexer.addProcessor(chatWindow.getInputProcessor());
        inputMultiplexer.addProcessor(weaponUI.getInputProcessor());
        inputMultiplexer.addProcessor(worldController.getInputProcessor());
        Gdx.input.setInputProcessor(inputMultiplexer);

        EventManager.getInstance().addListener(eventListener, EventManager.Type.Replay, EventManager.Type.ReplayEnded);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // game loop
        Gdx.graphics.setTitle("SEPGame FPS: " + Gdx.graphics.getFramesPerSecond());

        if (replayWorldHandler != null)
            replayWorldHandler.updateAndRender(spriteBatch, delta);
        else
            worldHandler.updateAndRender(spriteBatch, delta);

        /*if (replayWorld != null)
        	replayWorld.doGameLoop(spriteBatch, delta);
        else
        	world.doGameLoop(spriteBatch, delta);*/

        renderPhase(delta);

        weaponUI.render(spriteBatch, delta);

        if (chatWindow != null)
        chatWindow.render(delta);

        /*if (disposeReplayAfterUpdate) {
            replayWorld.dispose();
            replayWorld = null;
            disposeReplayAfterUpdate = false;
        }

        if (winningPlayer != -2 && replayWorld == null) {
            game.setGameOver(winningPlayer);
        }*/
    }

    public void renderPhase(float delta) {
        uiLayer.render(spriteBatch, delta);
    }

    @Override
    public void hide() {
        EventManager.getInstance().removeListener(eventListener, EventManager.Type.Replay, EventManager.Type.ReplayEnded);

        worldHandler.dispose();

        if (chatWindow != null)
            chatWindow.dispose();

        if (client != null)
            client.disconnect();

        //world.dispose();
        //world = null;

        Gdx.input.setInputProcessor(null);
    }

    public AssetManager getAssetManager() {
        return game.getAssetManager();
    }

    @Override
    public boolean load(AssetManager manager) {
        Assets.loadAssets(manager, Assets.PlayScreenAssets);
        Assets.loadAssets(manager, Assets.Music);

        manager.load(Assets.getMapByIndex(mapNumber));

        return false;
    }

    public void setGameState(GameWorld world, GameState gameState, int currentPlayer) {
        uiLayer.setGameState(gameState, currentPlayer);

        //if (this.world == world && gameState == GameState.SHOOTING)
        //	worldSnapshot = world.makeSnapshot();

        //if (this.replayWorld == world && gameState == GameState.REPLAY_ENDED) {
            disposeReplayAfterUpdate = true;
        //}

        /*if (this.world == world && (gameState == GameState.PLAYERTURN || gameState == GameState.GAMEOVERPLAYERONEWON || gameState == GameState.GAMEOVERPLAYERTWOWON)) {
            if (world.isWormDied() && worldSnapshot != null) {
                //replayWorld = new World(this);
                //replayWorld.initializeFromSnapshot(worldSnapshot);
            }

            weaponUI.setPlayer(this.world.getCurrentPlayer());
            worldSnapshot = null;
        }*/
    }

    public void setGameOver(int winningPlayer) {
        this.winningPlayer = winningPlayer;
    }
}
