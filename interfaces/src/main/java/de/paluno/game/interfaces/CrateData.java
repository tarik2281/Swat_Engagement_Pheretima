package de.paluno.game.interfaces;

public class CrateData {

    private int id;
    private PhysicsData physicsData;

    public CrateData setId(int id) {
        this.id = id;
        return this;
    }

    public CrateData setPhysicsData(PhysicsData physicsData) {
        this.physicsData = physicsData;
        return this;
    }

    public int getId() {
        return id;
    }

    public PhysicsData getPhysicsData() {
        return physicsData;
    }
}
