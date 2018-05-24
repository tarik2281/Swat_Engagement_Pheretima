package de.paluno.game.gameobjects;

import com.badlogic.gdx.physics.box2d.Body;

public interface PhysicsObject {
    void setBodyToNullReference();
    void setupBody();
    Body getBody();
}
