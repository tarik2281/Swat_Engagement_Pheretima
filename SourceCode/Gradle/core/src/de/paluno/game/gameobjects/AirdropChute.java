package de.paluno.game.gameobjects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import de.paluno.game.*;

public class AirdropChute extends WorldObject {

	private int id = -1;
	private AirdropCrate crate;
	private RevoluteJoint joint;
	
	private Sprite design;
	
	private boolean fading = false;
	private float opacity = 1.0f;
	private boolean removed = false;
	
	final float TORAD = (float)(Math.PI / 180.0f);
	final float TODEG = (float)(180.0f / Math.PI);
	
	public AirdropChute(AirdropCrate crate) {
		this.crate = crate;
	}

	@Override
	public void setupAssets(AssetManager manager) {
		design = new Sprite(manager.get(Assets.chute));
		design.setOriginCenter();
	}

	@Override
	protected Body onSetupBody(World world) {
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.fixedRotation = false;
		def.gravityScale = 0;
		Vector2 bodyPos = this.crate.getBody().getPosition();
		def.position.set(bodyPos.x, bodyPos.y + 20*Constants.WORLD_SCALE);
		def.linearVelocity.set(0, -1.5f);

		Body body = world.createBody(def);
		
		PolygonShape bodyRect = new PolygonShape();
		bodyRect.setAsBox(Constants.CRATE_RADIUS, Constants.CRATE_RADIUS);
		
		FixtureDef fDef = new FixtureDef();
		fDef.shape = bodyRect;
		fDef.density = 1.0f;
		fDef.friction = 1.0f;
		fDef.restitution = 0.0f;
		
		Fixture fix = body.createFixture(fDef);
		body.applyTorque(0.01f, true);
		fix.setUserData(new UserData(UserData.ObjectType.Chute, this));
		
		// Join them together, so they'll stay near, while still being able to wiggle
		RevoluteJointDef jointDef = new RevoluteJointDef();
		//jointDef.initialize(this.crate.getBody(), this.body, new Vector2(0,0));
		jointDef.bodyA = body;
		jointDef.bodyB = this.crate.getBody();
		jointDef.collideConnected = false;
		Vector2 thisPos = body.getLocalCenter();
		jointDef.localAnchorA.set(thisPos.x, thisPos.y - 25*Constants.WORLD_SCALE);
		jointDef.localAnchorB.set(this.crate.getBody().getLocalCenter());
		
		jointDef.enableLimit = true;
		jointDef.lowerAngle = -22 * TORAD;
		jointDef.upperAngle = 22 * TORAD;
		jointDef.referenceAngle = 0;
		
		jointDef.enableMotor = true;
		jointDef.maxMotorTorque = 1000.0f;
		jointDef.motorSpeed = 22 * TORAD;
		
		this.joint = (RevoluteJoint) world.createJoint(jointDef);
		//this.crate.getBody().applyTorque(5.0f, true);
		
		bodyRect.dispose();

		return body;
	}

	@Override
	public void update(float delta) {
		if(opacity == 0) crate.removeChute();
		if(joint != null) {
			if(this.crate.getBody().getAngle() >= 22 * TORAD) {
				this.joint.setMotorSpeed(-22 * TORAD);
			} else if(this.crate.getBody().getAngle() <= -22 * TORAD) {
				this.joint.setMotorSpeed(22 * TORAD);
			}
		}
		if (getAngle() >= 20 * TORAD) getBody().applyTorque(-0.01f, true);
		else if (getAngle() <= -20 * TORAD) getBody().applyTorque(0.01f, true);
	}

	@Override
	public void render(SpriteBatch batch, float delta) {
			// this crate still has a chute, so render it
			Vector2 chutePosition = Constants.getScreenSpaceVector(getPosition());
			if(fading) {
				// Chute has been dropped off and is slowly fading - calculate
				if(opacity != 0) {
					// Still fading, calculate how much we lose this round (duration: 1s)
					opacity -= 1*delta;
					if(opacity < 0) opacity = 0;
					design.setAlpha(opacity);
				}
			} else design.setRotation(getAngle() * TODEG);
			//batch.draw(design, chutePosition.x, chutePosition.y);
			design.setOriginBasedPosition(chutePosition.x, chutePosition.y);
			design.draw(batch);
	}

	public Joint getJoint() {
		return joint;
	}

	public void setJointToNull() {
		joint = null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void destroy() {
		//crate.getWorld().destroyJoint((Joint) joint);
		EventManager.getInstance().queueEvent(EventManager.Type.DestroyChute, this);
		//crate.getWorld().registerDestroyJoint(joint);
		System.out.println("Joint destroyed");
		//this.joint = null;
		this.fading = true;
	}
	
	public void remove() {
		if (!removed) {
			removed = true;
			EventManager.getInstance().queueEvent(EventManager.Type.RemoveChute, this);
		}
		//removeFromWorld();
		//crate.getWorld().forgetAfterUpdate(this);
	}
	
	public boolean isFading() {return this.fading;}

}
