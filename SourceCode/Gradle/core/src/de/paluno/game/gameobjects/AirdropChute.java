package de.paluno.game.gameobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.UserData;

public class AirdropChute implements Renderable, Updatable, PhysicsObject {

	private AirdropCrate crate;
	private Body body;
	private Fixture fix;
	private RevoluteJoint joint;
	
	private Sprite design;
	
	private boolean fading = false;
	private float opacity = 1.0f;
	
	final float TORAD = (float)(Math.PI / 180.0f);
	final float TODEG = (float)(180.0f / Math.PI);
	
	public AirdropChute(AirdropCrate crate) {
		this.crate = crate;
		
		Texture texture = crate.getAssets().get(Assets.chute);
		this.design = new Sprite(texture);
		design.setOriginCenter();
	}

	@Override
	public void setBodyToNullReference() {
		this.body = null;
	}

	@Override
	public void setupBody() {
		BodyDef def = new BodyDef();
		def.type = BodyType.KinematicBody;
		def.fixedRotation = false;
		Vector2 bodyPos = this.crate.getBody().getPosition();
		def.position.set(bodyPos.x, bodyPos.y + 20*Constants.WORLD_SCALE);
		def.linearVelocity.set(0, -1.5f);
		
		this.body = crate.getWorld().createBody(def);
		
		PolygonShape bodyRect = new PolygonShape();
		bodyRect.setAsBox(Constants.CRATE_RADIUS, Constants.CRATE_RADIUS);
		
		FixtureDef fDef = new FixtureDef();
		fDef.shape = bodyRect;
		fDef.density = 0.0f;
		fDef.friction = 0.0f;
		fDef.restitution = 0.0f;
		
		this.fix = body.createFixture(fDef);
		fix.setUserData(new UserData(UserData.ObjectType.Chute, this));
		
		// Join them together, so they'll stay near, while still being able to wiggle
		RevoluteJointDef jointDef = new RevoluteJointDef();
		//jointDef.initialize(this.crate.getBody(), this.body, new Vector2(0,0));
		jointDef.bodyA = this.body;
		jointDef.bodyB = this.crate.getBody();
		jointDef.collideConnected = false;
		Vector2 thisPos = this.body.getLocalCenter();
		jointDef.localAnchorA.set(thisPos.x, thisPos.y - 25*Constants.WORLD_SCALE);
		jointDef.localAnchorB.set(this.crate.getBody().getLocalCenter());
		
		jointDef.enableLimit = true;
		jointDef.lowerAngle = -22 * TORAD;
		jointDef.upperAngle = 22 * TORAD;
		jointDef.referenceAngle = 0;
		
		jointDef.enableMotor = true;
		jointDef.maxMotorTorque = 1000.0f;
		jointDef.motorSpeed = 22 * TORAD;
		
		this.joint = (RevoluteJoint) this.crate.getWorld().createJoint(jointDef);
		//this.crate.getBody().applyTorque(5.0f, true);
		
		bodyRect.dispose();
	}

	@Override
	public Body getBody() {
		return this.body;
	}

	@Override
	public void update(float delta, GameState gamestate) {
		if(opacity == 0) crate.removeChute();
		else if(this.crate.getBody().getAngle() >= 22 * TORAD) {
			this.joint.setMotorSpeed(-22 * TORAD);
		} else if(this.crate.getBody().getAngle() <= -22 * TORAD) {
			this.joint.setMotorSpeed(22 * TORAD);
		}
	}

	@Override
	public void render(SpriteBatch batch, float delta) {
		if(body != null) {
			// this crate still has a chute, so render it
			Vector2 chutePosition = Constants.getScreenSpaceVector(this.body.getPosition());
			if(fading) {
				// Chute has been dropped off and is slowly fading - calculate
				if(opacity != 0) {
					// Still fading, calculate how much we lose this round (duration: 1s)
					opacity -= 1*delta;
					if(opacity < 0) opacity = 0;
					design.setAlpha(opacity);
				}
			} else design.setRotation(body.getAngle() * TODEG);
			//batch.draw(design, chutePosition.x, chutePosition.y);
			design.setOriginBasedPosition(chutePosition.x, chutePosition.y);
			design.draw(batch);
		}
	}
	
	protected void destroy() {
		//crate.getWorld().destroyJoint((Joint) joint);
		crate.getWorld().registerDestroyJoint(joint);
		System.out.println("Joint destroyed");
		this.joint = null;
		this.fading = true;
	}
	
	protected void remove() {
		crate.getWorld().forgetAfterUpdate(this);
	}
	
	public boolean isFading() {return this.fading;}

}
