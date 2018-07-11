package de.paluno.game.interfaces;

public class ProjectileData {
    private int id;
    private int type; // according to WeaponType enum
    private PhysicsData physicsData;
    private int playerNumber;
    private int wormNumber;

    public int getType() {
        return type;
    }

    public PhysicsData getPhysicsData() {
        return physicsData;
    }

    public ProjectileData setType(int type) {
        this.type = type;
        return this;
    }

    public ProjectileData setPhysicsData(PhysicsData data) {
        this.physicsData = data;
        return this;
    }

    public int getId() {
        return id;
    }

    public ProjectileData setId(int id) {
        this.id = id;
        return this;
    }

    public ProjectileData setPlayerNumber(int number) {
        this.playerNumber = number;
        return this;
    }

    public ProjectileData setWormNumber(int number) {
        this.wormNumber = number;
        return this;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getWormNumber() {
        return wormNumber;
    }
}
