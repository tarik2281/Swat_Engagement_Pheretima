package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.paluno.game.GameState;
import de.paluno.game.screens.PlayScreen;

public class Worm implements Updatable, PhysicsObject, Renderable {
	private boolean standsOnGround;
	private PlayScreen screen;
	private int playerNumber;
	
	private Body body;
	private Fixture fixture;
	
	private GameState currentState;
	
	public Worm(int num, PlayScreen screen) {
		this.playerNumber = num;
		this.screen = screen;
		
		this.setupBody();
	}
	
	public void update(float delta, GameState state) {
		this.update(delta, state, 0, false);
	}
	
	public void update(float delta, GameState state, int movement, boolean jump) {
		if(this.body == null) return;
		
		this.currentState = state;
		
		if(jump && this.canJump()) {
			this.body.applyForceToCenter(0.0f, 2.0f, true);
			this.setStandsOnGround(false);
		}
		
		if(movement != 0) {
			Vector2 currentPos = this.body.getPosition();
			Vector2 currentVel = this.body.getLinearVelocity();
			
			if(movement == 1 && currentVel.x < 20) {
				if(currentPos.x > 0) this.body.applyLinearImpulse(0.5f, 0.0f, currentPos.x, currentPos.y, true);
				else this.body.setLinearVelocity(0.0f, currentVel.y);
			}
			else if(movement == -1 && currentVel.x > -20) {
				if(currentPos.x < 100) this.body.applyLinearImpulse(-0.5f, 0.0f, currentPos.x, currentPos.y, true);
				else this.body.setLinearVelocity(0.0f, currentVel.y);
			}
		}
	}
	
	public void render(SpriteBatch batch, float delta) {
		if(this.body == null) return;
		
		Texture texture = new Texture(Gdx.files.internal("waccuse1_blank.png"));
		// TODO: Rename textures. 
		
		batch.begin();
		batch.draw(texture, 10, 10);
		batch.end();
	}
	
	public void setupBody() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(10, 50);
		// TODO: Change spawning player and world-size dependent.
		bodyDef.type = BodyType.DynamicBody;
		
		this.body = this.screen.getWorld().createBody(bodyDef);
		
		PolygonShape bodyRect = new PolygonShape();
		bodyRect.setAsBox(5, 10);
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
}
