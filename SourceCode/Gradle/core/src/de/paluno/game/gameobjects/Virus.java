package de.paluno.game.gameobjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

import de.paluno.game.Constants;
import de.paluno.game.UserData;

public class Virus implements PhysicsObject{

	private Body body;
	private Worm worm;
	private World world;
	
	public Virus(Worm worm, World world) {
		this.worm = worm;
		this.world = world;
	}

	@Override
	public void setBodyToNullReference() {
		this.body = null;
	}

	@Override
	public void setupBody() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(worm.getBody().getPosition());
		bodyDef.type = BodyType.DynamicBody;
		
		this.body = world.createBody(bodyDef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(10 * Constants.WORLD_SCALE);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.isSensor = true;
		
		Fixture fix = this.body.createFixture(fixtureDef);
		fix.setUserData(new UserData(UserData.ObjectType.Virus,this));
		
		DistanceJointDef jointDef = new DistanceJointDef();
		jointDef.bodyA = worm.getBody();
		jointDef.bodyB = this.body;
		jointDef.localAnchorA.set(worm.getBody().getLocalCenter());
		jointDef.localAnchorB.set(this.body.getLocalCenter());
		
		world.createJoint(jointDef);
		
		circle.dispose();	
	}

	@Override
	public Body getBody() {
		return body;
	}
}
