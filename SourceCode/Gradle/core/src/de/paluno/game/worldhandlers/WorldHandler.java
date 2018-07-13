package de.paluno.game.worldhandlers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.EventManager;
import de.paluno.game.GameState;
import de.paluno.game.gameobjects.*;
import de.paluno.game.interfaces.*;
import de.paluno.game.screens.PlayScreen;

import java.util.*;

public abstract class WorldHandler implements Disposable {

    private PlayScreen screen;
    private int mapNumber;

    private de.paluno.game.Map map;

    private GameWorld world;

    protected int currentPlayer;
    private int numPlayersAlive;
    private ArrayList<Player> players;
    private GameState currentGameState = GameState.NONE;

    private WindHandler windHandler;

    private WeaponIndicator currentWeaponIndicator;
    private HashMap<WeaponIndicator.Type, WeaponIndicator> weaponIndicators;
    private WindDirectionIndicator windDirectionIndicator;

    private ArrayList<Vector2> spawnPositions;

    private ArrayList<Projectile> weaponProjectileCache;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Turret> turrets;
    private ArrayList<AirdropCrate> crates;
    private ArrayList<AirdropChute> chutes;
    private int projectileId = 0;
    private int crateId = 0;
    private int chuteId = 0;
    private boolean canShoot = false;

    private float targetLevel = 0.0f;

    private int currentGameTick;
    private float updateTimer;
    private Replay replay;
    
    private Sound gunRelease;
    private Sound grenadeContact;
    private Sound walkLoop;
    private Sound fallOut;
    private Sound virusSound;
    private Sound airstrikeUse;
    private Sound onGroundSound;
    private Sound grenadeExplosionSound;
    private Sound airstrikeExplosionSound;
    private Sound airballSound;
    private Sound destroySound;
    private Sound headshotSound;
    private Sound gunShotSound;
    private Sound bazookaShotSound;
    private Sound throwSound;
    private Sound targetSound;
    private Sound airstrikeSound;
    private Sound airDropFall;
    private Sound lootEquipSound;
    private Sound raiseLimitSound;
    private Sound landSound;
    private Sound minePlace;
    private Sound turretPlace;
    private Sound teleporterUse;
    private Sound clickSound;

    private Timer.Task endTurnTimer = new Timer.Task() {

        @Override
        public void run() {
            setWaiting();
        }
    };

    private EventManager.Listener listener = (eventType, data) -> {
        switch (eventType) {
            case WeaponShoot: {
                WeaponType weapon = (WeaponType) data;
                switch (weapon) {
                	case WEAPON_TURRET:
                		turretPlace.play();
                		break;
                    case WEAPON_GUN:
                        gunShotSound.play();
                        break;
                    case WEAPON_BAZOOKA:
                        bazookaShotSound.play();
                        break;
                    case WEAPON_SPECIAL:
                        throwSound.play();
                        break;
                    case WEAPON_GRENADE:
                        throwSound.play();
                        break;
                    case WEAPON_AIRSTRIKE:
                        targetSound.play();
                        airstrikeSound.play();
                        break;
                    case WEAPON_MINE:
                    	minePlace.play();
                    	break;
                }

                if (getReplay() != null)
                    getReplay().setStartingTick(getCurrentGameTick(), 2.0f);

                break;
            }
            case WormEquipWeapon: {
                Weapon weapon = (Weapon) data;
                switch (weapon.getWeaponType()) {
                    case WEAPON_GUN:
                    case WEAPON_BAZOOKA:
                        gunRelease.play(0.5f);
                        break;
                    case WEAPON_MINE:
                    case WEAPON_TELEPORTER:
                    case WEAPON_AIRSTRIKE:
                        airstrikeUse.play();
                        break;
                }
                break;
            }
            case GrenadeCollision:
                grenadeContact.play();

                if (shouldWorldStep())
                    emitGameEvent(GameEvent.Type.GRENADE_COLLISION);
                break;
            case FeetCollision:
                onGroundSound.play();

                if (shouldWorldStep())
                    emitGameEvent(GameEvent.Type.FEET_COLLISION);
                break;
            case WormMovement: {
                Worm worm = (Worm) data;
                switch (worm.getMovement()) {
                    case Constants.MOVEMENT_NO_MOVEMENT:
                        walkLoop.stop();
                        break;
                    case Constants.MOVEMENT_LEFT:
                    case Constants.MOVEMENT_RIGHT:
                        walkLoop.stop();
                        walkLoop.loop();
                        break;
                }
                break;
            }
            case PlayerDefeated: {
                Player player = (Player) data;
                numPlayersAlive--;
                break;
            }
            case WormDied: {
                Worm.DeathEvent event = (Worm.DeathEvent) data;

                if (event.getDeathType() == Constants.DEATH_TYPE_FALL_DOWN)
                    fallOut.play();

                if (currentGameState == GameState.PLAYERTURN && getCurrentPlayer().getCurrentWorm() == event.getWorm()) {
                    System.out.println("Current worm died, setting to waiting");
                    setWaiting();
                }

                if (replay != null)
                    replay.setStartingTick(currentGameTick, 5.0f);

                GameEvent.Type type = null;
                switch (event.getDeathType()) {
                    case de.paluno.game.Constants.DEATH_TYPE_NO_HEALTH:
                        type = GameEvent.Type.WORM_DIED;
                        break;
                    case de.paluno.game.Constants.DEATH_TYPE_FALL_DOWN:
                        type = GameEvent.Type.WORM_FELL_DOWN;
                        break;
                }

                onWormDied(event);

                if (shouldWorldStep()) {
                    if (event.getDeathType() != de.paluno.game.Constants.DEATH_TYPE_DISCONNECTED) {
                        WormEvent wormEvent = new WormEvent(currentGameTick, type, event.getWorm().getPlayerNumber(), event.getWorm().getCharacterNumber());
                        emitGameData(wormEvent);
                    }
                }

                world.forgetAfterUpdate(event.getWorm());
                break;
            }
            case WormInfected:
                virusSound.play();

                if (shouldWorldStep()) {
                    Worm worm = (Worm) data;

                    WormEvent event = new WormEvent(currentGameTick, GameEvent.Type.WORM_INFECTED, worm.getPlayerNumber(), worm.getCharacterNumber());
                    emitGameData(event);
                }

                break;
            case WormTookDamage: {
                Worm.DamageEvent event = (Worm.DamageEvent) data;
                HoverText hoverText = new HoverText(event.getWorm(), "-" + event.getDamage(), Color.RED);
                world.registerAfterUpdate(hoverText);

                if (shouldWorldStep()) {
                    if (event.getDamageType() != de.paluno.game.Constants.DAMAGE_TYPE_VIRUS) {
                        WormDamageEvent gameEvent = new WormDamageEvent(currentGameTick, event.getWorm().getPlayerNumber(),
                                event.getWorm().getCharacterNumber(), event.getDamage(), event.getDamageType());
                        emitGameData(gameEvent);
                    }
                }
                break;
            }
            case ProjectileExploded:
                Projectile projectile = (Projectile) data;
                removeProjectile(projectile);

                switch (projectile.getWeaponType()) {
                    case WEAPON_GUN:
                        break;
                    case WEAPON_BAZOOKA:
                        destroySound.play();
                        break;
                    case WEAPON_MINE:
                    case WEAPON_GRENADE:
                        destroySound.play();
                        grenadeExplosionSound.play();
                        break;
                    case WEAPON_AIRSTRIKE:
                        destroySound.play();
                        airstrikeExplosionSound.play();
                        break;
                }

                if (shouldWorldStep()) {
                    Vector2 pos = projectile.getExplosion().getCenter();
                    ExplosionEvent e = new ExplosionEvent(currentGameTick, pos.x, pos.y, projectile.getExplosion().getRadius(), projectile.getExplosion().getBlastPower());
                    e.projectileId = projectile.getId();
                    emitGameData(e);
                }

                break;
            case AirBall:
                airballSound.play();

                if (shouldWorldStep())
                    emitGameEvent(GameEvent.Type.AIR_BALL);
                break;
            case Headshot:
                headshotSound.play();

                if (shouldWorldStep())
                    emitGameEvent(GameEvent.Type.HEADSHOT);
                break;
            case IdleRequest:
                setIdle();
                break;
            case DestroyChute: {
                AirdropChute chute = (AirdropChute) data;

                world.getWorld().destroyJoint(chute.getJoint());
                chute.setJointToNull();

                if (shouldWorldStep()) {
                    DestroyChuteEvent event = new DestroyChuteEvent(getCurrentGameTick(), GameEvent.Type.DESTROY_CHUTE, chute.getId());
                    emitGameData(event);
                }
                break;
            }
            case RemoveCrate: {
                AirdropCrate crate = (AirdropCrate)data;

                removeCrate(crate);

                if (shouldWorldStep()) {
                    RemoveCrateEvent event = new RemoveCrateEvent(getCurrentGameTick(), GameEvent.Type.REMOVE_CRATE, crate.getId());
                    emitGameData(event);
                }

                if (currentGameState == GameState.DROPPING)
                    setWaiting();
                break;
            }
            case RemoveChute: {
                AirdropChute chute = (AirdropChute)data;

                removeChute(chute);

                if (shouldWorldStep()) {
                    DestroyChuteEvent event = new DestroyChuteEvent(getCurrentGameTick(), GameEvent.Type.REMOVE_CHUTE, chute.getId());
                    emitGameData(event);
                }

                break;
            }
            case CrateLanded: {
                if (currentGameState == GameState.DROPPING)
                    setWaiting();
                
                landSound.play(1.5f);

                if (shouldWorldStep()) {
                    GameEvent event = new GameEvent(getCurrentGameTick(), GameEvent.Type.CRATE_LANDED);
                    emitGameData(event);
                }
                break;
            }
            case CratePickup: {
                AirdropCrate.PickupEvent event = (AirdropCrate.PickupEvent)data;

                HoverText hoverText = new HoverText(event.getWorm(),
                        "+1 " + event.getCrate().getDrop().getName(),
                        Constants.PLAYER_COLORS[event.getWorm().getPlayerNumber()]);
                world.registerAfterUpdate(hoverText);

                Player player = getPlayers().get(event.getWorm().getPlayerNumber());
                player.addAmmo(event.getCrate().getDrop());

                if (shouldWorldStep()) {
                    CratePickupEvent pickupEvent = new CratePickupEvent(getCurrentGameTick(), event.getCrate().getId(),
                            event.getWorm().getPlayerNumber(), event.getWorm().getCharacterNumber());
                    emitGameData(pickupEvent);
                }
                
                lootEquipSound.play();
                break;
            }
            case CrateFall:
            	airDropFall.play();
            	break;
            case TurretShot:
            	gunShotSound.play(3f);
            	break;
            case TeleporterUse:
            	teleporterUse.play();
            	break;
            case ClickSound:
            	clickSound.play();
            	break;
        }
    };

    protected abstract void onInitializePlayers();
    protected abstract boolean shouldAcceptInput();
    protected abstract boolean shouldWorldStep();
    protected abstract void requestNextTurn();
    protected abstract boolean shouldCreateReplay();

    protected void onWormDied(Worm.DeathEvent event) {}
    protected void onUpdate(float delta) {}
    protected void onEmitGameData(GameData gameData) {}
    protected boolean shouldStartInstantly() { return false; }

    public WorldHandler(PlayScreen screen, int mapNumber) {
        this.screen = screen;
        this.mapNumber = mapNumber;

        this.players = new ArrayList<>();

        this.projectiles = new ArrayList<>();
        this.turrets = new ArrayList<>();
        this.crates = new ArrayList<>();
        this.chutes = new ArrayList<>();
        this.weaponProjectileCache = new ArrayList<>();
        weaponIndicators = new HashMap<>();

        currentPlayer = -1;
    }

    public void initialize() {
        map = screen.getAssetManager().get(Assets.getMapByIndex(mapNumber));
        spawnPositions = new ArrayList<>(Arrays.asList(map.getSpawnPoints()));
        
        gunRelease = screen.getAssetManager().get(Assets.gunRelease);
        grenadeContact = screen.getAssetManager().get(Assets.grenadeContact);
        walkLoop = screen.getAssetManager().get(Assets.walkLoop);
        fallOut = screen.getAssetManager().get(Assets.fallDown);
        virusSound = screen.getAssetManager().get(Assets.virusSound);
        airstrikeUse = screen.getAssetManager().get(Assets.airstrikeUse);
        onGroundSound = screen.getAssetManager().get(Assets.onGroundSound);
        grenadeExplosionSound = screen.getAssetManager().get(Assets.grenadeExplosionSound);
        airstrikeExplosionSound = screen.getAssetManager().get(Assets.airstrikeExplosion);
        airballSound = screen.getAssetManager().get(Assets.airballSound);
        destroySound = screen.getAssetManager().get(Assets.destroySound);
        headshotSound = screen.getAssetManager().get(Assets.headshotSound);
        gunShotSound = screen.getAssetManager().get(Assets.gunShotSound);
        bazookaShotSound = screen.getAssetManager().get(Assets.bazookaShotSound);
        throwSound = screen.getAssetManager().get(Assets.throwSound);
        targetSound = screen.getAssetManager().get(Assets.targetSound);
        airstrikeSound = screen.getAssetManager().get(Assets.airstrikeSound);
        airDropFall = screen.getAssetManager().get(Assets.airDropFall);
        lootEquipSound = screen.getAssetManager().get(Assets.lootEquip);
        raiseLimitSound = screen.getAssetManager().get(Assets.raiseLimitSound);
        landSound = screen.getAssetManager().get(Assets.landSound);
        minePlace = screen.getAssetManager().get(Assets.minePlaceSound);
        turretPlace = screen.getAssetManager().get(Assets.turretUse);
        teleporterUse = screen.getAssetManager().get(Assets.teleporterUse);
        clickSound = screen.getAssetManager().get(Assets.clickSound);
        
        windHandler = new WindHandler();
        windHandler.setProjectiles(projectiles);

        windDirectionIndicator = new WindDirectionIndicator(windHandler);

        world = new GameWorld(this);
        world.initialize(map);
        world.registerAfterUpdate(windHandler);

        numPlayersAlive = 0;
        onInitializePlayers();

        currentGameState = GameState.NONE;

        if (shouldStartInstantly())
            setIdle();
    }

    public void updateViewport(float width, float height) {
        world.getCamera().updateViewport(width, height);
    }

    public void show() {
        EventManager.getInstance().addListener(listener,
                EventManager.Type.WormDied,
                EventManager.Type.WormInfected,
                EventManager.Type.ProjectileExploded,
                EventManager.Type.WormTookDamage,
                EventManager.Type.PlayerDefeated,
                EventManager.Type.WormEquipWeapon,
                EventManager.Type.GrenadeCollision,
                EventManager.Type.FeetCollision,
                EventManager.Type.WormMovement,
                EventManager.Type.WeaponShoot,
                EventManager.Type.AirBall,
                EventManager.Type.Headshot,
                EventManager.Type.IdleRequest,
                EventManager.Type.DestroyChute,
                EventManager.Type.CrateLanded,
                EventManager.Type.RemoveCrate,
                EventManager.Type.RemoveChute,
                EventManager.Type.CratePickup,
                EventManager.Type.CrateFall,
                EventManager.Type.TurretShot,
                EventManager.Type.TeleporterUse,
                EventManager.Type.ClickSound);

        for (Player player : players)
            player.show();
    }

    public void hide() {
        for (Player player : players)
            player.hide();

        EventManager.getInstance().removeListener(listener,
                EventManager.Type.WormDied,
                EventManager.Type.WormInfected,
                EventManager.Type.ProjectileExploded,
                EventManager.Type.WormTookDamage,
                EventManager.Type.PlayerDefeated,
                EventManager.Type.WormEquipWeapon,
                EventManager.Type.GrenadeCollision,
                EventManager.Type.FeetCollision,
                EventManager.Type.WormMovement,
                EventManager.Type.WeaponShoot,
                EventManager.Type.AirBall,
                EventManager.Type.Headshot,
                EventManager.Type.IdleRequest,
                EventManager.Type.DestroyChute,
                EventManager.Type.CrateLanded,
                EventManager.Type.RemoveCrate,
                EventManager.Type.RemoveChute,
                EventManager.Type.CratePickup,
                EventManager.Type.CrateFall,
                EventManager.Type.TurretShot,
                EventManager.Type.TeleporterUse,
                EventManager.Type.ClickSound);
    }

    @Override
    public void dispose() {
        world.dispose();

        hide();
    }

    public boolean isIdle() {
        return currentGameState == GameState.IDLE || currentGameState == GameState.NONE;
    }

    public void toggleDebugRender() {
        world.toggleDebugRender();
    }

    protected void emitGameEvent(GameEvent.Type type) {
        emitGameData(new GameEvent(getCurrentGameTick(), type));
    }

    protected void emitGameData(GameData gameData) {
        if (getReplay() != null)
            getReplay().addGameData(gameData);
        onEmitGameData(gameData);
    }

    protected void initializePlayersDefault(int numWorms) {
        for (int i = 0; i < de.paluno.game.Constants.NUM_PLAYERS; i++) {
            Player player = addPlayer(i);

            for (int j = 0; j < numWorms; j++) {
                Worm worm = addWorm(player, j,"");
                worm.setPosition(getRandomSpawnPosition());
            }
        }
    }

    protected ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    protected ArrayList<AirdropCrate> getCrates() {
        return crates;
    }

    protected ArrayList<AirdropChute> getChutes() {
        return chutes;
    }

    protected void addProjectile(Projectile.SnapshotData projectileData) {

        switch (projectileData.getWeaponType()) {
            case WEAPON_TURRET:
                Turret turret = new Turret(projectileData);
                projectiles.add(turret);
                world.registerAfterUpdate(turret);
                break;
            default:
                Projectile projectile = new Projectile(projectileData);
                projectiles.add(projectile);
                world.registerAfterUpdate(projectile);
                break;
        }
    }

    protected void addProjectile(Projectile projectile) {
        if (projectile.getWeaponType() != WeaponType.WEAPON_TURRET) {
            projectiles.add(projectile);
            projectile.setId(projectileId++);
        }
        world.registerAfterUpdate(projectile);

        if (projectile.getWeaponType() == WeaponType.WEAPON_TURRET_PROJECTILE)
            System.out.println("spawning turret projectile with id: " + projectile.getId() + " (" + toString() + ")");

        switch (projectile.getWeaponType()) {
            case WEAPON_MINE:
                unequipWeapon();
                canShoot = false;
                if (shouldWorldStep())
                Timer.schedule(endTurnTimer, Constants.END_TURN_TIMER_SECONDS);
                break;
            case WEAPON_TURRET:
                unequipWeapon();
                turrets.add((Turret)projectile);
                canShoot = false;
                if (shouldWorldStep())
                Timer.schedule(endTurnTimer, Constants.END_TURN_TIMER_SECONDS);
                break;
            default:
                world.getCamera().setCameraFocus(projectile);

                endPlayerTurn();

                currentGameState = GameState.SHOOTING;
                break;
        }
    }

    protected void addCrate(AirdropCrate crate) {
        if (crate.getId() == -1)
            crate.setId(crateId++);
        else if (crate.getId() >= crateId)
            crateId = crate.getId() + 1;
        crates.add(crate);
        world.registerAfterUpdate(crate);
    }

    protected void addChute(AirdropChute chute) {
        if (chute.getId() == -1)
            chute.setId(chuteId++);
        else if (chute.getId() >= chuteId)
            chuteId = chute.getId() + 1;
        chutes.add(chute);
        world.registerAfterUpdate(chute);
    }

    protected void removeChute(AirdropChute chute) {
        chutes.remove(chute);
        world.forgetAfterUpdate(chute);
    }

    protected void removeCrate(AirdropCrate crate) {
        crates.remove(crate);
        world.forgetAfterUpdate(crate);
    }

    protected void removeProjectile(Projectile projectile) {
        System.out.println("Called from " + toString());
        if (projectile.getWeaponType() == WeaponType.WEAPON_TURRET)
            turrets.remove(projectile);
        else
            projectiles.remove(projectile);

        world.forgetAfterUpdate(projectile);

        if (world.getCamera().getCameraFocus() == projectile) {
            if (!projectiles.isEmpty()) {
                loop: for (Projectile focusProjectile : projectiles) {
                    switch (focusProjectile.getWeaponType()) {
                        case WEAPON_TURRET:
                        case WEAPON_MINE:
                            break;
                        default:
                            world.getCamera().setCameraFocus(focusProjectile);
                            break loop;
                    }
                }
            }
        }

        boolean advance = true;
        for (Projectile projectile1 : projectiles) {
            if (projectile1.getWeaponType() != WeaponType.WEAPON_MINE) {
                advance = false;
                break;
            }
        }

        if (advance) {
            setWaiting();
        }
    }

    private void unequipWeapon() {
        Worm currentWorm = getCurrentPlayer().getCurrentWorm();
        currentWorm.unequipWeapon();

        if (currentWeaponIndicator instanceof ShotDirectionIndicator)
            ((ShotDirectionIndicator) currentWeaponIndicator).setRotationMovement(de.paluno.game.Constants.MOVEMENT_NO_MOVEMENT);

        if (currentWeaponIndicator != null)
            currentWorm.removeChild(currentWeaponIndicator);

        currentWeaponIndicator = null;
    }

    protected void equipWeapon(WeaponType weaponType) {
        getCurrentPlayer().equipWeapon(weaponType);

        if (currentWeaponIndicator != null && currentWeaponIndicator.getType() == weaponType.getIndicatorType())
            return;

        Worm currentWorm = getCurrentPlayer().getCurrentWorm();

        if (currentWeaponIndicator != null)
            currentWorm.removeChild(currentWeaponIndicator);

        currentWeaponIndicator = weaponIndicators.computeIfAbsent(weaponType.getIndicatorType(), WeaponIndicator.Type::newInstance);

        if (currentWeaponIndicator != null)
            currentWorm.addChild(currentWeaponIndicator);
    }

    protected Projectile getProjectileById(int id) {
        for (Projectile projectile : projectiles) {
            if (projectile.getId() == id)
                return projectile;
        }

        return null;
    }

    protected AirdropCrate getCrateById(int id) {
        for (AirdropCrate crate : crates) {
            if (crate.getId() == id)
                return crate;
        }

        return null;
    }

    protected AirdropChute getChuteById(int id) {
        for (AirdropChute chute : chutes) {
            if (chute.getId() == id)
                return chute;
        }

        return null;
    }

    protected void setCurrentPlayerTurn(int playerNumber, int wormNumber) {
        currentGameState = GameState.PLAYERTURN;

        currentPlayer = playerNumber;

        for (Player player : players) {
            player.setWormsStatic(true);

            if (player.getPlayerNumber() == playerNumber) {
                player.setTurn(wormNumber);
                Worm worm = player.getWormByNumber(wormNumber);
                System.out.println("Starting player turn for playerNumber: " + playerNumber + ", wormNumber: " + wormNumber + ", wormDead: " + worm.isDead());
                worm.setIsPlaying(true);
                worm.addChild(windDirectionIndicator);
                world.getCamera().setCameraFocus(worm);
                canShoot = true;
                equipWeapon(WeaponType.WEAPON_BAZOOKA);

                createReplayPlayerTurn(playerNumber, wormNumber);
                EventManager.getInstance().queueEvent(EventManager.Type.StartPlayerTurn, player);
            }
        }
    }

    private void createReplayPlayerTurn(int playerNumber, int wormNumber) {
        if (shouldCreateReplay()) {
            replay = new Replay(Replay.TYPE_PLAYER_TURN);
            replay.setSetupSnapshot(new WorldStateSnapshot(getWorld(), getPlayers(), getProjectiles(), turrets, crates));
            replay.setPlayerTurn(playerNumber, wormNumber);
            replay.setSetupTick(currentGameTick);
            replay.setMapNumber(getMapNumber());
            replay.setCameraPosition(new Vector2(getWorld().getCamera().getWorldPosition()));
            replay.setCameraZoom(world.getCamera().getZoom());
        }
    }

    private void createReplayTurrets() {
        if (shouldCreateReplay()) {
            replay = new Replay(Replay.TYPE_TURRETS);
            replay.setSetupSnapshot(new WorldStateSnapshot(getWorld(), getPlayers(), getProjectiles(), turrets, crates));
            replay.setSetupTick(currentGameTick);
            replay.setMapNumber(getMapNumber());
            replay.setCameraPosition(new Vector2(getWorld().getCamera().getWorldPosition()));
            replay.setCameraZoom(world.getCamera().getZoom());
        }
    }

    protected void endPlayerTurn() {
        if (currentGameState == GameState.PLAYERTURN) {
            Worm worm = getCurrentPlayer().getCurrentWorm();
            worm.setMovement(de.paluno.game.Constants.MOVEMENT_NO_MOVEMENT);
            worm.setIsPlaying(false);

            if (currentWeaponIndicator instanceof ShotDirectionIndicator)
                ((ShotDirectionIndicator) currentWeaponIndicator).setRotationMovement(Constants.MOVEMENT_NO_MOVEMENT);

            unequipWeapon();

            worm.removeChild(windDirectionIndicator);

            players.forEach(player -> player.setWormsStatic(false));
        }
    }

    protected Replay getReplay() {
        return replay;
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public AssetManager getAssetManager() {
        return screen.getAssetManager();
    }

    protected Vector2 getRandomSpawnPosition() {
        return spawnPositions.remove(new Random().nextInt(spawnPositions.size()));
    }

    protected Vector2 getRandomAirdropPosition() {
        Random random = new Random();
        float x = random.nextFloat() * map.getWorldWidth();
        return new Vector2(x, map.getWorldHeight()- (700 * Constants.WORLD_SCALE));
    }

    public int getNumPlayersAlive() {
        return numPlayersAlive;
    }

    protected Player addPlayer(int playerNumber) {
        // TODO: remove playerNumber parameter
        numPlayersAlive++;
        Player player = new Player(playerNumber);
        players.add(playerNumber, player);
        addWeapons(player);
        return player;
    }

    private void addWeapons(Player player) {
        for (WeaponType type : WeaponType.values()) {
            Weapon weapon = new Weapon(type);
            weapon.setAirstrikeHeight(map.getWorldHeight());
            weapon.setupAssets(getAssetManager());
            player.addWeapon(weapon);
        }
    }

    protected Worm addWorm(Player player, int wormNumber, String name) {
        Worm worm = player.addWorm(wormNumber, name);
        world.registerAfterUpdate(worm);
        return worm;
    }

    public de.paluno.game.Map getMap() {
        return map;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        if (currentPlayer == -1)
            return null;

        return players.get(currentPlayer);
    }

    public WindHandler getWindHandler() {
        return windHandler;
    }

    protected WeaponIndicator getWeaponIndicator() {
        return currentWeaponIndicator;
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }

    public void applyWormMovement(int movement) {
        if (shouldAcceptInput() && currentGameState == GameState.PLAYERTURN) {
            if (movement != de.paluno.game.Constants.MOVEMENT_NO_MOVEMENT && world.getCamera().getCameraFocus() != getCurrentPlayer().getCurrentWorm())
                world.getCamera().setCameraFocus(getCurrentPlayer().getCurrentWorm());

            players.get(currentPlayer).getCurrentWorm().setMovement(movement);
        }
    }

    public void applyEquipWeapon(WeaponType weaponType) {
        if (shouldAcceptInput() && currentGameState == GameState.PLAYERTURN) {
        	equipWeapon(weaponType);
        }
    }

    public void applyWormJump() {
        if (shouldAcceptInput() && currentGameState == GameState.PLAYERTURN) {
            players.get(currentPlayer).getCurrentWorm().setJump(true);
        }
    }

    public void applyCameraMovement(int vertical, int horizontal) {
        world.getCamera().setCameraFocus(null);
        world.getCamera().setVerticalMovement(vertical);
        world.getCamera().setHorizontalMovement(horizontal);
    }

    public WeaponIndicator getCurrentWeaponIndicator() {
        if (shouldAcceptInput() && currentGameState == GameState.PLAYERTURN)
            return currentWeaponIndicator;

        return null;
    }

    public void shoot() {
        if (canShoot && shouldAcceptInput() && currentGameState == GameState.PLAYERTURN) {
            Player player = getCurrentPlayer();
            Worm worm = player.getCurrentWorm();
            player.getCurrentWeapon().shoot(worm, currentWeaponIndicator, weaponProjectileCache);
            shootProjectiles(player.getPlayerNumber(), worm.getCharacterNumber(), player.getCurrentWeapon().getWeaponType());
        }
    }

    private void shootProjectiles(int playerNumber, int wormNumber, WeaponType type) {
        weaponProjectileCache.forEach(this::addProjectile);

        ProjectileData[] projectilesArray = new ProjectileData[weaponProjectileCache.size()];

        int index = 0;
        for (Projectile projectile : weaponProjectileCache) {
            ProjectileData data = new ProjectileData()
                    .setId(projectile.getId())
                    .setPlayerNumber(playerNumber)
                    .setWormNumber(wormNumber)
                    .setType(projectile.getWeaponType().ordinal())
                    .setPhysicsData(new PhysicsData()
                            .setPositionX(projectile.getPosition().x)
                            .setPositionY(projectile.getPosition().y));
            projectilesArray[index++] = data;
        }

        ShootEvent event = new ShootEvent(currentGameTick, type.ordinal(), projectilesArray);
        emitGameData(event);
        weaponProjectileCache.clear();
    }

    private Worm getNextActiveWorm() {
        float vel = 0.00001f;
        Worm nextWorm = null;

        for (Player player : players) {
            for (Worm worm : player.getWorms()) {
                if (worm.getBody() != null && worm.getBody().isAwake()) {
                    if (worm.getBody().getLinearVelocity().len2() > vel) {
                        vel = worm.getBody().getLinearVelocity().len2();
                        nextWorm = worm;
                    }
                }
            }
        }

        return nextWorm;
    }

    private boolean allWormsIdle() {
        boolean allIdle = true;

        players: for (Player player : players) {
            for (Worm worm : player.getWorms()) {
                if (worm != null && worm.getBody() != null && worm.getBody().isAwake()) {
                    allIdle = false;
                    break players;
                }
            }
        }

        if (allIdle) {
            for (AirdropCrate crate : crates) {
                if (crate != null && crate.getBody() != null && crate.getBody().isAwake()) {
                    allIdle = false;
                    break;
                }
            }
        }

        return allIdle;
    }

    public void setWaiting() {
        endTurnTimer.cancel();

        endPlayerTurn();

        Worm nextActiveWorm = getNextActiveWorm();
        currentGameState = GameState.WAITING;

        if (nextActiveWorm != null) {
            world.getCamera().setCameraFocus(nextActiveWorm);
        }
    }

    protected int getCurrentGameTick() {
        return currentGameTick;
    }

    protected void setCurrentGameTick(int tick) {
        currentGameTick = tick;
    }

    public void updateAndRender(SpriteBatch batch, float delta) {
        if (shouldWorldStep()) {
            world.update(delta);
            world.step();

            if (!isIdle()) {
                updateTimer += delta;
                if (updateTimer >= de.paluno.game.Constants.UPDATE_FREQUENCY) {
                    updateTimer -= Constants.UPDATE_FREQUENCY;

                    currentGameTick++;
                    WorldData worldData = makeWorldSnapshot();
                    emitGameData(worldData);
                }
            }
        }

        if (currentGameState == GameState.RAISE_WATER_LEVEL) {
            world.setWaterLevel(world.getWaterLevel() + delta * Constants.RAISE_LEVEL_SPEED);
            if (world.getWaterLevel() >= targetLevel) {
                setIdle();
            	raiseLimitSound.stop();
            }
        }

        onUpdate(delta);

        world.render(batch, delta);

        if (currentGameState == GameState.NONE) {
            if (allWormsIdle()) {
                setIdle();
            }
        }

        if (shouldWorldStep() && currentGameState == GameState.WAITING) {
            if (allWormsIdle()) {
                setIdle();
            }
        }
    }

    protected void setIdle() {
        endPlayerTurn();
        currentGameState = GameState.IDLE;
        requestNextTurn();
    }

    public GameWorld getWorld() {
        return world;
    }

    public boolean shootTurrets() {
        if (turrets.isEmpty())
            return false;

        currentGameState = GameState.SHOOTING;

        createReplayTurrets();

        if (shouldWorldStep()) {
            for (Turret turret : turrets) {
                turret.shoot(weaponProjectileCache);
                shootProjectiles(turret.getShootingWorm().getPlayerNumber(), turret.getShootingWorm().getCharacterNumber(), WeaponType.WEAPON_TURRET_PROJECTILE);
            }
        }

        return true;
    }

    public void createAirdrop(Vector2 position, WeaponType drop) {
        AirdropCrate crate = new AirdropCrate(position, drop);
        addCrate(crate);
        AirdropChute chute = new AirdropChute(crate);
        crate.setChute(chute);
        addChute(chute);
        world.getCamera().setCameraFocus(crate);

        beginAirdrop();

        if (shouldWorldStep()) {
            SpawnAirdropEvent event = new SpawnAirdropEvent(getCurrentGameTick(), crate.getId(), chute.getId(), position.x, position.y, drop.ordinal());
            emitGameData(event);
        }
    }

    public void beginAirdrop() {
    	EventManager.getInstance().queueEvent(EventManager.Type.CrateFall, null);
        endPlayerTurn();
        currentGameState = GameState.DROPPING;
    }

    public void randomAirdrop() {
        createAirdrop(getRandomAirdropPosition(), WeaponType.getRandomDrop());
    }

    public void raiseWaterLevel() {
        replay = null;
        currentGameState = GameState.RAISE_WATER_LEVEL;
        targetLevel = world.getWaterLevel() + Constants.RAISE_LEVEL_LENGTH;
        world.getCamera().goToPoint(world.getCamera().getWorldPosition().x, 0.0f);
        raiseLimitSound.loop();
    }

    private WorldData makeWorldSnapshot() {
        WorldData data = new WorldData(currentGameTick, false);
        if (getWeaponIndicator() != null)
            data.setIndicatorData(getWeaponIndicator().makeSnapshot());

        if (!getProjectiles().isEmpty()) {
            ProjectileData[] projectiles = new ProjectileData[getProjectiles().size()];

            int index = 0;
            for (Projectile projectile : getProjectiles()) {
                projectiles[index++] = new ProjectileData()
                        .setId(projectile.getId())
                        .setType(projectile.getWeaponType().ordinal())
                        .setPhysicsData(projectile.generatePhysicsData());
            }

            data.setProjectiles(projectiles);
        }

        if (!crates.isEmpty()) {
            CrateData[] cratesArray = new CrateData[crates.size()];

            int index = 0;
            for (AirdropCrate crate : crates) {
                cratesArray[index++] = new CrateData()
                        .setId(crate.getId())
                        .setPhysicsData(crate.generatePhysicsData());
            }

            data.setCrates(cratesArray);
        }

        if (!chutes.isEmpty()) {
            CrateData[] cratesArray = new CrateData[chutes.size()];

            int index = 0;
            for (AirdropChute chute : chutes) {
                cratesArray[index++] = new CrateData()
                        .setId(chute.getId())
                        .setPhysicsData(chute.generatePhysicsData());
            }

            data.setChutes(cratesArray);
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
                        .setPhysicsData(worm.generatePhysicsData());
            }

            playerDataArray[i++] = new PlayerData(player.getClientId(), player.getPlayerNumber(), wormDataArray, null);
        }

        data.setPlayers(playerDataArray);

        Worm currentWorm = getCurrentPlayer().getCurrentWorm();
        if (currentWorm.getCurrentWeapon() != null) {
            data.setCurrentWeapon(currentWorm.getCurrentWeapon().getWeaponType().ordinal());
        }

        return data;
    }
}
