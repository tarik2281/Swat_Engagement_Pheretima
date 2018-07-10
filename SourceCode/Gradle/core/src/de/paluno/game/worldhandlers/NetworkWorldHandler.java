package de.paluno.game.worldhandlers;

import de.paluno.game.Constants;
import de.paluno.game.DataHandler;
import de.paluno.game.EventManager;
import de.paluno.game.GameState;
import de.paluno.game.NetworkClient;
import de.paluno.game.gameobjects.*;
import de.paluno.game.interfaces.*;
import de.paluno.game.screens.PlayScreen;
import de.paluno.game.screens.WinningPlayer;

import java.util.ArrayList;

public class NetworkWorldHandler extends InterpolationWorldHandler {

    public static final float UPDATE_FREQUENCY = 1.0f / 30.0f; // 30Hz
    public static final float TIME_SHIFT = 0.3f; // 300 ms delay

    private NetworkClient client;

    private ArrayList<GameEvent> receivedGameEvents = new ArrayList<>();
    private ArrayList<WorldData> receivedGameData = new ArrayList<>();

    private int numWorms;
    private GameSetupRequest gameSetupRequest;
    private GameSetupData gameSetupData;

    private boolean wormDied;

    private DataHandler dataHandler = new DataHandler() {
        @Override
        public void handleData(NetworkClient client, Object data) {
            if (data instanceof StartTurnEvent) {
                StartTurnEvent event = (StartTurnEvent)data;
                setCurrentPlayerTurn(event.playerNumber, event.wormNumber);
                getWindHandler().setWind(event.wind);
                getReplay().setWind(event.wind);
                setCurrentTime(getCurrentGameTick() * de.paluno.game.Constants.UPDATE_FREQUENCY);
            }
            else if (data instanceof GameOverEvent) {
                GameOverEvent event = (GameOverEvent)data;
                WinningPlayer winningPlayer = WinningPlayer.NONE;
                switch (event.winningPlayer) {
                    case 0:
                        winningPlayer = WinningPlayer.PLAYERONE;
                        break;
                    case 1:
                        winningPlayer = WinningPlayer.PLAYERTWO;
                        break;
                }
                EventManager.getInstance().queueEvent(EventManager.Type.GameOver, winningPlayer);
            }
            else if (data instanceof WorldData) {
                WorldData worldData = (WorldData)data;
                worldData.setReceivingTimeStamp(worldData.getTick() * UPDATE_FREQUENCY);// + getIdleTime());
                receivedGameData.add(worldData);
            }
            else if (data instanceof GameEvent) {
                GameEvent event = (GameEvent)data;
                if (event.getType() == GameEvent.Type.END_TURN)
                    System.out.println("End turn received");
                event.setReceivingTimeStamp(event.getTick() * UPDATE_FREQUENCY);// + getIdleTime());
                receivedGameEvents.add(event);
            }
            else if (data instanceof UserMessage) {
                UserMessage message = (UserMessage)data;
                if (message.getType() == Message.Type.UserLeft) {
                    for (Player player : getPlayers()) {
                        if (player.getClientId() == message.getUserId()) {
                            for (Worm worm : player.getWorms())
                                worm.die(de.paluno.game.Constants.DEATH_TYPE_DISCONNECTED);
                            break;
                        }
                    }
                }
            }
        }
    };

    private EventManager.Listener eventListener = new EventManager.Listener() {
        @Override
        public void handleEvent(EventManager.Type eventType, Object data) {
            switch (eventType) {
                case ReplayEnded:
                    System.out.println("Sending ready message");
                    client.send(Message.clientReady());
                    break;
            }
        }
    };

    @Override
    protected boolean shouldInterpolate() {
        return !isControllingCurrentPlayer();
    }

    public NetworkWorldHandler(PlayScreen screen, NetworkClient client, GameSetupData data) {
        super(screen, data.mapNumber);

        this.client = client;

        this.gameSetupData = data;

        wormDied = false;
    }

    public NetworkWorldHandler(PlayScreen screen, NetworkClient client, GameSetupRequest request) {
        super(screen, request.getMapNumber());

        this.client = client;

        this.gameSetupRequest = request;
        this.numWorms = request.getNumWorms();

        wormDied = false;
    }

    @Override
    public boolean shouldAcceptInput() {
        return isControllingCurrentPlayer();
    }

    @Override
    protected void requestNextTurn() {
        if (isControllingCurrentPlayer()) {
            GameEvent event = new GameEvent(getCurrentGameTick(), GameEvent.Type.END_TURN);
            getReplay().addGameData(event);
            client.send(event);
        }

        if (wormDied && getReplay() != null) {
            EventManager.getInstance().queueEvent(EventManager.Type.Replay, getReplay());
        }
        else
            client.send(Message.clientReady());

        clearSnapshots();

        wormDied = false;
        //if (isControllingCurrentPlayer())
            //sendWorldSnapshot(true);

        receivedGameData.clear();

        //client.send(Message.clientReady());
    }

    @Override
    public void onInitializePlayers() {
        EventManager.getInstance().addListener(eventListener, EventManager.Type.ReplayEnded);
        setTimeShift(-TIME_SHIFT);
        setEventList(receivedGameEvents);
        setSnapshotList(receivedGameData);
        client.registerDataHandler(dataHandler);

        if (gameSetupRequest != null) {
            PlayerData[] playerData = new PlayerData[gameSetupRequest.getPlayers().length];

            int playerIndex = 0;
            for (GameSetupRequest.Player setupPlayer : gameSetupRequest.getPlayers()) {
                Player player = addPlayer(playerIndex);
                player.setClientId(setupPlayer.getClientId());

                WormData[] wormData = new WormData[numWorms];
                for (int j = 0; j < numWorms; j++) {
                    Worm worm = addWorm(player, j);
                    worm.setPosition(getRandomSpawnPosition());

                    wormData[j] = new WormData()
                            .setPlayerNumber(player.getPlayerNumber())
                            .setWormNumber(j)
                            .setPhysicsData(new PhysicsData()
                                    .setPositionX(worm.getPosition().x)
                                    .setPositionY(worm.getPosition().y));
                }

                playerData[playerIndex++] = new PlayerData(player.getClientId(), player.getPlayerNumber(), wormData);
            }

            GameSetupData data = new GameSetupData(playerData);
            data.mapNumber = getMapNumber();
            client.send(data);
        }
        else if (gameSetupData != null) {
            for (int i = 0; i < gameSetupData.getPlayerData().length; i++) {
                PlayerData playerData = gameSetupData.getPlayerData()[i];

                Player player = addPlayer(playerData.getPlayerNumber());
                player.setClientId(playerData.getClientId());

                for (WormData wormData : playerData.getWorms()) {
                    Worm worm = addWorm(player, wormData.getWormNumber());
                    worm.setPosition(wormData.getPhysicsData().getPositionX(),
                            wormData.getPhysicsData().getPositionY());
                    // TODO: setup worms

                }
            }
        }
        else
            throw new IllegalStateException("Either gameSetupData or gameSetupRequest must be set");
    }

    public boolean isControllingCurrentPlayer() {
        Player currentPlayer = getCurrentPlayer();
        return currentPlayer != null && currentPlayer.getClientId() == getClientId();
    }

    @Override
    protected void onEmitGameData(GameData gameData) {
        if (gameData instanceof WorldData)
            client.sendUDP(gameData);
        else
            client.send(gameData);
    }

    @Override
    protected void onGameDataProcessed(GameData gameData) {
        if (gameData instanceof GameEvent) {
            if (((GameEvent) gameData).getType() == GameEvent.Type.END_TURN)
                System.out.println("Adding end turn to replay");
        }
        getReplay().addGameData(gameData);
        if (getCurrentGameTick() < gameData.getTick())
            setCurrentGameTick(gameData.getTick());
    }

    @Override
    public boolean shouldWorldStep() {
        return getCurrentGameState() == GameState.NONE || isControllingCurrentPlayer();
    }

    public int getClientId() {
        return client.getClientId();
    }

    @Override
    protected void onWormDied(Worm.DeathEvent event) {
        if (event.getDeathType() != de.paluno.game.Constants.DEATH_TYPE_DISCONNECTED)
            wormDied = true;

        if (event.getDeathType() == Constants.DEATH_TYPE_FALL_DOWN && getReplay() != null) {
            getReplay().setStartingTick(getCurrentGameTick(), 5.0f);
        }
    }

    @Override
    protected boolean shouldCreateReplay() {
        return true;
    }
}
