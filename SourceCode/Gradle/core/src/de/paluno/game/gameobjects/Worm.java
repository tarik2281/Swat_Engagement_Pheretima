package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.paluno.game.AnimatedSprite;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.screens.PlayScreen;

public class Worm implements Updatable, PhysicsObject, Renderable {
	private boolean standsOnGround;
	private PlayScreen screen;
	private int playerNumber;
	
	private Body body;
	//private Fixture fixture;
	//private Texture texture;
	
	private Vector2 spawnPosition;
	
	private GameState currentState;
	private AnimatedSprite idleAnimation;
	private AnimatedSprite walkAnimation;
	
	private int movement = 0;
	private boolean jump = false;

	public Worm(int num, PlayScreen screen, Vector2 position) {
		this.playerNumber = num;
		this.screen = screen;
		this.spawnPosition = position;
		
		//this.setupBody();
		
		//this.texture = new Texture(Gdx.files.internal("waccuse1_blank.png"));
		idleAnimation = new AnimatedSprite(Gdx.files.internal("wbrth1.xml"));
		walkAnimation = new AnimatedSprite(Gdx.files.internal("wwalk.xml"));
		
		
		
	}
	
	public void update(float delta, GameState state) {
		if(this.body == null) return;
		
		this.currentState = state;

        Vector2 currentPos = body.getWorldCenter();

		if(this.jump && this.canJump()) {
		    // TODO: maybe jump and landing animations
			this.body.applyLinearImpulse(0.0f, body.getMass() * 4.0f, currentPos.x, currentPos.y, true);
			jump = false;
		}

		// http://www.iforce2d.net/b2dtut/constant-speed
		Vector2 currentVel = body.getLinearVelocity();
		float desiredVel = 0;

		switch (movement) {
            case Constants.MOVEMENT_LEFT:
                desiredVel = -Constants.MOVE_VELOCITY;
                break;
            case Constants.MOVEMENT_NO_MOVEMENT:
                desiredVel = 0;
                break;
            case Constants.MOVEMENT_RIGHT:
                desiredVel = Constants.MOVE_VELOCITY;
                break;
        }

        float velChange = desiredVel - currentVel.x;
		float impulse = body.getMass() * velChange;
		body.applyLinearImpulse(impulse, 0, currentPos.x, currentPos.y, true);

		/*if(this.movement != Constants.MOVEMENT_NO_MOVEMENT) {
			Vector2 currentPos = this.body.getPosition();
			Vector2 currentVel = this.body.getLinearVelocity();

			float impulse = body.getMass() * 10.0f * Constants.WORLD_SCALE;
			
			if(movement == Constants.MOVEMENT_RIGHT && currentVel.x < Constants.MOVE_VELOCITY) {
				if(currentPos.x > 0) this.body.applyLinearImpulse(impulse, 0.0f, currentPos.x, currentPos.y, true);
				else this.body.setLinearVelocity(0.0f, currentVel.y);
			}
			else if(movement == Constants.MOVEMENT_LEFT && currentVel.x > -Constants.MOVE_VELOCITY) {
				if(currentPos.x < 100) this.body.applyLinearImpulse(-impulse, 0.0f, currentPos.x, currentPos.y, true);
				else this.body.setLinearVelocity(0.0f, currentVel.y);
			}
		}*/
	}
	
	public void render(SpriteBatch batch, float delta) {
		if(this.body == null) return;
		
		Vector2 currentPos = Constants.getScreenSpaceVector(this.body.getPosition());
		
		// TODO: Rename textures. 

        AnimatedSprite sprite = null;

        if (movement == Constants.MOVEMENT_NO_MOVEMENT) {
            sprite = idleAnimation;
        }
        else {
            sprite = walkAnimation;
            // walk animation is only for moving left
            walkAnimation.setFlipX(movement == Constants.MOVEMENT_RIGHT);
        }

        sprite.setPosition(currentPos);
        sprite.draw(batch, delta);
		//batch.draw(this.texture, currentPos.x, currentPos.y);
	}
	
	public void setupBody() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(this.spawnPosition.x, this.spawnPosition.y);
		// TODO: Change spawning player and world-size dependent.
		bodyDef.type = BodyType.DynamicBody;
		
		this.body = this.screen.getWorld().createBody(bodyDef);
		body.setFixedRotation(true);
		
		PolygonShape bodyRect = new PolygonShape();
		// TODO: hardcoded worm hitbox size
		bodyRect.setAsBox(18.0f / 2 * Constants.WORLD_SCALE, 25.0f / 2.0f * Constants.WORLD_SCALE);
		// TODO: Maybe finetune hitbox
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = bodyRect;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.8f;
		fixtureDef.restitution = 0.0f;
		
		Fixture fix = this.body.createFixture(fixtureDef);
		// CollisionHandler Identifier
		fix.setUserData("Worm");
		
		bodyRect.dispose();
		
	}
	
	public Body getBody() {
		return this.body;
	}
	
	public void setBodyToNullReference() {
		//this.fixture = null;
		this.body = null;
	}
	
	public void die() {
	    // TODO: proper object removing through PlayScreen
		this.screen.getWorld().destroyBody(this.body);
		this.setBodyToNullReference();
		if(this.playerNumber == 1) {
			this.screen.setGameState(GameState.GAMEOVERPLAYERTWOWON);
		} else {
			this.screen.setGameState(GameState.GAMEOVERPLAYERONEWON);
		}
	}
	
	public boolean canJump() {
		return this.isStandsOnGround() && (
				(this.playerNumber == Constants.PLAYER_NUMBER_1 && this.currentState == GameState.PLAYERONETURN)
				||
				(this.playerNumber == Constants.PLAYER_NUMBER_2 && this.currentState == GameState.PLAYERTWOTURN));
	}
	
	public boolean isStandsOnGround() {
		return this.standsOnGround;
	}
	
	public void setStandsOnGround(boolean onGround) {
		this.standsOnGround = onGround;
	}
	
	public void setMovement(int newMovementCode) {
		this.movement = newMovementCode;

		// reset all animations since they will change
		idleAnimation.reset();
		walkAnimation.reset();

		if (Constants.MOVEMENT_NO_MOVEMENT != movement) {
		    idleAnimation.setFlipX(movement == Constants.MOVEMENT_RIGHT);
		    walkAnimation.setFlipX(movement == Constants.MOVEMENT_RIGHT);
        }
	}

	public void setJump(boolean newJump) {
		this.jump = newJump;
	}
}
