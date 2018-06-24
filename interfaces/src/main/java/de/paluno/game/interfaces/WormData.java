package de.paluno.game.interfaces;

public class WormData {

    public int playerNumber;
    public int wormNumber;

    private PhysicsData physicsData;

    private int movement;
    private int orientation;
    public int numGroundContacts;


    public PhysicsData getPhysicsData() {
        return physicsData;
    }

    public int getMovement() {
        return movement;
    }

    public int getOrientation() {
        return orientation;
    }


    public WormData setPhysicsData(PhysicsData data) {
        this.physicsData = data;
        return this;
    }

    public WormData setMovement(int movement) {
        this.movement = movement;
        return this;
    }

    public WormData setOrientation(int orientation) {
        this.orientation = orientation;
        return this;
    }
}
