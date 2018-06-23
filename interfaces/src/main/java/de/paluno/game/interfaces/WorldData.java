package de.paluno.game.interfaces;

public class WorldData extends GameData {

    public int gameState;
    public PlayerData[] players;
    public float shootingAngle;
    public ProjectileData projectile;

    public WorldData() {
        super();
    }

    public WorldData(int tick) {
        super(tick);
    }
}
