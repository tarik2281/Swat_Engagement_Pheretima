package de.paluno.game.gameobjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public interface PhysicsObject {
    void setBodyToNullReference();
    void setupBody();
    Body getBody();
}
