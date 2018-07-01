package de.paluno.game;

import com.badlogic.gdx.Gdx;
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
    private boolean sendGameData;

    private WorldData currentSnapshot;
    private WorldData nextSnapshot;

    private int numWorms;
    private GameSetupRequest gameSetupRequest;
    private GameSetupData gameSetupData;
    private PhysicsData physicsCache = new PhysicsData();

    private DataHandler<StartTurnEvent> startTurnHandler = (client, data) -> {
        System.out.println("Received start turn request");
        Gdx.app.postRunnable(() -> {
            setCurrentPlayerTurn(data.playerNumber, data.wormNumber);
            getWindHandler().setWind(data.wind);
        });
    };

    private DataHandler<GameOverEvent> gameOverHandler = (client, data) -> {
        Gdx.app.postRunnable(() -> {
            WinningPlayer winningPlayer = WinningPlayer.NONE;
            switch (data.winningPlayer) {
                case 0:
                    winningPlayer = WinningPlayer.PLAYERONE;
                    break;
                case 1:
                    winningPlayer = WinningPlayer.PLAYERTWO;
                    break;
            }
            EventManager.getInstance().queueEvent(EventManager.Type.GameOver, winningPlayer);
        });
    };

    private DataHandler<WorldData> worldDataHandler = (client, data) -> {
        data.setReceivingTimeStamp(currentTime);

        Gdx.app.postRunnable(() -> receivedGameData.add(data));
    };

    private DataHandler<GameEvent> eventHandler = (client, data) -> {
        data.setReceivingTimeStamp(currentTime);

        Gdx.app.postRunnable(() -> receivedGameEvents.add(data));
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

        MessageData messageData = new MessageData(MessageData.Type.ClientReady);
        client.sendObject(messageData);
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
        client.registerDataHandler(StartTurnEvent.class, startTurnHandler);
        client.registerDataHandler(WorldData.class, worldDataHandler);
        client.registerDataHandler(ExplosionEvent.class, eventHandler);
        client.registerDataHandler(EndTurnEvent.class, eventHandler);
        client.registerDataHandler(GameEvent.class, eventHandler);
        client.registerDataHandler(ShootEvent.class, eventHandler);
        client.registerDataHandler(WormEvent.class, eventHandler);
        client.registerDataHandler(WormDamageEvent.class, eventHandler);
        client.registerDataHandler(GameOverEvent.class, gameOverHandler);

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
                    worm.setPosition(getRandomSpawnPosition());

                    wormData[j] = new WormData()
                            .setPlayerNumber(player.getPlayerNumber())
                            .setWormNumber(j)
                            .setPhysicsData(new PhysicsData()
                                    .setPositionX(worm.getPosition().x)
                                    .setPositionY(worm.getPosition().y));
                }

                playerData[i] = new PlayerData(player.getPlayerNumber(), wormData);
            }

            GameSetupData data = new GameSetupData(gameSetupRequest.getClientIds(), playerData);
            data.mapNumber = getMapNumber();
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
        WorldData data = new WorldData(0, usingTCP);
        data.setShootingAngle(getShotDirectionIndicator().getAngle());

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

            playerDataArray[i++] = new PlayerData(player.getPlayerNumber(), wormDataArray);
        }

        data.setPlayers(playerDataArray);

        Worm currentWorm = getCurrentPlayer().getCurrentWorm();
        if (currentWorm.getCurrentWeapon() != null) {
            data.setCurrentWeapon(currentWorm.getCurrentWeapon().getWeaponType().ordinal());
        }

        if (usingTCP)
            client.sendObject(data);
        else
            client.sendObjectUDP(data);
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
                    getCurrentPlayer().equipWeapon(WeaponType.values()[currentWeapon]);
            }

            float angle = currentSnapshot.getShootingAngle();
            if (nextSnapshot != null)
                angle = angle * from + nextSnapshot.getShootingAngle() * to;
            getShotDirectionIndicator().setAngle(angle);

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
                    worm.die();
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

        if (isControllingCurrentPlayer()) {
            updateTimer += delta;

            if (updateTimer >= UPDATE_FREQUENCY) {
                updateTimer -= UPDATE_FREQUENCY;

                sendWorldSnapshot(false);
            }
        }
        else {
            interpolateWorldSnapshots();
        }
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

        ShootEvent event = new ShootEvent(0, projectilesArray);
        client.sendObject(event);
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
        ExplosionEvent e = new ExplosionEvent(0, pos.x, pos.y, projectile.getExplosion().getRadius(), projectile.getExplosion().getBlastPower());
        e.projectileId = projectile.getId();
        client.sendObject(e);
    }

    @Override
    protected void onWormDied(Worm worm) {
        WormEvent event = new WormEvent(0, GameEvent.Type.WORM_DIED, worm.getPlayerNumber(), worm.getCharacterNumber());
        client.sendObject(event);
    }

    @Override
    protected void onWormInfected(Worm worm) {
        WormEvent event = new WormEvent(0, GameEvent.Type.WORM_INFECTED, worm.getPlayerNumber(), worm.getCharacterNumber());
        client.sendObject(event);
    }

    @Override
    protected void onWormTookDamage(Worm.DamageEvent event) {
        if (event.getDamageType() == Constants.DAMAGE_TYPE_VIRUS)
            return;

        WormDamageEvent gameEvent = new WormDamageEvent(0, event.getWorm().getPlayerNumber(),
                event.getWorm().getCharacterNumber(), event.getDamage(), event.getDamageType());
        client.sendObject(gameEvent);
    }

    @Override
    public boolean shouldWorldStep() {
        return getCurrentGameState() == GameState.NONE || isControllingCurrentPlayer();
    }

    public int getClientId() {
        return client.getClientId();
    }
}
