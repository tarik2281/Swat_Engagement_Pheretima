package de.paluno.game.interfaces;

public class WormData {

    private String name;
    private int playerNumber;
    private int wormNumber;

    private PhysicsData physicsData;

    private int movement;
    private int orientation;
    private int numGroundContacts;

    public WormData() {

    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getWormNumber() {
        return wormNumber;
    }

    public PhysicsData getPhysicsData() {
        return physicsData;
    }

    public int getMovement() {
        return movement;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getNumGroundContacts() {
        return numGroundContacts;
    }

    public WormData setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
        return this;
    }

    public WormData setWormNumber(int wormNumber) {
        this.wormNumber = wormNumber;
        return this;
    }

    public WormData setPhysicsData(PhysicsData data) {
        this.physicsData = data;
        return this;
    }

    public WormData setMovement(int movement) {
        this.movement = movement;
        return this;
    }

    public WormData setNumGroundContacts(int numGroundContacts) {
        this.numGroundContacts = numGroundContacts;
        return this;
    }

    public WormData setOrientation(int orientation) {
        this.orientation = orientation;
        return this;
    }

    public String getName() {
        return name;
    }

    public WormData setName(String name) {
        this.name = name;
        return this;
    }
}
