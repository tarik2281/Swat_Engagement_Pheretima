package de.paluno.game.worldhandlers;

import de.paluno.game.gameobjects.*;

import java.util.Collection;

public class WorldStateSnapshot {
    private GameWorld.SnapshotData worldSnapshot;
    private Player.SnapshotData[] playerSnapshots;
    private Projectile.SnapshotData[] projectileSnapshots;
    private Projectile.SnapshotData[] turretSnapshots;
    private AirdropCrate.SnapshotData[] crateSnapshots;

    public WorldStateSnapshot(GameWorld world, Collection<Player> players, Collection<Projectile> projectiles,
                              Collection<Turret> turrets, Collection<AirdropCrate> crates) {
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

        crateSnapshots = new AirdropCrate.SnapshotData[crates.size()];
        index = 0;
        for (AirdropCrate crate : crates)
            crateSnapshots[index++] = crate.makeSnapshot();
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

    public AirdropCrate.SnapshotData[] getCrateSnapshots() {
        return crateSnapshots;
    }
}
