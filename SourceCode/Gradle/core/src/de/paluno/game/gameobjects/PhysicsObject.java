package de.paluno.game.gameobjects;

import com.badlogic.gdx.physics.box2d.Body;

public interface PhysicsObject {

	void setupBody();

	void setBodyToNullReference();

	Body getBody();

}
