package de.paluno.game.worldhandlers;

import com.badlogic.gdx.math.Vector2;
import de.paluno.game.Constants;
import de.paluno.game.interfaces.GameData;
import de.paluno.game.interfaces.GameEvent;
import de.paluno.game.interfaces.WorldData;

import java.util.ArrayList;
import java.util.List;

public class Replay {

    public static final int TYPE_PLAYER_TURN = 0;
    public static final int TYPE_TURRETS = 1;

    private WorldStateSnapshot setupSnapshot;
    private int type;
    private int mapNumber;
    private int playerNumber;
    private int wormNumber;
    private int setupTick;
    private int startingTick;
    private int wind;
    private Vector2 cameraPosition;
    private float cameraZoom;
    private ArrayList<GameEvent> events;
    private ArrayList<WorldData> snapshots;

    public Replay(int type) {
        this.type = type;

        events = new ArrayList<>();
        snapshots = new ArrayList<>();

        startingTick = -1;
    }

    public int getType() {
        return type;
    }

    public void addGameData(GameData data) {
        data.setReceivingTimeStamp(data.getTick() * Constants.UPDATE_FREQUENCY);

        if (data instanceof GameEvent)
            events.add((GameEvent)data);
        else if (data instanceof WorldData)
            snapshots.add((WorldData)data);
    }

    public void setSetupSnapshot(WorldStateSnapshot snapshot) {
        setupSnapshot = snapshot;
    }

    public WorldStateSnapshot getSetupSnapshot() {
        return setupSnapshot;
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public void setMapNumber(int mapNumber) {
        this.mapNumber = mapNumber;
    }

    public float getStartingTime() {
        int tick = startingTick;

        if (tick == -1)
            tick = setupTick;

        return tick * Constants.UPDATE_FREQUENCY;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }

    public void setSetupTick(int setupTick) {
        this.setupTick = setupTick;
    }

    public void setStartingTick(int startingTick, float shiftSeconds) {
        if (this.startingTick == -1) {
            int tickOffset = (int) Math.floor(shiftSeconds / Constants.UPDATE_FREQUENCY);
            if (this.setupTick < startingTick - tickOffset)
                this.startingTick = startingTick - tickOffset;
        }
    }

    public Vector2 getCameraPosition() {
        return cameraPosition;
    }

    public void setCameraPosition(Vector2 cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public float getCameraZoom() {
        return cameraZoom;
    }

    public void setCameraZoom(float cameraZoom) {
        this.cameraZoom = cameraZoom;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getWormNumber() {
        return wormNumber;
    }

    public int getWind() {
        return wind;
    }

    public void setPlayerTurn(int player, int worm) {
        this.playerNumber = player;
        this.wormNumber = worm;
    }

    public List<GameEvent> getGameEvents() {
        return events;
    }

    public List<WorldData> getWorldSnapshots() {
        return snapshots;
    }
}
