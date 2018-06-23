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

    private ArrayList<Player> players;
    private GameState currentGameState = GameState.NONE;

    private ArrayList<Worm> worms;

    private ShotDirectionIndicator shotDirectionIndicator;

    private List<Vector2> spawnPositions;

    public WorldHandler(PlayScreen screen, int mapNumber) {
        this.screen = screen;
        this.mapNumber = mapNumber;

        this.players = new ArrayList<>();
    }

    public void initialize() {
        map = screen.getAssetManager().get(Assets.getMapByIndex(mapNumber));
        spawnPositions = Arrays.asList(map.getSpawnPoints());

        world = new World2(this);
        world.initialize(map);

        onInitializePlayers();
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

    public Worm addWorm(Player player) {
        Worm worm = player.addWorm();
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

    }

    public Collection<Worm> getWorms() {
        return worms;
    }

    public boolean shouldIdle() {
        return false;
    }

    public void applyWormMovement(int movement) {

    }

    public void applyWormJump() {

    }

    public void applyShotDirectionMovement(int movement) {

    }

    public boolean allWormsIdle() {
        boolean allIdle = true;

        players: for (Player player : players) {
            for (Worm worm : player.getCharacters()) {
                if (worm != null && worm.getBody() != null && worm.getBody().isAwake()) {
                    allIdle = false;
                    break players;
                }
            }
        }

        return allIdle;
    }

    public void update(float delta) {

    }

    public void updateAndRender(SpriteBatch batch, float delta) {

    }

    public World2 getWorld() {
        return world;
    }

    protected abstract void onEmitWorldData(WorldData data);
}
