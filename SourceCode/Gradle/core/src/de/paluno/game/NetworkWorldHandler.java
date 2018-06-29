package de.paluno.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.gameobjects.*;
import de.paluno.game.interfaces.*;
import de.paluno.game.screens.PlayScreen;

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

    private DataHandler<StartTurnEvent> startTurnHandler = (client, data) -> {
        System.out.println("Received start turn request");
        Gdx.app.postRunnable(() -> {
            setCurrentPlayerTurn(data.playerNumber, data.wormNumber);
            getWindHandler().setWind(data.wind);
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

                    WormData wormD = new WormData();
                    wormD.playerNumber = player.getPlayerNumber();
                    wormD.wormNumber = j;
                    wormD.setPhysicsData(new PhysicsData()
                            .setPositionX(worm.getPosition().x)
                            .setPositionY(worm.getPosition().y));

                    wormData[j] = wormD;
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
                    Worm worm = addWorm(player, wormData.wormNumber);
                    worm.setPosition(wormData.getPhysicsData().getPositionX(),
                            wormData.getPhysicsData().getPositionY());
                    // TODO: setup worms
                }
            }
        }
        else
            throw new IllegalStateException("Either gameSetupData or gameSetupRequest must be set");
    }

    public void sendWorldSnapshot() {
        WorldData data = new WorldData();
        data.shootingAngle = getShotDirectionIndicator().getAngle();

        if (!getProjectiles().isEmpty()) {
            ProjectileData[] projectiles = new ProjectileData[getProjectiles().size()];

            int index = 0;
            for (Projectile projectile : getProjectiles()) {
                ProjectileData projectileData = new ProjectileData();
                projectileData.id = projectile.getId();
                projectileData.setType(projectile.getWeaponType().ordinal());
                projectileData.setPhysicsData(new PhysicsData()
                        .setPositionX(projectile.getPosition().x)
                        .setPositionY(projectile.getPosition().y)
                        .setVelocityX(projectile.getVelocity().x)
                        .setVelocityY(projectile.getVelocity().y)
                        .setAngle(projectile.getAngle()));
                projectiles[index++] = projectileData;
            }

            data.projectiles = projectiles;
        }

        int i = 0;
        PlayerData[] playerDataArray = new PlayerData[getPlayers().size()];

        for (Player player : getPlayers()) {
            WormData[] wormDataArray = new WormData[player.getWorms().size()];

            int index = 0;
            for (Worm worm : player.getWorms()) {
                WormData wormData = new WormData();
                wormData.numGroundContacts = worm.getNumContacts();
                wormData.playerNumber = player.getPlayerNumber();
                wormData.wormNumber = worm.getCharacterNumber();
                wormData.setPhysicsData(new PhysicsData().setPositionX(worm.getPosition().x).setPositionY(worm.getPosition().y)
                        .setVelocityX(worm.getVelocity().x).setVelocityY(worm.getVelocity().y));
                wormData.setMovement(worm.getMovement());
                wormData.setOrientation(worm.getOrientation());
                wormDataArray[index++] = wormData;
            }

            playerDataArray[i++] = new PlayerData(player.getPlayerNumber(), wormDataArray);
        }

        data.players = playerDataArray;

        Worm currentWorm = getCurrentPlayer().getCurrentWorm();
        if (currentWorm.getCurrentWeapon() != null) {
            data.currentWeapon = currentWorm.getCurrentWeapon().getWeaponType().ordinal();
        }

        client.sendObjectUDP(data);
    }

    public void interpolateWorldSnapshots() {
        float shiftedTime = updateCurrentSnapshots();
        float ratio = 0.0f;

        if (currentSnapshot != null && nextSnapshot != null)
            ratio = getSnapshotsRatio(shiftedTime);

        if (currentSnapshot != null) {
            if (currentSnapshot.currentWeapon != -1) {
                Worm currentWorm = getCurrentPlayer().getCurrentWorm();
                if (currentWorm.getCurrentWeapon() != null && currentWorm.getCurrentWeapon().getWeaponType().ordinal() != currentSnapshot.currentWeapon) {
                    getCurrentPlayer().equipWeapon(WeaponType.values()[currentSnapshot.currentWeapon]);
                }
            }

            float angle = currentSnapshot.shootingAngle;
            if (nextSnapshot != null)
                angle = angle * (1.0f - ratio) + nextSnapshot.shootingAngle * ratio;
            getShotDirectionIndicator().setAngle(angle);

            if (currentSnapshot.projectiles != null) {
                for (ProjectileData projectileData : currentSnapshot.projectiles) {
                    float x = projectileData.getPhysicsData().getPositionX();
                    float y = projectileData.getPhysicsData().getPositionY();
                    float velX = projectileData.getPhysicsData().getVelocityX();
                    float velY = projectileData.getPhysicsData().getVelocityY();
                    float projectileAngle = projectileData.getPhysicsData().getAngle();

                    if (nextSnapshot != null && nextSnapshot.projectiles != null) {
                        for (ProjectileData nextProjectileData : nextSnapshot.projectiles) {
                            if (nextProjectileData.id == projectileData.id) {
                                x = x * (1.0f - ratio) + nextProjectileData.getPhysicsData().getPositionX() * ratio;
                                y = y * (1.0f - ratio) + nextProjectileData.getPhysicsData().getPositionY() * ratio;
                                velX = velX * (1.0f - ratio) + nextProjectileData.getPhysicsData().getVelocityX() * ratio;
                                velY = velY * (1.0f - ratio) + nextProjectileData.getPhysicsData().getVelocityY() * ratio;
                                projectileAngle = projectileAngle * (1.0f - ratio) + nextProjectileData.getPhysicsData().getAngle() * ratio;
                                break;
                            }
                        }
                    }

                    Projectile projectile = getProjectileById(projectileData.id);
                    if (projectile == null) {
                        System.out.println("No projectile found for " + projectileData.id);
                    }
                    if (projectile != null) {
                        projectile.setPosition(x, y);
                        projectile.setVelocity(velX, velY);
                        projectile.setAngle(projectileAngle);
                    }
                }
            }

            for (int i = 0; i < currentSnapshot.players.length; i++) {
                PlayerData playerData = currentSnapshot.players[i];
                Player player = getPlayers().get(playerData.getPlayerNumber());

                for (int j = 0; j < playerData.getWorms().length; j++) {
                    WormData wormData = playerData.getWorms()[j];

                    Worm worm = player.getWormByNumber(wormData.wormNumber);

                    if (worm != null) {

                        float x = wormData.getPhysicsData().getPositionX();
                        float y = wormData.getPhysicsData().getPositionY();
                        float velX = wormData.getPhysicsData().getVelocityX();
                        float velY = wormData.getPhysicsData().getVelocityY();

                        if (nextSnapshot != null) {
                            for (int k = 0; k < nextSnapshot.players[i].getWorms().length; k++) {
                                if (nextSnapshot.players[i].getWorms()[k].wormNumber == wormData.wormNumber) {
                                    x = x * (1.0f - ratio) + nextSnapshot.players[i].getWorms()[k].getPhysicsData().getPositionX() * ratio;
                                    y = y * (1.0f - ratio) + nextSnapshot.players[i].getWorms()[k].getPhysicsData().getPositionY() * ratio;
                                    velX = velX * (1.0f - ratio) + nextSnapshot.players[i].getWorms()[k].getPhysicsData().getVelocityX() * ratio;
                                    velY = velX * (1.0f - ratio) + nextSnapshot.players[i].getWorms()[k].getPhysicsData().getVelocityY() * ratio;
                                }
                            }
                        }

                        worm.setNumContacts(wormData.numGroundContacts);

                        worm.setPosition(x, y);
                        worm.setVelocity(velX, velY);
                        worm.setMovement(wormData.getMovement());
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

        if (isControllingCurrentPlayer()) {
            updateTimer += delta;

            if (updateTimer >= UPDATE_FREQUENCY) {
                updateTimer -= UPDATE_FREQUENCY;

                sendWorldSnapshot();
            }
        }
        else {
            interpolateWorldSnapshots();

            GameEvent currentEvent;
            while ((currentEvent = pollEvents()) != null) {
                switch (currentEvent.getType()) {
                    case EXPLOSION: {
                        ExplosionEvent ex = (ExplosionEvent)currentEvent;
                        Projectile projectile = getProjectileById(ex.projectileId);
                        getWorld().addExplosion(new Explosion(new Vector2(ex.getCenterX(), ex.getCenterY()),
                                ex.getRadius(), 1.0f));
                        removeProjectile(projectile);
                        break;
                    }
                    case SHOOT: {
                        for (ProjectileData data : ((ShootEvent) currentEvent).projectiles) {
                            Projectile projectile = new Projectile(null, WeaponType.values()[data.getType()],
                                    new Vector2(data.getPhysicsData().getPositionX(), data.getPhysicsData().getPositionY()), new Vector2());
                            addProjectile(projectile);
                            projectile.setId(data.id);
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
                        getWorld().forgetAfterUpdate(worm);
                        player.removeWorm(worm);
                        EventManager.getInstance().queueEvent(EventManager.Type.WormDied, worm);
                        break;
                    }
                    case WORM_INFECTED: {
                        WormEvent event = (WormEvent)currentEvent;
                        Player player = getPlayers().get(event.getPlayerNumber());
                        Worm worm = player.getWormByNumber(event.getWormNumber());
                        worm.setIsInfected(true);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onShoot(List<Projectile> projectiles) {
        ProjectileData[] projectilesArray = new ProjectileData[projectiles.size()];

        int index = 0;
        for (Projectile projectile : projectiles) {
            ProjectileData data = new ProjectileData()
                    .setType(projectile.getWeaponType().ordinal())
                    .setPhysicsData(new PhysicsData()
                        .setPositionX(projectile.getPosition().x)
                        .setPositionY(projectile.getPosition().y));
            data.id = projectile.getId();
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
        ExplosionEvent e = new ExplosionEvent(0, pos.x, pos.y, projectile.getExplosion().getRadius());
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
    public boolean shouldWorldStep() {
        return getCurrentGameState() == GameState.NONE || isControllingCurrentPlayer();
    }

    public int getClientId() {
        return client.getClientId();
    }
}
