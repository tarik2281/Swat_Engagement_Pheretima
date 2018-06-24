package de.paluno.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.WeaponType;
import de.paluno.game.gameobjects.Worm;
import de.paluno.game.interfaces.*;
import de.paluno.game.screens.PlayScreen;

import java.util.ArrayList;
import java.util.Iterator;

public class NetworkWorldHandler extends WorldHandler {

    private static final float UPDATE_FREQUENCY = 1.0f / 30.0f; // 30Hz
    private static final float TIME_SHIFT = 0.1f; // 100 ms delay

    private NetworkClient client;

    private ArrayList<WorldData> receivedGameData = new ArrayList<>();

    private float currentTime;
    private int currentTick;
    private float updateTimer;
    private boolean sendGameData;

    private WorldData currentSnapshot;
    private WorldData nextSnapshot;

    private int numWorms;
    private GameSetupRequest gameSetupRequest;
    private GameSetupData gameSetupData;

    private DataHandler<StartTurnEvent> startTurnHandler = new DataHandler<StartTurnEvent>() {
        @Override
        public void handleData(NetworkClient client, StartTurnEvent data) {
            Gdx.app.postRunnable(() -> {
                setCurrentPlayerTurn(data.playerNumber, data.wormNumber);
            });
        }
    };

    private DataHandler<WorldData> worldDataHandler = new DataHandler<WorldData>() {
        @Override
        public void handleData(NetworkClient client, WorldData data) {
            data.setReceivingTimeStamp(currentTime);

            Gdx.app.postRunnable(() -> {
                receivedGameData.add(data);
            });
        }
    };

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
    public boolean requestNextTurn() {
        MessageData messageData = new MessageData(MessageData.Type.ClientReady);
        client.sendObject(messageData);

        return false;
    }

    @Override
    public void onInitializePlayers() {
        client.registerDataHandler(StartTurnEvent.class, startTurnHandler);
        client.registerDataHandler(WorldData.class, worldDataHandler);

        if (gameSetupRequest != null) {
            PlayerData[] playerData = new PlayerData[gameSetupRequest.getPlayerNumbers().length];

            for (int i = 0; i < gameSetupRequest.getPlayerNumbers().length; i++) {
                Player player = addPlayer(gameSetupRequest.getPlayerNumbers()[i]);
                player.setClientId(gameSetupRequest.getClientIds()[i]);
                addWeapon(player, WeaponType.WEAPON_BAZOOKA);
                addWeapon(player, WeaponType.WEAPON_GRENADE);
                addWeapon(player, WeaponType.WEAPON_GUN);
                addWeapon(player, WeaponType.WEAPON_SPECIAL);

                WormData[] wormData = new WormData[numWorms];
                for (int j = 0; j < numWorms; j++) {
                    Worm worm = addWorm(player, j);
                    worm.spawnPosition = getRandomSpawnPosition();

                    WormData wormD = new WormData();
                    wormD.playerNumber = player.getPlayerNumber();
                    wormD.wormNumber = j;
                    wormD.setPhysicsData(new PhysicsData()
                            .setPositionX(worm.spawnPosition.x)
                            .setPositionY(worm.spawnPosition.y));

                    wormData[j] = wormD;
                }

                playerData[i] = new PlayerData(player.getPlayerNumber(), wormData);
            }

            GameSetupData data = new GameSetupData(gameSetupRequest.getClientIds(), playerData);
            client.sendObject(data);
        }
        else if (gameSetupData != null) {
            for (int i = 0; i < gameSetupData.getPlayerData().length; i++) {
                PlayerData playerData = gameSetupData.getPlayerData()[i];
                int clientId = gameSetupData.getClientIds()[i];

                Player player = addPlayer(playerData.getPlayerNumber());
                player.setClientId(clientId);
                addWeapon(player, WeaponType.WEAPON_BAZOOKA);
                addWeapon(player, WeaponType.WEAPON_GRENADE);
                addWeapon(player, WeaponType.WEAPON_GUN);
                addWeapon(player, WeaponType.WEAPON_SPECIAL);

                for (WormData wormData : playerData.getWorms()) {
                    Worm worm = addWorm(player, wormData.wormNumber);
                    worm.spawnPosition = new Vector2(wormData.getPhysicsData().getPositionX(), wormData.getPhysicsData().getPositionY());
                    // TODO: setup worms
                }
            }
        }
        else
            throw new IllegalStateException("Either gameSetupData or gameSetupRequest must be set");
    }

    @Override
    protected void onUpdate(float delta) {
        currentTime += delta;

        if (getPlayers().get(currentPlayer).getClientId() == getClientId()) {
            updateTimer += delta;

            if (updateTimer >= UPDATE_FREQUENCY) {
                updateTimer -= UPDATE_FREQUENCY;

                WorldData data = new WorldData();

                int i = 0;
                PlayerData[] playerDataArray = new PlayerData[getPlayers().size()];

                for (Player player : getPlayers()) {
                    WormData[] wormDataArray = new WormData[player.getWorms().size()];

                    int index = 0;
                    for (Worm worm : player.getWorms()) {
                        WormData wormData = new WormData();
                        wormData.playerNumber = player.getPlayerNumber();
                        wormData.wormNumber = worm.getCharacterNumber();
                        if (worm.getBody() != null)
                        wormData.setPhysicsData(new PhysicsData().setPositionX(worm.getPosition().x).setPositionY(worm.getPosition().y));
                        wormData.setMovement(worm.getMovement());
                        wormData.setOrientation(worm.getOrientation());
                        wormDataArray[index++] = wormData;
                    }

                    playerDataArray[i++] = new PlayerData(player.getPlayerNumber(), wormDataArray);
                }

                data.players = playerDataArray;

                client.sendObject(data);
            }
        }
        else {
            float shiftedTime = updateCurrentSnapshots();
            float ratio = 0.0f;

            if (currentSnapshot != null && nextSnapshot != null)
                ratio = getSnapshotsRatio(shiftedTime);

            System.out.println("Ratio: " + ratio);

            if (currentSnapshot != null) {
                for (int i = 0; i < currentSnapshot.players.length; i++) {
                    PlayerData playerData = currentSnapshot.players[i];
                    Player player = getPlayers().get(playerData.getPlayerNumber());

                    for (int j = 0; j < playerData.getWorms().length; j++) {
                        WormData wormData = playerData.getWorms()[j];

                        Worm worm = player.getWormByNumber(wormData.wormNumber);

                        float x = wormData.getPhysicsData().getPositionX();
                        float y = wormData.getPhysicsData().getPositionY();

                        if (nextSnapshot != null) {
                            x = x * (1.0f - ratio) + nextSnapshot.players[i].getWorms()[j].getPhysicsData().getPositionX() * ratio;
                            y = y * (1.0f - ratio) + nextSnapshot.players[i].getWorms()[j].getPhysicsData().getPositionY() * ratio;
                        }

                        worm.setPosition(x, y);
                        worm.setMovement(wormData.getMovement());
                    }
                }
            }
        }
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

        for (Iterator<WorldData> it = receivedGameData.iterator(); it.hasNext(); ) {
            WorldData gameData = it.next();
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

    @Override
    public boolean shouldWorldStep() {
        return true;
        //return getPlayers().get(currentPlayer).getClientId() == getClientId();
    }

    public int getClientId() {
        return client.getClientId();
    }

    /*@Override
    public void update(float delta) {
        currentTime += delta;

        if (sendGameData) {
            updateTimer += delta;

            if (updateTimer >= UPDATE_FREQUENCY) {
                updateTimer -= UPDATE_FREQUENCY;

                WorldData worldData = new WorldData();
                //worldData.gameState = getWorld().getGameState().ordinal();
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
    }*/

    @Override
    protected void onEmitWorldData(WorldData data) {
        //client.sendTCP(data);
    }
}
