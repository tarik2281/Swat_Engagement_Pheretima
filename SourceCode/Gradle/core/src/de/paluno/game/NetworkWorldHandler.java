package de.paluno.game;

import com.badlogic.gdx.math.Vector2;
import de.paluno.game.gameobjects.*;
import de.paluno.game.interfaces.*;
import de.paluno.game.screens.PlayScreen;
import de.paluno.game.screens.WinningPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NetworkWorldHandler extends WorldHandler {

    private static final float UPDATE_FREQUENCY = 1.0f / 30.0f; // 30Hz
    private static final float TIME_SHIFT = 0.3f; // 300 ms delay

    private NetworkClient client;

    private ArrayList<GameEvent> receivedGameEvents = new ArrayList<>();
    private ArrayList<WorldData> receivedGameData = new ArrayList<>();

    private float currentTime;
    private int currentTick;
    private float updateTimer;
    private float idleTime;
    private boolean sendGameData;

    private WorldData currentSnapshot;
    private WorldData nextSnapshot;

    private int numWorms;
    private GameSetupRequest gameSetupRequest;
    private GameSetupData gameSetupData;
    private PhysicsData physicsCache = new PhysicsData();

    private DataHandler dataHandler = new DataHandler() {
        @Override
        public void handleData(NetworkClient client, Object data) {
            if (data instanceof StartTurnEvent) {
                StartTurnEvent event = (StartTurnEvent)data;
                setCurrentPlayerTurn(event.playerNumber, event.wormNumber);
                getWindHandler().setWind(event.wind);
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
                worldData.setReceivingTimeStamp(worldData.getTick() * UPDATE_FREQUENCY + idleTime);
                receivedGameData.add(worldData);
            }
            else if (data instanceof GameEvent) {
                GameEvent event = (GameEvent)data;
                event.setReceivingTimeStamp(event.getTick() * UPDATE_FREQUENCY + idleTime);
                receivedGameEvents.add(event);
            }
            else if (data instanceof UserMessage) {
                UserMessage message = (UserMessage)data;
                if (message.getType() == Message.Type.UserLeft) {
                    for (Player player : getPlayers()) {
                        if (player.getClientId() == message.getUserId()) {
                            for (Worm worm : player.getWorms())
                                worm.die(Constants.DEATH_TYPE_DISCONNECTED);
                            break;
                        }
                    }
                }
            }
        }
    };

    public NetworkWorldHandler(PlayScreen screen, NetworkClient client, GameSetupData data) {
        super(screen, data.mapNumber);

        this.client = client;

        this.gameSetupData = data;
    }

    public NetworkWorldHandler(PlayScreen screen, NetworkClient client, GameSetupRequest request) {
        super(screen, request.getMapNumber());

        this.client = client;

        this.gameSetupRequest = request;
        this.numWorms = request.getNumWorms();
    }

    @Override
    public boolean shouldAcceptInput() {
        return isControllingCurrentPlayer();
    }

    @Override
    protected void requestNextTurn() {
        if (isControllingCurrentPlayer())
            sendWorldSnapshot(true);

        currentSnapshot = null;
        nextSnapshot = null;
        receivedGameData.clear();

        client.send(Message.clientReady());
    }

    private GameEvent pollEvents() {
        float shiftedTime = currentTime - TIME_SHIFT;

        for (Iterator<GameEvent> it = receivedGameEvents.iterator(); it.hasNext(); ) {
            GameEvent event = it.next();
            if (shiftedTime >= event.getReceivingTimeStamp()) {
                it.remove();
                return event;
            }
        }

        return null;
    }

    @Override
    public void onInitializePlayers() {
        currentTick = 0;
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

    public void sendWorldSnapshot(boolean usingTCP) {
        WorldData data = new WorldData(currentTick, usingTCP);
        if (getShotDirectionIndicator() != null)
        data.setIndicatorData(getShotDirectionIndicator().makeSnapshot());

        if (!getProjectiles().isEmpty()) {
            ProjectileData[] projectiles = new ProjectileData[getProjectiles().size()];

            int index = 0;
            for (Projectile projectile : getProjectiles()) {
                projectiles[index++] = new ProjectileData()
                        .setId(projectile.getId())
                        .setType(projectile.getWeaponType().ordinal())
                        .setPhysicsData(new PhysicsData()
                                .setPositionX(projectile.getPosition().x)
                                .setPositionY(projectile.getPosition().y)
                                .setVelocityX(projectile.getVelocity().x)
                                .setVelocityY(projectile.getVelocity().y)
                                .setAngle(projectile.getAngle()));
            }

            data.setProjectiles(projectiles);
        }

        int i = 0;
        PlayerData[] playerDataArray = new PlayerData[getPlayers().size()];

        for (Player player : getPlayers()) {
            WormData[] wormDataArray = new WormData[player.getWorms().size()];

            int index = 0;
            for (Worm worm : player.getWorms()) {
                wormDataArray[index++] = new WormData()
                        .setPlayerNumber(worm.getPlayerNumber())
                        .setWormNumber(worm.getCharacterNumber())
                        .setNumGroundContacts(worm.getNumContacts())
                        .setMovement(worm.getMovement())
                        .setOrientation(worm.getOrientation())
                        .setPhysicsData(new PhysicsData()
                                .setPositionX(worm.getPosition().x)
                                .setPositionY(worm.getPosition().y)
                                .setVelocityX(worm.getVelocity().x)
                                .setVelocityY(worm.getVelocity().y));
            }

            playerDataArray[i++] = new PlayerData(player.getClientId(), player.getPlayerNumber(), wormDataArray);
        }

        data.setPlayers(playerDataArray);

        Worm currentWorm = getCurrentPlayer().getCurrentWorm();
        if (currentWorm.getCurrentWeapon() != null) {
            data.setCurrentWeapon(currentWorm.getCurrentWeapon().getWeaponType().ordinal());
        }

        if (usingTCP)
            client.send(data);
        else
            client.sendUDP(data);
    }

    public void interpolateWorldSnapshots() {
        float shiftedTime = updateCurrentSnapshots();

        if (currentSnapshot != null) {
            float to = 0.0f;

            if (nextSnapshot != null)
                to = getSnapshotsRatio(shiftedTime);

            float from = 1.0f - to;

            int currentWeapon = currentSnapshot.getCurrentWeapon();
            if (currentWeapon != -1) {
                Worm currentWorm = getCurrentPlayer().getCurrentWorm();

                if (currentWorm.getCurrentWeapon() != null && currentWorm.getCurrentWeapon().getWeaponType().ordinal() != currentWeapon)
                    equipWeapon(WeaponType.values()[currentWeapon]);
            }

            Object fromData = currentSnapshot.getIndicatorData();
            Object toData = null;
            if (nextSnapshot != null)
                toData = nextSnapshot.getIndicatorData();

            if (getShotDirectionIndicator() != null) {
                if (toData != null && toData.getClass() != fromData.getClass())
                    toData = null;

                getShotDirectionIndicator().interpolateSnapshots(fromData, toData, to);
            }

            for (Projectile projectile : getProjectiles()) {
                ProjectileData currentData = currentSnapshot.getProjectileById(projectile.getId());
                if (currentData != null) {
                    PhysicsData physics = null;

                    if (nextSnapshot != null) {
                        ProjectileData nextData = nextSnapshot.getProjectileById(projectile.getId());
                        if (nextData != null)
                            physics = nextData.getPhysicsData();
                    }

                    physics = physicsCache.interpolate(currentData.getPhysicsData(), physics, to);
                    projectile.setPhysics(physics);
                }
            }

            for (PlayerData playerData : currentSnapshot.getPlayers()) {
                Player player = getPlayers().get(playerData.getPlayerNumber());

                for (WormData wormData : playerData.getWorms()) {
                    Worm worm = player.getWormByNumber(wormData.getWormNumber());

                    if (worm != null && !worm.isDead()) {
                        PhysicsData physics = null;

                        if (nextSnapshot != null) {
                            physics = nextSnapshot.getPlayer(player.getPlayerNumber()).getWormByNumber(wormData.getWormNumber()).getPhysicsData();
                        }

                        physics = physicsCache.interpolate(wormData.getPhysicsData(), physics, to);
                        worm.setPhysics(physics);
                        worm.setMovement(wormData.getMovement());
                        worm.setNumContacts(wormData.getNumGroundContacts());
                    }
                }
            }
        }
    }

    public boolean isControllingCurrentPlayer() {
        Player currentPlayer = getCurrentPlayer();
        return currentPlayer != null && currentPlayer.getClientId() == getClientId();
    }

    @Override
    protected void onUpdate(float delta) {
        currentTime += delta;

        GameEvent currentEvent;
        while ((currentEvent = pollEvents()) != null) {
            switch (currentEvent.getType()) {
                case EXPLOSION: {
                    ExplosionEvent ex = (ExplosionEvent)currentEvent;
                    Projectile projectile = getProjectileById(ex.projectileId);
                    getWorld().addExplosion(new Explosion(new Vector2(ex.getCenterX(), ex.getCenterY()),
                            ex.getRadius(), ex.getBlastPower()));
                    removeProjectile(projectile);
                    break;
                }
                case SHOOT: {
                    for (ProjectileData data : ((ShootEvent) currentEvent).projectiles) {
                        Projectile projectile = new Projectile(null, WeaponType.values()[data.getType()],
                                new Vector2(data.getPhysicsData().getPositionX(), data.getPhysicsData().getPositionY()), new Vector2());
                        addProjectile(projectile);
                        projectile.setId(data.getId());
                    }
                    break;
                }
                case END_TURN: {
                    setIdle();
                    break;
                }
                case WORM_DIED: {
                    WormEvent event = (WormEvent)currentEvent;
                    Player player = getPlayers().get(event.getPlayerNumber());
                    Worm worm = player.getWormByNumber(event.getWormNumber());
                    worm.die(Constants.DEATH_TYPE_NO_HEALTH);
                    break;
                }
                case WORM_FELL_DOWN: {
                    WormEvent event = (WormEvent)currentEvent;
                    Player player = getPlayers().get(event.getPlayerNumber());
                    Worm worm = player.getWormByNumber(event.getWormNumber());
                    worm.die(Constants.DEATH_TYPE_FALL_DOWN);
                    break;
                }
                case WORM_INFECTED: {
                    WormEvent event = (WormEvent)currentEvent;
                    Player player = getPlayers().get(event.getPlayerNumber());
                    Worm worm = player.getWormByNumber(event.getWormNumber());
                    worm.setIsInfected(true);
                    break;
                }
                case WORM_TOOK_DAMAGE: {
                    WormDamageEvent event = (WormDamageEvent)currentEvent;
                    Player player = getPlayers().get(event.getPlayerNumber());
                    Worm worm = player.getWormByNumber(event.getWormNumber());
                    worm.takeDamage(event.getDamage(), event.getDamageType());
                    break;
                }
            }
        }

        if (getCurrentGameState() != GameState.NONE && getCurrentGameState() != GameState.IDLE) {
            updateTimer += delta;
            if (updateTimer >= UPDATE_FREQUENCY) {
                updateTimer -= UPDATE_FREQUENCY;
                currentTick++;

                if (isControllingCurrentPlayer()) {
                    sendWorldSnapshot(false);
                }
            }
        }
        else
            idleTime += delta;

        if (!isControllingCurrentPlayer())
            interpolateWorldSnapshots();
    }

    @Override
    public void onShoot(List<Projectile> projectiles) {
        ProjectileData[] projectilesArray = new ProjectileData[projectiles.size()];

        int index = 0;
        for (Projectile projectile : projectiles) {
            ProjectileData data = new ProjectileData()
                    .setId(projectile.getId())
                    .setType(projectile.getWeaponType().ordinal())
                    .setPhysicsData(new PhysicsData()
                        .setPositionX(projectile.getPosition().x)
                        .setPositionY(projectile.getPosition().y));
            projectilesArray[index++] = data;
        }

        ShootEvent event = new ShootEvent(currentTick, projectilesArray);
        client.send(event);
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
    public void onProjectileExploded(Projectile projectile) {
        Vector2 pos = projectile.getExplosion().getCenter();
        ExplosionEvent e = new ExplosionEvent(currentTick, pos.x, pos.y, projectile.getExplosion().getRadius(), projectile.getExplosion().getBlastPower());
        e.projectileId = projectile.getId();
        client.send(e);
    }

    @Override
    protected void onWormDied(Worm.DeathEvent event) {
        if (event.getDeathType() == Constants.DEATH_TYPE_DISCONNECTED)
            return;

        GameEvent.Type type = null;
        switch (event.getDeathType()) {
            case Constants.DEATH_TYPE_NO_HEALTH:
                type = GameEvent.Type.WORM_DIED;
                break;
            case Constants.DEATH_TYPE_FALL_DOWN:
                type = GameEvent.Type.WORM_FELL_DOWN;
                break;
        }

        WormEvent wormEvent = new WormEvent(currentTick, type, event.getWorm().getPlayerNumber(), event.getWorm().getCharacterNumber());
        client.send(wormEvent);
    }

    @Override
    protected void onWormInfected(Worm worm) {
        WormEvent event = new WormEvent(currentTick, GameEvent.Type.WORM_INFECTED, worm.getPlayerNumber(), worm.getCharacterNumber());
        client.send(event);
    }

    @Override
    protected void onWormTookDamage(Worm.DamageEvent event) {
        if (event.getDamageType() == Constants.DAMAGE_TYPE_VIRUS)
            return;

        WormDamageEvent gameEvent = new WormDamageEvent(currentTick, event.getWorm().getPlayerNumber(),
                event.getWorm().getCharacterNumber(), event.getDamage(), event.getDamageType());
        client.send(gameEvent);
    }

    @Override
    public boolean shouldWorldStep() {
        return getCurrentGameState() == GameState.NONE || isControllingCurrentPlayer();
    }

    public int getClientId() {
        return client.getClientId();
    }

    @Override
    protected boolean shouldCreateReplay() {
        return true;
    }
}
