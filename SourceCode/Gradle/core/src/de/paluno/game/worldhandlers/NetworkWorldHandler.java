package de.paluno.game.worldhandlers;

import de.paluno.game.Constants;
import de.paluno.game.DataHandler;
import de.paluno.game.EventManager;
import de.paluno.game.GameState;
import de.paluno.game.NetworkClient;
import de.paluno.game.gameobjects.*;
import de.paluno.game.interfaces.*;
import de.paluno.game.screens.PlayScreen;

import java.util.ArrayList;

public class NetworkWorldHandler extends InterpolationWorldHandler {

    public static final float TIME_SHIFT = 0.3f; // 300 ms delay

    private NetworkClient client;

    private ArrayList<GameEvent> receivedGameEvents = new ArrayList<>();
    private ArrayList<WorldData> receivedGameData = new ArrayList<>();

    private int numWorms;
    private GameSetupRequest gameSetupRequest;
    private GameSetupData gameSetupData;

    private boolean wormDied;
    private boolean simulatingAirdrop = false;
    private boolean simulatingTurrets = false;
    private boolean raisingLimit = false;
    private boolean simulatingPlayer = false;

    private DataHandler dataHandler = new DataHandler() {
        @Override
        public void handleData(NetworkClient client, Object data) {
            if (data instanceof StartTurnEvent) {
                StartTurnEvent event = (StartTurnEvent)data;
                setCurrentPlayerTurn(event.playerNumber, event.wormNumber);
                getWindHandler().setWind(event.wind);
                getReplay().setWind(event.wind);
                setCurrentTime(getCurrentGameTick() * Constants.UPDATE_FREQUENCY);
            }
            else if (data instanceof TurretsShootRequest) {
                TurretsShootRequest request = (TurretsShootRequest)data;
                simulatingPlayer = request.getSimulatingUserId() == client.getClientId();
                simulatingTurrets = true;
                if (!shootTurrets())
                    client.send(Message.clientReady());
                setCurrentTime(getCurrentGameTick() * Constants.UPDATE_FREQUENCY);
            }
            else if (data instanceof SpawnAirdropRequest) {
                SpawnAirdropRequest request = (SpawnAirdropRequest)data;
                simulatingPlayer = request.getSimulatingUserId() == client.getClientId();
                simulatingAirdrop = true;
                if (simulatingPlayer)
                    createAirdrop(getRandomAirdropPosition(), WeaponType.getRandomDrop());
                else
                    beginAirdrop();
                setCurrentTime(getCurrentGameTick() * Constants.UPDATE_FREQUENCY);
            }
            else if (data instanceof RaiseWaterEvent) {
                raisingLimit = true;
                raiseWaterLevel();
            }
            else if (data instanceof GameOverEvent) {
                GameOverEvent event = (GameOverEvent)data;
                String winningPlayer = null;
                Player player = getPlayers().get(event.winningPlayer);
                if (player != null)
                    winningPlayer = player.getName();
                EventManager.getInstance().queueEvent(EventManager.Type.GameOver, winningPlayer);
            }
            else if (data instanceof WorldData) {
                WorldData worldData = (WorldData)data;
                worldData.setReceivingTimeStamp(worldData.getTick() * Constants.UPDATE_FREQUENCY);// + getIdleTime());
                receivedGameData.add(worldData);
            }
            else if (data instanceof GameEvent) {
                GameEvent event = (GameEvent)data;
                if (event.getType() == GameEvent.Type.END_TURN)
                    System.out.println("End turn received");
                event.setReceivingTimeStamp(event.getTick() * Constants.UPDATE_FREQUENCY);// + getIdleTime());
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
        System.out.println("Requested next turn wormDied: " + wormDied);

        if (!raisingLimit && isControllingCurrentPlayer()) {
            GameEvent event = new GameEvent(getCurrentGameTick(), GameEvent.Type.END_TURN);
            if (getReplay() != null)
                getReplay().addGameData(event);
            client.send(event);
        }

        raisingLimit = false;
        simulatingTurrets = false;
        simulatingAirdrop = false;

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
                player.setName(setupPlayer.getUserName().getUserName());
                player.setClientId(setupPlayer.getClientId());

                WormData[] wormData = new WormData[numWorms];
                for (int j = 0; j < numWorms; j++) {
                    Worm worm = addWorm(player, j, setupPlayer.getUserName().getWormNames()[j]);
                    worm.setPosition(getRandomSpawnPosition());

                    wormData[j] = new WormData()
                            .setPlayerNumber(player.getPlayerNumber())
                            .setWormNumber(j)
                            .setPhysicsData(new PhysicsData()
                                    .setPositionX(worm.getPosition().x)
                                    .setPositionY(worm.getPosition().y));
                }

                playerData[playerIndex++] = new PlayerData(player.getClientId(), player.getPlayerNumber(), wormData, setupPlayer.getUserName());
            }

            GameSetupData data = new GameSetupData(playerData);
            data.mapNumber = getMapNumber();
            client.send(data);
        }
        else if (gameSetupData != null) {
            for (int i = 0; i < gameSetupData.getPlayerData().length; i++) {
                PlayerData playerData = gameSetupData.getPlayerData()[i];

                Player player = addPlayer(playerData.getPlayerNumber());
                player.setName(playerData.getUserName().getUserName());
                player.setClientId(playerData.getClientId());

                for (WormData wormData : playerData.getWorms()) {
                    Worm worm = addWorm(player, wormData.getWormNumber(), playerData.getUserName().getWormNames()[wormData.getWormNumber()]);
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
        if (getCurrentGameState() == GameState.RAISE_WATER_LEVEL)
            return true;

        if (simulatingTurrets || simulatingAirdrop)
            return simulatingPlayer;

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
        if (getReplay() != null)
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
        if (event.getDeathType() != de.paluno.game.Constants.DEATH_TYPE_DISCONNECTED) {
            System.out.println("Registering worm died in NetworkWorldHandler");
            wormDied = true;
        }
    }

    @Override
    protected boolean shouldCreateReplay() {
        return true;
    }
}
