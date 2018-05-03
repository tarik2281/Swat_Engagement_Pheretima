package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.screens.PlayScreen;

public class Worm implements Updatable, PhysicsObject, Renderable {
	private boolean standsOnGround;
	private PlayScreen screen;
	private int playerNumber;
	
	private Body body;
	private Fixture fixture;
	private Texture texture;
	
	private Vector2 spawnPosition;
	
	private GameState currentState;
	
	private int movement = 0;
	private boolean jump = false;
	
	public Worm(int num, PlayScreen screen, Vector2 position) {
		this.playerNumber = num;
		this.screen = screen;
		this.spawnPosition = position;
		
		this.setupBody();
		
		this.texture = new Texture(Gdx.files.internal("waccuse1_blank.png"));
	}
	
	public void update(float delta, GameState state) {
		if(this.body == null) return;
		
		this.currentState = state;
		
		if(this.jump && this.canJump()) {
			this.body.applyForceToCenter(0.0f, 2.0f, true);
			this.setStandsOnGround(false);
		}
		
		if(this.movement != Constants.MOVEMENT_NO_MOVEMENT) {
			Vector2 currentPos = this.body.getPosition();
			Vector2 currentVel = this.body.getLinearVelocity();
			
			if(movement == Constants.MOVEMENT_RIGHT && currentVel.x < Constants.MAX_VELOCITY) {
				if(currentPos.x > 0) this.body.applyLinearImpulse(0.5f, 0.0f, currentPos.x, currentPos.y, true);
				else this.body.setLinearVelocity(0.0f, currentVel.y);
			}
			else if(movement == Constants.MOVEMENT_LEFT && currentVel.x > -Constants.MAX_VELOCITY) {
				if(currentPos.x < 100) this.body.applyLinearImpulse(-0.5f, 0.0f, currentPos.x, currentPos.y, true);
				else this.body.setLinearVelocity(0.0f, currentVel.y);
			}
		}
	}
	
	public void render(SpriteBatch batch, float delta) {
		if(this.body == null) return;
		
		Vector2 currentPos = Constants.getScreenSpaceVector(this.body.getPosition());
		
		// TODO: Rename textures. 
		
		batch.draw(this.texture, currentPos.x, currentPos.y);
	}
	
	public void setupBody() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(this.spawnPosition.x, this.spawnPosition.y);
		// TODO: Change spawning player and world-size dependent.
		bodyDef.type = BodyType.DynamicBody;
		
		this.body = this.screen.getWorld().createBody(bodyDef);
		
		PolygonShape bodyRect = new PolygonShape();
		bodyRect.setAsBox(5 * Constants.WORLD_SCALE, 10 * Constants.WORLD_SCALE);
		// TODO: Maybe finetune hitbox
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = bodyRect;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.8f;
		fixtureDef.restitution = 0.2f;
		
		this.fixture = this.body.createFixture(fixtureDef);
		
		bodyRect.dispose();
	}
	
	public Body getBody() {
		return this.body;
	}
	
	public void setBodyToNullReference() {
		this.fixture = null;
		this.body = null;
	}
	
	public void die() {
		this.screen.getWorld().destroyBody(this.body);
		this.setBodyToNullReference();
		if(this.playerNumber == 1) {
			this.screen.setGameState(GameState.GAMEOVERPLAYERTWOWON);
		} else {
			this.screen.setGameState(GameState.GAMEOVERPLAYERONEWON);
		}
	}
	
	public boolean canJump() {
		return this.canJump() && (
				(this.playerNumber == 1 && this.currentState == GameState.PLAYERONETURN)
				||
				(this.playerNumber == 2 && this.currentState == GameState.PLAYERTWOTURN));
	}
	
	public boolean isStandsOnGround() {
		return this.standsOnGround;
	}
	
	public void setStandsOnGround(boolean onGround) {
		this.standsOnGround = onGround;
	}
	
	public void setMovement(int newMovementCode) {
		this.movement = newMovementCode;
	}
	public void setJump(boolean newJump) {
		this.jump = newJump;
	}
}
