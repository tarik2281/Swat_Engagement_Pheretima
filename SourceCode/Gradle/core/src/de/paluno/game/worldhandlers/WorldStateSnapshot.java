package de.paluno.game.worldhandlers;

import de.paluno.game.gameobjects.GameWorld;
import de.paluno.game.gameobjects.Player;

import java.util.Collection;

public class WorldStateSnapshot {
    private GameWorld.SnapshotData worldSnapshot;
    private Player.SnapshotData[] playerSnapshots;

    public WorldStateSnapshot(GameWorld world, Collection<Player> players) {
        worldSnapshot = world.makeSnapshot();

        playerSnapshots = new Player.SnapshotData[players.size()];
        int index = 0;
        for (Player player : players)
            playerSnapshots[index++] = player.makeSnapshot();
    }

    public GameWorld.SnapshotData getWorldSnapshot() {
        return worldSnapshot;
    }

    public Player.SnapshotData[] getPlayerSnapshots() {
        return playerSnapshots;
    }
}
