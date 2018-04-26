package de.paluno.game.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import de.paluno.game.GameState;
import de.paluno.game.screens.PlayScreen;

public class Worm implements Updatable, PhysicsObject, Renderable {
	private boolean standsOnGround;
	private PlayScreen screen;
	private int playerNumber;
	
	private Body body;
	
	public Worm(int num, PlayScreen screen) {
		this.playerNumber = num;
		this.screen = screen;
	}
	
	public void update(float delta, GameState state) {
		
	}
	
	public void render(SpriteBatch batch, float delta) {
		
	}
	
	public void setupBody() {
		this.body = new Body(this.screen.getWorld(), addr);
	}
	
	public Body getBody() {
		return this.body;
	}
	
	public void setBodyToNullReference() {
		this.body = null;
	}
	
	public void die() {
		this.setBodyToNullReference();
		if(this.playerNumber == 1) {
			this.screen.setGameState(GameState.GAMEOVERPLAYERTWOWON);
		} else {
			this.screen.setGameState(GameState.GAMEOVERPLAYERONEWON);
		}
	}
	
	public boolean canJump() {
		return this.canJump() && (
				(this.playerNumber == 1 && this.screen.getGameState() == GameState.PLAYERONETURN)
				||
				(this.playerNumber == 2 && this.screen.getGameState() == GameState.PLAYERTWOTURN));
	}
	
	public boolean isStandsOnGround() {
		return this.standsOnGround;
	}
	
	public void setStandsOnGround(boolean onGround) {
		this.standsOnGround = onGround;
	}
}
