package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	
	public Worm(int num, PlayScreen screen) {
		this.playerNumber = num;
		this.screen = screen;
		
		this.setupBody();
	}
	
	public void update(float delta, GameState state) {
		if(this.body == null) return;
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
		bodyDef.type = BodyType.DynamicBody;
		
		this.body = this.screen.getWorld().createBody(bodyDef);
		
		PolygonShape bodyRect = new PolygonShape();
		bodyRect.setAsBox(5, 10);
		
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
