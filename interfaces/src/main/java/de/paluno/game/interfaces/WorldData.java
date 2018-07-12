package de.paluno.game.interfaces;

public class WorldData extends GameData {

    private boolean usingTCP;
    private PlayerData[] players;
    private Object indicatorData;
    private ProjectileData[] projectiles;
    private CrateData[] crates;
    private CrateData[] chutes;
    private int currentWeapon = -1;

    public WorldData() {
        super();
    }

    public WorldData(int tick, boolean usingTCP) {
        super(tick);

        this.usingTCP = usingTCP;
    }

    public boolean isUsingTCP() {
        return usingTCP;
    }

    public PlayerData getPlayer(int number) {
        return players[number];
    }

    public PlayerData[] getPlayers() {
        return players;
    }

    public WorldData setPlayers(PlayerData[] players) {
        this.players = players;
        return this;
    }

    public ProjectileData[] getProjectiles() {
        return projectiles;
    }

    public ProjectileData getProjectileById(int id) {
        if (projectiles != null)
            for (ProjectileData projectileData : projectiles)
                if (projectileData.getId() == id)
                    return projectileData;

        return null;
    }

    public WorldData setProjectiles(ProjectileData[] projectiles) {
        this.projectiles = projectiles;
        return this;
    }

    public CrateData[] getCrates() {
        return crates;
    }

    public CrateData getCrateById(int id) {
        if (crates != null)
            for (CrateData crateData : crates)
                if (crateData.getId() == id)
                    return crateData;

        return null;
    }

    public WorldData setChutes(CrateData[] chutes) {
        this.chutes = chutes;
        return this;
    }

    public CrateData[] getChutes() {
        return chutes;
    }

    public CrateData getChuteById(int id) {
        if (chutes != null)
            for (CrateData crateData : chutes)
                if (crateData.getId() == id)
                    return crateData;

        return null;
    }

    public WorldData setCrates(CrateData[] crates) {
        this.crates = crates;
        return this;
    }

    public int getCurrentWeapon() {
        return currentWeapon;
    }

    public WorldData setCurrentWeapon(int currentWeapon) {
        this.currentWeapon = currentWeapon;
        return this;
    }

    public Object getIndicatorData() {
        return indicatorData;
    }

    public WorldData setIndicatorData(Object indicatorData) {
        this.indicatorData = indicatorData;
        return this;
    }
}
