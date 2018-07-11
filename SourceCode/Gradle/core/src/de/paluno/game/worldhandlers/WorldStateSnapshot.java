package de.paluno.game.worldhandlers;

import de.paluno.game.gameobjects.GameWorld;
import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.Projectile;
import de.paluno.game.gameobjects.Turret;

import java.util.Collection;

public class WorldStateSnapshot {
    private GameWorld.SnapshotData worldSnapshot;
    private Player.SnapshotData[] playerSnapshots;
    private Projectile.SnapshotData[] projectileSnapshots;
    private Projectile.SnapshotData[] turretSnapshots;

    public WorldStateSnapshot(GameWorld world, Collection<Player> players, Collection<Projectile> projectiles, Collection<Turret> turrets) {
        worldSnapshot = world.makeSnapshot();

        playerSnapshots = new Player.SnapshotData[players.size()];
        int index = 0;
        for (Player player : players)
            playerSnapshots[index++] = player.makeSnapshot();

        projectileSnapshots = new Projectile.SnapshotData[projectiles.size()];
        index = 0;
        for (Projectile projectile : projectiles)
            projectileSnapshots[index++] = projectile.makeSnapshot();

        turretSnapshots = new Projectile.SnapshotData[turrets.size()];
        index = 0;
        for (Turret turret : turrets)
            turretSnapshots[index++] = turret.makeSnapshot();
    }

    public GameWorld.SnapshotData getWorldSnapshot() {
        return worldSnapshot;
    }

    public Player.SnapshotData[] getPlayerSnapshots() {
        return playerSnapshots;
    }

    public Projectile.SnapshotData[] getProjectileSnapshots() {
        return projectileSnapshots;
    }

    public Projectile.SnapshotData[] getTurretSnapshots() {
        return turretSnapshots;
    }
}
