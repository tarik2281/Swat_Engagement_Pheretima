package de.paluno.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import de.paluno.game.gameobjects.*;
import de.paluno.game.screens.PlayScreen;

import java.util.*;

public abstract class WorldHandler implements Disposable {

    private PlayScreen screen;
    private int mapNumber;

    private Map map;

    private World2 world;

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
    private int projectileId = 0;

    private EventManager.Listener listener = (eventType, data) -> {
        switch (eventType) {
            case PlayerDefeated: {
                Player player = (Player)data;
                numPlayersAlive--;
                break;
            }
            case WormDied: {
                Worm worm = (Worm) data;

                if (shouldWorldStep()) {
                    if (getCurrentPlayer().getCurrentWorm() == worm) {
                        System.out.println("Current worm died, setting to waiting");
                        setWaiting();
                    }

                    onWormDied(worm);
                }

                world.forgetAfterUpdate(worm);
                break;
            }
            case WormInfected:
                if (shouldWorldStep()) {
                    Worm worm = (Worm)data;
                    onWormInfected(worm);
                }
                break;
            case WormTookDamage: {
                if (shouldWorldStep()) {
                    Worm.DamageEvent event = (Worm.DamageEvent)data;
                    onWormTookDamage(event);
                }
                break;
            }
            case ProjectileExploded:
                if (shouldWorldStep()) {
                    Projectile projectile = (Projectile) data;
                    removeProjectile(projectile);
                    onProjectileExploded(projectile);
                }
                break;
        }
    };

    protected abstract void onInitializePlayers();
    protected abstract boolean shouldAcceptInput();
    protected abstract boolean shouldWorldStep();
    protected abstract void requestNextTurn();

    protected void onWormTookDamage(Worm.DamageEvent event) {}
    protected void onWormDied(Worm worm) {}
    protected void onWormInfected(Worm worm) {}
    protected void onProjectileExploded(Projectile projectile) {}
    protected void onShoot(List<Projectile> projectiles) {}
    protected void onUpdate(float delta) {}

    public WorldHandler(PlayScreen screen, int mapNumber) {
        this.screen = screen;
        this.mapNumber = mapNumber;

        this.players = new ArrayList<>();

        this.projectiles = new ArrayList<>();
        this.weaponProjectileCache = new ArrayList<>();

        currentPlayer = -1;
    }

    public void initialize() {
        map = screen.getAssetManager().get(Assets.getMapByIndex(mapNumber));
        spawnPositions = new ArrayList<>(Arrays.asList(map.getSpawnPoints()));

        windHandler = new WindHandler();
        windHandler.setProjectiles(projectiles);

        windDirectionIndicator = new WindDirectionIndicator(windHandler);

        world = new World2(this);
        world.initialize(map);
        world.registerAfterUpdate(windHandler);

        numPlayersAlive = 0;
        onInitializePlayers();

        currentGameState = GameState.NONE;

        EventManager.getInstance().addListener(listener,
                EventManager.Type.WormDied,
                EventManager.Type.WormInfected,
                EventManager.Type.ProjectileExploded,
                EventManager.Type.WormTookDamage,
                EventManager.Type.PlayerDefeated);
    }

    @Override
    public void dispose() {
        world.dispose();

        for (Player player : players)
            player.dispose();

        EventManager.getInstance().removeListener(listener,
                EventManager.Type.WormDied,
                EventManager.Type.WormInfected,
                EventManager.Type.ProjectileExploded,
                EventManager.Type.WormTookDamage,
                EventManager.Type.PlayerDefeated);
    }

    protected void initializePlayersDefault(int numWorms) {
        for (int i = 0; i < Constants.NUM_PLAYERS; i++) {
            Player player = addPlayer(i);

            addWeapon(player, WeaponType.WEAPON_BAZOOKA);
            addWeapon(player, WeaponType.WEAPON_GRENADE);
            addWeapon(player, WeaponType.WEAPON_GUN);
            addWeapon(player, WeaponType.WEAPON_SPECIAL);

            for (int j = 0; j < numWorms; j++) {
                Worm worm = addWorm(player, j);
                worm.setPosition(getRandomSpawnPosition());
            }
        }
    }

    protected ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    protected void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
        projectile.setId(projectileId++);
        world.registerAfterUpdate(projectile);
        world.getCamera().setCameraFocus(projectile);

        endPlayerTurn();

        currentGameState = GameState.SHOOTING;
    }

    protected void removeProjectile(Projectile projectile) {
        projectiles.remove(projectile);
        world.forgetAfterUpdate(projectile);

        if (world.getCamera().getCameraFocus() == projectile) {
            if (!projectiles.isEmpty())
                world.getCamera().setCameraFocus(projectiles.get(0));
        }

        if (projectiles.isEmpty()) {
            setWaiting();
        }
    }

    public void equipWeapon(WeaponType weaponType) {
        if (currentWeaponIndicator.getType() == weaponType.getIndicatorType())
            return;

        Worm currentWorm = getCurrentPlayer().getCurrentWorm();

        if (currentWeaponIndicator != null)


        getCurrentPlayer().equipWeapon(weaponType);

        currentWeaponIndicator = weaponIndicators.computeIfAbsent(weaponType.getIndicatorType(), WeaponIndicator.Type::newInstance);
        currentWorm.addChild(indicator);
    }

    protected Projectile getProjectileById(int id) {
        for (Projectile projectile : projectiles) {
            if (projectile.getId() == id)
                return projectile;
        }

        return null;
    }

    protected void setCurrentPlayerTurn(int playerNumber, int wormNumber) {
        currentGameState = GameState.PLAYERTURN;

        currentPlayer = playerNumber;

        for (Player player : players) {
            player.setWormsStatic(true);

            if (player.getPlayerNumber() == playerNumber) {
                System.out.println("Starting player turn for playerNumber: " + playerNumber);
                player.setTurn(wormNumber);
                Worm worm = player.getWormByNumber(wormNumber);
                worm.setIsPlaying(true);
                worm.addChild(windDirectionIndicator);
                world.getCamera().setCameraFocus(worm);
                player.equipWeapon(WeaponType.WEAPON_BAZOOKA);
            }
        }
    }

    protected void endPlayerTurn() {
        if (currentGameState == GameState.PLAYERTURN) {
            System.out.println("ending player turn");
            Worm worm = getCurrentPlayer().getCurrentWorm();
            worm.setMovement(Constants.MOVEMENT_NO_MOVEMENT);
            worm.unequipWeapon();
            worm.setIsPlaying(false);

            shotDirectionIndicator.setRotationMovement(Constants.MOVEMENT_NO_MOVEMENT);

            worm.removeChild(shotDirectionIndicator);
            worm.removeChild(windDirectionIndicator);

            players.forEach(player -> player.setWormsStatic(false));
        }
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public AssetManager getAssetManager() {
        return screen.getAssetManager();
    }

    public Vector2 getRandomSpawnPosition() {
        return spawnPositions.remove(new Random().nextInt(spawnPositions.size()));
    }

    public int getNumPlayersAlive() {
        return numPlayersAlive;
    }

    public Player addPlayer(int playerNumber) {
        // TODO: remove playerNumber parameter
        numPlayersAlive++;
        Player player = new Player(playerNumber);
        players.add(playerNumber, player);
        return player;
    }

    public void addWeapon(Player player, WeaponType type) {
        Weapon weapon = new Weapon(type);
        weapon.setupAssets(getAssetManager());
        player.addWeapon(weapon);
    }

    public Worm addWorm(Player player, int wormNumber) {
        Worm worm = player.addWorm(wormNumber);
        world.registerAfterUpdate(worm);
        return worm;
    }

    public Map getMap() {
        return map;
    }

    public List<Player> getPlayers() {
        return players;
    }

    protected int getCurrentPlayerIndex() {
        return currentPlayer;
    }

    public Player getCurrentPlayer() {
        if (currentPlayer == -1)
            return null;

        return players.get(currentPlayer);
    }

    public WindHandler getWindHandler() {
        return windHandler;
    }

    public ShotDirectionIndicator getShotDirectionIndicator() {
        return shotDirectionIndicator;
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }

    public void applyWormMovement(int movement) {
        if (shouldAcceptInput() && currentGameState == GameState.PLAYERTURN) {
            System.out.println("Applying movement for player: " + currentPlayer + " for worm: " + getCurrentPlayer().getCurrentWorm().getCharacterNumber() + " with turn: " + getCurrentPlayer().getTurn());
            setWormMovement(movement);
        }
    }

    protected void setWormMovement(int movement) {
        players.get(currentPlayer).getCurrentWorm().setMovement(movement);
    }

    public void applyEquipWeapon(WeaponType weaponType) {
        if (shouldAcceptInput()) {
            Player player = getCurrentPlayer();
            player.equipWeapon(weaponType);
        }
    }

    public void applyWormJump() {
        if (shouldAcceptInput() && currentGameState == GameState.PLAYERTURN) {
            System.out.println("Applying jump for player: " + currentPlayer + " for worm: " + getCurrentPlayer().getCurrentWorm().getCharacterNumber() + " with turn: " + getCurrentPlayer().getTurn());
            players.get(currentPlayer).getCurrentWorm().setJump(true);
        }
    }

    public void applyShotDirectionMovement(int movement) {
        if (shouldAcceptInput() && currentGameState == GameState.PLAYERTURN) {
            shotDirectionIndicator.setRotationMovement(movement);
        }
    }

    public void shoot() {
        if (shouldAcceptInput() && currentGameState == GameState.PLAYERTURN) {
            Player player = getCurrentPlayer();
            Worm worm = player.getCurrentWorm();
            weaponProjectileCache.clear();
            player.getCurrentWeapon().shoot(worm, shotDirectionIndicator, weaponProjectileCache);
            weaponProjectileCache.forEach(this::addProjectile);
            onShoot(weaponProjectileCache);
            //Projectile projectile = new Projectile(worm, WeaponType.WEAPON_BAZOOKA,
            //        worm.getBody().getWorldCenter(), new Vector2(1, 0).rotate(shotDirectionIndicator.getAngle()));
            //addProjectile(projectile);
            //world.registerAfterUpdate(projectile);
            //currentGameState = GameState.SHOOTING;
            //world.forgetAfterUpdate(shotDirectionIndicator);
            //world.getCamera().setCameraFocus(projectile);

            //this.projectile = projectile;

            //for (Player player : players) {
            //  player.setWormsStatic(false);
            //}
        }
    }

    public boolean queryWeaponAvailable(WeaponType weaponType) {
        Player player = players.get(currentPlayer);
        return player.getWeapon(weaponType).getSelectable();
    }

    public Worm getNextActiveWorm() {
        Vector2 velocity = null;
        Worm nextWorm = null;

        for (Player player : players) {
            for (Worm worm : player.getWorms()) {
                if (worm.getBody() != null && worm.getBody().isAwake()) {
                    if (velocity == null) {
                        velocity = worm.getBody().getLinearVelocity();
                        nextWorm = worm;
                    }
                    else if (worm.getBody().getLinearVelocity().len2() > velocity.len2()) {
                        velocity = worm.getBody().getLinearVelocity();
                        nextWorm = worm;
                    }
                }
            }
        }

        return nextWorm;
    }

    public boolean allWormsIdle() {
        boolean allIdle = true;

        players: for (Player player : players) {
            for (Worm worm : player.getWorms()) {
                if (worm != null && worm.getBody() != null && worm.getBody().isAwake()) {
                    allIdle = false;
                    break players;
                }
            }
        }

        return allIdle;
    }

    public void setWaiting() {
        endPlayerTurn();

        Worm nextActiveWorm = getNextActiveWorm();
        currentGameState = GameState.WAITING;

        if (nextActiveWorm != null) {
            world.getCamera().setCameraFocus(nextActiveWorm);
        }
        else {

        }
    }

    public void updateAndRender(SpriteBatch batch, float delta) {
        if (shouldWorldStep()) {
            world.update(delta);
            world.step();
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
        System.out.println("Setting idle");
        currentGameState = GameState.IDLE;
        requestNextTurn();
    }

    public World2 getWorld() {
        return world;
    }
}
