package de.paluno.game.interfaces;

public class ProjectileData {
    private int type; // according to WeaponType enum
    private PhysicsData physicsData;

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
}
