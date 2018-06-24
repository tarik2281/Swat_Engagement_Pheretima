package de.paluno.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import de.paluno.game.gameobjects.*;
import de.paluno.game.interfaces.WorldData;
import de.paluno.game.screens.PlayScreen;

import java.util.*;

public abstract class WorldHandler implements Disposable {

    private PlayScreen screen;
    private int mapNumber;

    private Map map;

    private World2 world;

    public int currentPlayer;
    private ArrayList<Player> players;
    public GameState currentGameState = GameState.NONE;

    public ShotDirectionIndicator shotDirectionIndicator;

    private ArrayList<Vector2> spawnPositions;

    public Projectile projectile;

    public WorldHandler(PlayScreen screen, int mapNumber) {
        this.screen = screen;
        this.mapNumber = mapNumber;

        this.players = new ArrayList<>();
    }

    public void initialize() {
        map = screen.getAssetManager().get(Assets.getMapByIndex(mapNumber));
        spawnPositions = new ArrayList<>(Arrays.asList(map.getSpawnPoints()));
        shotDirectionIndicator = new ShotDirectionIndicator();

        world = new World2(this);
        world.initialize(map);

        onInitializePlayers();

        currentGameState = GameState.WAITING;
    }

    public void initializePlayersDefault(int numWorms) {
        for (int i = 0; i < Constants.NUM_PLAYERS; i++) {
            Player player = addPlayer(i);

            addWeapon(player, WeaponType.WEAPON_BAZOOKA);
            addWeapon(player, WeaponType.WEAPON_GRENADE);
            addWeapon(player, WeaponType.WEAPON_GUN);
            addWeapon(player, WeaponType.WEAPON_SPECIAL);

            for (int j = 0; j < numWorms; j++) {
                Worm worm = addWorm(player, j);
                worm.spawnPosition = getRandomSpawnPosition();
            }
        }
    }

    public void addWeapon(Player player, WeaponType type) {
        Weapon weapon = new Weapon(type);
        weapon.setupAssets(getAssetManager());
        player.addWeapon(weapon);
    }

    public void setGameState(GameState state) {
        currentGameState = state;
    }

    @Override
    public void dispose() {
        world.dispose();
    }

    public AssetManager getAssetManager() {
        return screen.getAssetManager();
    }

    public Vector2 getRandomSpawnPosition() {
        return spawnPositions.remove(new Random().nextInt(spawnPositions.size()));
    }

    public Player addPlayer(int playerNumber) {
        // TODO: remove playerNumber parameter
        Player player = new Player(playerNumber);
        players.add(playerNumber, player);
        return player;
    }

    public Worm addWorm(Player player, int wormNumber) {
        Worm worm = player.addWorm(wormNumber);
        world.registerAfterUpdate(worm);
        return worm;
    }

    public boolean shouldWorldStep() {
        return true;
    }

    public Map getMap() {
        return map;
    }

    public void onInitializePlayers() {

    }

    public boolean requestNextTurn() {
        return false;
    }

    public boolean requestAdvanceGameState() {
        return false;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean shouldIdle() {
        return false;
    }

    public void applyWormMovement(int movement) {
        if (currentGameState == GameState.PLAYERTURN) {
            setWormMovement(movement);
        }
    }

    protected void setWormMovement(int movement) {
        players.get(currentPlayer).getCurrentWorm().setMovement(movement);
    }

    public void applyWormJump() {
        if (currentGameState == GameState.PLAYERTURN) {
            players.get(currentPlayer).getCurrentWorm().setJump(true);
        }
    }

    public void applyShotDirectionMovement(int movement) {
        if (currentGameState == GameState.PLAYERTURN) {
            shotDirectionIndicator.setRotationMovement(movement);
        }
    }

    public void shoot() {
        if (currentGameState == GameState.PLAYERTURN) {
            Worm worm = players.get(currentPlayer).getCurrentWorm();
            Projectile projectile = new Projectile(worm, WeaponType.WEAPON_BAZOOKA,
                    worm.getBody().getWorldCenter(), new Vector2(1, 0).rotate(shotDirectionIndicator.getAngle()));
            world.registerAfterUpdate(projectile);
            currentGameState = GameState.SHOOTING;
            world.forgetAfterUpdate(shotDirectionIndicator);
            world.getCamera().setCameraFocus(projectile);

            this.projectile = projectile;

            for (Player player : players) {
                player.setWormsStatic(false);
            }
        }
    }

    public void onAddExplosion(Explosion explosion) {

    }

    public void setCurrentPlayerTurn(int playerNumber, int wormNumber) {
        currentGameState = GameState.PLAYERTURN;

        currentPlayer = playerNumber;

        for (Player player : players) {
            player.setWormsStatic(true);

            if (player.getPlayerNumber() == playerNumber) {
                player.setTurn(wormNumber);
                Worm worm = player.getWormByNumber(wormNumber);
                worm.setIsPlaying(true);
                worm.addChild(shotDirectionIndicator);
                world.getCamera().setCameraFocus(worm);
                player.equipWeapon(WeaponType.WEAPON_BAZOOKA);
            }
        }
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

    public void updateAndRender(SpriteBatch batch, float delta) {
        if (shouldWorldStep()) {
            world.update(delta);
            world.step();
        }

        onUpdate(delta);

        world.render(batch, delta);

        if (currentGameState == GameState.WAITING) {
            if (allWormsIdle()) {
                setIdle();
            }
        }
    }

    public void setIdle() {
        currentGameState = GameState.IDLE;
        requestNextTurn();
    }

    public World2 getWorld() {
        return world;
    }

    protected abstract void onEmitWorldData(WorldData data);

    protected abstract void onUpdate(float delta);
}
