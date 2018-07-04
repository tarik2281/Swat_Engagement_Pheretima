package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.UserData;
import de.paluno.game.screens.PlayScreen;

public class Turret extends WorldObject {

	public static class SnapshotData {

		private Vector2 position;
		private Vector2 direction;
		private WeaponType weaponType;

		private int playerNumber;
		private int wormNumber;

		public Vector2 getPosition() {
			return position;
		}
	}
	
	private GameState currentGameState = GameState.NONE;
	private	ShotDirectionAction shotdirectionsaction;
	private PlayScreen screen;
	private Body body;
	private Vector2 position;
	private GameWorld world;
	private Player player;
	private float timetoshoot=3;
	private boolean shoot=false;
    private Texture texture;
    private Sprite sprite;
    private int numworm;
    private Worm currentWorm;
    
	
	public Turret(Worm worm ,GameWorld world, Vector2 position) {
		this.position = position;
		this.world= world;
		this.currentWorm= worm;
		
		
		shotdirectionsaction = new ShotDirectionAction();
		sprite = new Sprite(texture);
		sprite.setOriginCenter();
		
	}
	
	@Override
	public void setupAssets(AssetManager manager) {
		texture = manager.get(Assets.weaponTurret);
		super.setupAssets(manager);
	}

	@Override
	protected Body onSetupBody(World world) {
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(currentWorm.getPosition().x, currentWorm.getPosition().y);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(30 * Constants.WORLD_SCALE, 30 * Constants.WORLD_SCALE);
      
        Body body = world.createBody(bodyDef);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = Constants.TURRET_DENSITY;
        fixtureDef.friction = 1.0f;
        fixtureDef.restitution = 0.0f;
        
        Fixture fix = body.createFixture(fixtureDef);
        fix.setUserData(new UserData(UserData.ObjectType.turret, this));
        
        shape.dispose();

        return body;
	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		super.update(delta);
	}

	@Override
	public void render(SpriteBatch batch, float delta) {
		// TODO Auto-generated method stub
		 Vector2 position = Constants.getScreenSpaceVector(getPosition());

	        sprite.setOriginBasedPosition(position.x, position.y);
	        sprite.draw(batch);
	}
	
	//public void  automaticshoot(Worm worm) {
		 
	//	if(player.getCurrentWorm() != null&& shoot)
		//	player.getCurrentWorm().shoot(shotdirectionsaction.getAngle(worm));
		
		
		// We're shooting. That means, someone could die.
		// That means, we want to see him suffer again and again in slow motion.
		// That means: Capture that shit!
		//world.makeSnapshot();
		//setshoot(false);
	//}
	public void setshotdirectionaction(ShotDirectionAction s) {
		this.shotdirectionsaction=s;
	}
	public ShotDirectionAction getSchotDirectionaction() {
		return shotdirectionsaction;
	}
  
	  public boolean getShoot() {return shoot;}
	  public void setshoot(boolean s) {this.shoot=s;}
	  
	  
	/* public void AutomaticShoot() {
		 if (shoot && shouldAcceptInput() && currentGameState == GameState.PLAYERTURN) {
	            Player player = getCurrentPlayer();
	            Worm worm = player.getCurrentWorm();
	            weaponProjectileCache.clear();
	            player.getCurrentWeapon().shoot(worm, currentWeaponIndicator, weaponProjectileCache);
	            weaponProjectileCache.forEach(this::addProjectile);
	            onShoot(weaponProjectileCache);
	 }
		*/
	 
	  public SnapshotData makeSnapshot() {
			SnapshotData data = new SnapshotData();
			return data;
		}
}

