package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.paluno.game.*;
import de.paluno.game.gameobjects.GameWorld;
import de.paluno.game.interfaces.*;

public class PlayScreen extends ScreenAdapter implements Loadable {

    private SEPGame game;
    private SpriteBatch spriteBatch;

	//private World replayWorld;
	private boolean disposeReplayAfterUpdate;
    //private World.SnapshotData worldSnapshot;
    private WorldHandler worldHandler;
    private UserWorldController worldController;

    private WinningPlayer winningPlayer = WinningPlayer.NONE;

    private int mapNumber;
    private int numWorms;
    private PlayUILayer uiLayer;
    private WeaponUI weaponUI;

    private Sound mapSound;
    
    private GameSetupData gameSetupData;
    private GameSetupRequest gameSetupRequest;
    private NetworkClient client;
    private ChatWindow chatWindow;

    public PlayScreen(SEPGame game, int mapNumber, int numWorms) {
        this.game = game;

        this.mapNumber = mapNumber;
        this.numWorms = numWorms;

        spriteBatch = new SpriteBatch();
    }

    public PlayScreen(SEPGame game, int mapNumber, int numWorms, NetworkClient client, GameSetupRequest request) {
        this(game, mapNumber, numWorms);

        this.client = client;

        this.gameSetupRequest = request;
    }

    public PlayScreen(SEPGame game, NetworkClient client, GameSetupData data) {
        this(game, data.mapNumber, 1);

        this.client = client;

        this.gameSetupData = data;
    }

    @Override
    public void show() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        uiLayer = new PlayUILayer(screenWidth, screenHeight);

        if (gameSetupRequest != null) {
            worldHandler = new NetworkWorldHandler(this, client, gameSetupRequest, mapNumber, numWorms);
            chatWindow = new ChatWindow(client);
            chatWindow.initialize();
        }
        else if (gameSetupData != null) {
            worldHandler = new NetworkWorldHandler(this, client, gameSetupData);
            chatWindow = new ChatWindow(client);
            chatWindow.initialize();
        }
        else {
            worldHandler = new LocalWorldHandler(this, mapNumber, numWorms);
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

        worldHandler.initialize();

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
            world.initializeNew(mapNumber, numWorms);
        }
*/
        weaponUI = new WeaponUI(this);
        //weaponUI.setPlayer(world.getCurrentPlayer());

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        if (chatWindow != null)
            inputMultiplexer.addProcessor(chatWindow.getInputProcessor());
        inputMultiplexer.addProcessor(weaponUI.getInputProcessor());
        inputMultiplexer.addProcessor(worldController.getInputProcessor());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // game loop
        Gdx.graphics.setTitle("SEPGame FPS: " + Gdx.graphics.getFramesPerSecond());

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

        if (winningPlayer != WinningPlayer.NONE && replayWorld == null) {
            game.setGameOver(winningPlayer);
        }*/
    }

    public void renderPhase(float delta) {
        uiLayer.render(spriteBatch, delta);
    }

    @Override
    public void hide() {
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

    public void setGameOver(WinningPlayer winningPlayer) {
        this.winningPlayer = winningPlayer;
    }
}
