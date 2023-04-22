package de.paluno.game;

import com.badlogic.gdx.physics.box2d.Fixture;

// Holds the information of the fixture on which object it is assigned to
public class UserData {
    public enum ObjectType {
        Worm, Ground, WormFoot, Projectile, Virus, Turret, Headshot, Crate, Chute, Teleport
    }

    private ObjectType type;
    private Object userData;

    public UserData(ObjectType type, Object userData) {
        this.type = type;
        this.userData = userData;
    }

    public ObjectType getType() {
        return type;
    }

    public Object getUserData() {
        return userData;
    }

    public static ObjectType getType(Fixture fixture) {
        UserData data = (UserData) fixture.getUserData();
        return data.getType();
    }

    public static <T> T getObject(Fixture fixture) {
        UserData data = (UserData)fixture.getUserData();
        return (T)data.getUserData();
    }
}
