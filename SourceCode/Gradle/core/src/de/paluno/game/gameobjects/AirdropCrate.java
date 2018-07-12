package de.paluno.game.gameobjects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.EventManager;
import de.paluno.game.UserData;

public class AirdropCrate extends WorldObject {

	public static class PickupEvent {
		private Worm worm;
		private AirdropCrate crate;

		public PickupEvent(Worm worm, AirdropCrate crate) {
			this.worm = worm;
			this.crate = crate;
		}

		public Worm getWorm() {
			return worm;
		}

		public AirdropCrate getCrate() {
			return crate;
		}
	}

	public static class SnapshotData {
		private Vector2 position;
		private float angle;
		private WeaponType drop;
		private int id;
	}

	private int id = -1;
	private boolean fromSnapshot = false;

	private Sprite design;
	private WeaponType drop;
	
	// C = Chute, D = Drop, contact = Shall things collide with crate?
	private boolean dropping = true;
	private boolean fading = false;
	private float opacity = 1.0f;
	private boolean contact = true;
	private boolean removed = false;
	
	private AirdropChute chute = null;
	
	final float TORAD = (float)(Math.PI / 180);
	final float TODEG = (float)(180 / Math.PI);
	
	/**
	 * Constructor
	 * @param spawn - Spawn coordinate
	 * @param drop - Drop content
	 */
	public AirdropCrate(Vector2 spawn, WeaponType drop) {
		setPosition(spawn);
		this.drop = drop;
		
		System.out.println("New drop initialized!");
		//world.getCamera().setCameraFocus(this);
	}

	public AirdropCrate(SnapshotData data) {
		setPosition(data.position);
		setAngle(data.angle);
		setId(data.id);

		this.drop = data.drop;

		fromSnapshot = true;
		dropping = false;
	}

	@Override
	public void setupAssets(AssetManager manager) {
		design = new Sprite(manager.get(Assets.crate));
		design.setOriginCenter();
	}

	/**
	 * Handler method for Game Loop's render phase
	 * @param batch - The Sprite batch to draw in
	 * @param delta - Time since last round in seconds
	 */
	@Override
	public void render(SpriteBatch batch, float delta) {

		// The crate itself still exists (if not, we got a problem), so render it
		Vector2 cratePosition = Constants.getScreenSpaceVector(getPosition());
		if (fading) {
			// The crate has been picked up and is slowly fading - calculate
			if (opacity != 0) {
				// Still fading, calculate how much we lose this round (duration: 1s)
				opacity -= 2 * delta;
				if (opacity < 0) opacity = 0;
				design.setAlpha(opacity);
			}
		} else design.setRotation(getAngle() * TODEG);
		//batch.draw(design, cratePosition.x, cratePosition.y);
		design.setOriginBasedPosition(cratePosition.x, cratePosition.y);
		design.draw(batch);
	}

	/**
	 * Handler method for Game Loop's update phase
	 * @param delta - Time since last update in seconds
	 */
	@Override
	public void update(float delta) {
		// No body anymore? Shouldn't happen, catch

		// Determine done fading outs
		if(opacity == 0) removeCrate();
		
		// Crate finally landed, make it static so it doesn't move and doesn't need much render capacity
		/*if(body.getType() == BodyType.DynamicBody && body.getLinearVelocity() == new Vector2(0,0)) {
			body.setType(BodyType.StaticBody);
		}*/
		
		if (getWorld().isUnderWater(this)) {
			if(chute != null) removeChute();
			removeCrate();
		}
	}
	
	/**
	 * Method to create the body for our pickup crate
	 */
	@Override
	protected Body onSetupBody(World world) {
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.gravityScale = 0.0f;
		def.fixedRotation = false;
		def.position.set(getPosition());
		def.linearVelocity.set(0, -1.5f);

		if (fromSnapshot) {
			def.gravityScale = 1.0f;
			def.angle = getAngle();
			def.linearVelocity.set(0, 0);
		}
		
		Body body = world.createBody(def);
		
		PolygonShape bodyRect = new PolygonShape();
		bodyRect.setAsBox(Constants.CRATE_RADIUS, Constants.CRATE_RADIUS);
		
		FixtureDef fDef = new FixtureDef();
		fDef.shape = bodyRect;
		fDef.density = 0.5f;
		fDef.friction = 1.0f;
		fDef.restitution = 0.1f;
		
		Fixture fix = body.createFixture(fDef);
		fix.setUserData(new UserData(UserData.ObjectType.Crate, this));
		
		bodyRect.dispose();

		return body;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public WeaponType getDrop() {
		return drop;
	}

	/**
	 * Method to start fading out the crate
	 */
	private void destroy() {
		this.contact = false;
		if(this.chute != null && !this.fading) destroyChute();
		this.fading = true;
	}
	/**
	 * Method to start fading out the chute
	 */
	private void destroyChute() {
		// Destroy the joint, the chute is going and the crate probably not
		if(chute == null || this.chute.isFading()) return;
		this.chute.destroy();
		System.out.println("Chute destruction triggered, chaging crate specs.");
		// Update the crate body, to make it affected by gravity
		getBody().setGravityScale(1.0f);
		getBody().resetMassData();
		getBody().setLinearVelocity(0.0f, 0.0f);
		System.out.println("Done!");
	}
	
	/**
	 * Method to finally completely remove the crate's body
	 */
	public void removeCrate() {
		if(!removed) {
			removed = true;
			System.out.println("World should have forgotten this crate");
			EventManager.getInstance().queueEvent(EventManager.Type.RemoveCrate, this);
			//removeFromWorld();
			//world.forgetAfterUpdate(this);
		} else System.out.println("Waiting for chute to despawn...");
	}
	/**
	 * Method to finally completely remove the chute's body
	 */
	protected void removeChute() {
		if (chute != null) {
			chute.remove();
			chute = null;
		}
	}

	/**
	 * Method to trigger the "Crate pickup" event
	 * @return The weapon contained in this drop
	 */
	public void pickup(Worm worm) {
		if(fading) return;
		EventManager.getInstance().queueEvent(EventManager.Type.CratePickup, new PickupEvent(worm, this));
		destroy();
	}
	/**
	 * Method to trigger the "Crate landed" event
	 */
	public void land() {
		if(!dropping) return;
		this.dropping = false;
		System.out.println("Throwing off chute");
		destroyChute();
		System.out.println("Chute thrown off!");
		EventManager.getInstance().queueEvent(EventManager.Type.CrateLanded, null);
		//this.world.setCrateLanded();
	}
	
	public void setChute(AirdropChute chute) {this.chute = chute;}
	
	/**
	 * Getter method to check if this crate shall react to collisions with Worms
	 * @return Handle contact?
	 */
	public boolean getContact() {return this.contact;}

	public SnapshotData makeSnapshot() {
		SnapshotData data = new SnapshotData();
		data.position = new Vector2(getPosition());
		data.angle = getAngle();
		data.drop = drop;
		data.id = id;
		return data;
	}
}
