package de.paluno.game.gameobjects;

import java.util.ArrayList;
import java.util.List;

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
import com.badlogic.gdx.physics.box2d.World;

import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.UserData;
import de.paluno.game.screens.PlayScreen;

public class Turret extends Projectile {

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
	private static float  PROJECTILE_RADIUS = 0.3f;
	private static float radiusturret= 1007.5f;
	private static float impulseturret = 0.0f;
	
	private GameState currentGameState = GameState.NONE;

	private PlayScreen screen;
	private Body body;
	private GameWorld world;
	private Player player;
	private Projectile projectile;

    private Texture texture;
    private Sprite sprite;
    private int numworm;
  
    private float degrees;
    private Explosion explosionsimulate;
    
	
	public Turret(Worm shootingWorm, WeaponType weaponType, Vector2 position, Vector2 direction) {
		super(shootingWorm,weaponType,position,direction);
		
	}

	public Turret(Projectile.SnapshotData data) {
		super(data);
	}

	@Override
	public void setupAssets(AssetManager manager) {
		texture = manager.get(Assets.weaponTurret);
		super.setupAssets(manager);
	}

	@Override
	public Body onSetupBody(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(getPosition());
		bodyDef.bullet = true;

		CircleShape shape = new CircleShape();
		shape.setRadius(Constants.TURRET_RADIUS);
		Body body = null;
		//bodyDef.position.set(shootingWorm.getPosition().x + (shootingWorm.getOrientation() * 50.0f * Constants.WORLD_SCALE), shootingWorm.getPosition().y);

		bodyDef.fixedRotation = true;
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = Constants.TURRET_DENSITY;
		body = world.createBody(bodyDef);
		Fixture fix = body.createFixture(fixtureDef);
		body.setGravityScale(1.0f);

		fix.setUserData(new UserData(UserData.ObjectType.Turret, this));


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
		 super.render(batch, delta);
	}

	public Vector2 directions() {
		Vector2 result = new Vector2();

		Vector2 position1 = Constants.getScreenSpaceVector(getAdversaryWorm().getBody().getPosition());
		Vector2 position2 = Constants.getScreenSpaceVector(getBody().getPosition());

		result.x = -position2.x + position1.x;
		result.y = -position2.y + position1.y;

		return result;
	}

	public float getAngle() {
		degrees = this.directions().angle(new Vector2(1, 0));
		return degrees;
	}
	
	public void shoot(List<Projectile> output) {
		Projectile projectile = new Projectile(null, WeaponType.WEAPON_TURRET_PROJECTILE, getPosition(), directions().nor());
		output.add(projectile);
	}
	
	public Worm getAdversaryWorm() {
		explosionsimulate = new Explosion(getPosition(), radiusturret, impulseturret);
		Worm worm = null;
		int number = 1;
		ArrayList<Worm> affectedWorms = getWorld().addExplosion(explosionsimulate);
		while (number > 0) {
			for (Worm worm2 : affectedWorms) {
				if (worm2.getPlayerNumber() != shootingWorm.getPlayerNumber()) {

					worm = worm2;
				}
				number--;
			}
		}

		return worm;
	}

public void explodeturret() {
	removeFromWorld();
	getWorld().forgetAfterUpdate(this);
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
//	public void setshotdirectionaction(ShotDirectionAction s) {
//		this.shotdirectionsaction=s;
//	}
//	public ShotDirectionAction getSchotDirectionaction() {
//		return shotdirectionsaction;
//	}
//  
//	  public boolean getShoot() {return shoot;}
//	  public void setshoot(boolean s) {this.shoot=s;}
	  
	  
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
	 
//	  public SnapshotData makeSnapshot() {
//			SnapshotData data = new SnapshotData();
//			return data;
//		}

//public Vector2 directions1() {
//	Vector2 result= new Vector2();
//	
//Vector2 position1 = worm.getPosition();
//Vector2 position2 = getPosition();
//
//result.x= -position2.x+position1.x;
//result.y=-position2.y+position1.y;
//	
//return result;
//	
//
//}
//public void shoot(List<Projectile> output) {
////	explosion = new Explosion(getPosition(),radiusturret, impulseturret);
////    ArrayList<Worm> affectedWorms = getWorld().addExplosion(explosion);
////    for (Worm worm : affectedWorms) {
////    	if (worm.getPlayerNumber() != shootingWorm.getPlayerNumber()) {
//    	Projectile projectile = new Projectile(null, WeaponType.WEAPON_TURRET_PROJECTILE, getPosition(), directions().nor());
//    	output.add(projectile);
//    	}
}

