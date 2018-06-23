package de.paluno.game;

import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.Worm;
import de.paluno.game.interfaces.*;
import de.paluno.game.screens.PlayScreen;

import java.util.ArrayList;
import java.util.Iterator;

public class NetworkWorldHandler extends WorldHandler {

    private static final float UPDATE_FREQUENCY = 1.0f / 30.0f; // 30Hz
    private static final float TIME_SHIFT = 0.1f; // 100 ms delay

    private NetworkClient client;

    private ArrayList<GameData> receivedGameData;

    private float currentTime;
    private int currentTick;
    private float updateTimer;
    private boolean sendGameData;

    private GameData currentSnapshot;
    private GameData nextSnapshot;

    private int numWorms;
    private GameSetupRequest gameSetupRequest;
    private GameSetupData gameSetupData;

    public NetworkWorldHandler(PlayScreen screen, NetworkClient client, GameSetupData data) {
        super(screen, data.mapNumber);

        this.client = client;

        this.gameSetupData = data;
    }

    public NetworkWorldHandler(PlayScreen screen, NetworkClient client, GameSetupRequest request, int mapNumber, int numWorms) {
        super(screen, mapNumber);

        this.client = client;

        this.gameSetupRequest = request;
        this.numWorms = numWorms;
    }

    @Override
    public void onInitializePlayers() {
        if (gameSetupRequest != null) {
            PlayerData[] playerData = new PlayerData[gameSetupRequest.getPlayerNumbers().length];

            for (int i = 0; i < gameSetupRequest.getPlayerNumbers().length; i++) {
                Player player = addPlayer(gameSetupRequest.getPlayerNumbers()[i]);
                player.setClientId(gameSetupRequest.getClientIds()[i]);

                WormData[] wormData = new WormData[numWorms];
                for (int j = 0; j < numWorms; j++) {
                    Worm worm = addWorm(player);
                    worm.spawnPosition = getRandomSpawnPosition();

                    WormData wormD = new WormData();
                    wormD.playerNumber = player.getPlayerNumber();
                    wormD.wormNumber = j;
                    wormD.setPhysicsData(new PhysicsData().setPositionX(worm.spawnPosition.x)
                    .setPositionY(worm.spawnPosition.y));

                    wormData[j] = wormD;
                }

                playerData[i] = new PlayerData(player.getPlayerNumber(), wormData);
            }

            GameSetupData data = new GameSetupData(gameSetupRequest.getClientIds(), playerData);
            client.sendObject(data);
        }
        else if (gameSetupData != null) {

        }
        else
            throw new IllegalStateException("Either gameSetupData or gameSetupRequest must be set");
    }

    private float getSnapshotsRatio(float shiftedTime) {
        float total = nextSnapshot.getReceivingTimeStamp() - currentSnapshot.getReceivingTimeStamp();

        return Math.min(1.0f, (shiftedTime - currentSnapshot.getReceivingTimeStamp()) / total);
    }

    private float updateCurrentSnapshots() {
        float shiftedTime = currentTime - TIME_SHIFT;

        if (nextSnapshot != null) {
            if (shiftedTime <= nextSnapshot.getReceivingTimeStamp())
                return shiftedTime;

            currentSnapshot = nextSnapshot;
            nextSnapshot = null;
        }

        for (Iterator<GameData> it = receivedGameData.iterator(); it.hasNext(); ) {
            GameData gameData = it.next();
            it.remove();

            if (gameData == null)
                continue;

            if (shiftedTime <= gameData.getReceivingTimeStamp()) {
                nextSnapshot = gameData;
                break;
            }
            else {
                currentSnapshot = gameData;
            }
        }

        return shiftedTime;
    }

    public int getClientId() {
        return client.getClientId();
    }

    @Override
    public void update(float delta) {
        currentTime += delta;

        if (sendGameData) {
            updateTimer += delta;

            if (updateTimer >= UPDATE_FREQUENCY) {
                updateTimer -= UPDATE_FREQUENCY;

                WorldData worldData = new WorldData();
                worldData.gameState = getWorld().getGameState().ordinal();
                //worldData.worms = new WormData[getWorld().getWormsCount()];
                //worldData.shootingAngle = getWorld().getShotDirectionIndicator().getAngle();
                // TODO: send data
            }
        }
        else {
            // update from received data

            float shiftedTime = updateCurrentSnapshots();
            if (currentSnapshot != null) {
                if (currentSnapshot instanceof PlayerTurnData) {
                    PlayerTurnData playerTurnData = (PlayerTurnData)currentSnapshot;

                }
            }
        }

        super.update(delta);
    }

    @Override
    protected void onEmitWorldData(WorldData data) {
        //client.sendTCP(data);
    }
}
