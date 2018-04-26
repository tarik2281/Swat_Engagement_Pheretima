package de.paluno.game.gameobjects;

public interface PhysicsObject {
	
public void	setBodyToNullReference() ;

public void setupBody() ;

com.badlogic.gdx.physics.box2d.Body getBody();

}
