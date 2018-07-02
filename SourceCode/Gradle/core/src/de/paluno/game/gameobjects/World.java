package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import de.paluno.game.*;
import de.paluno.game.gameobjects.ground.ExplosionMaskRenderer;
import de.paluno.game.gameobjects.ground.Ground;
import de.paluno.game.screens.PlayScreen;
import de.paluno.game.screens.WinningPlayer;

import java.util.ArrayList;
import java.util.LinkedList;

public class World implements Disposable {

	public class SnapshotData{
		private WindHandler.SnapshotData windHandler;
        private int currentPlayer;
		private Projectile.SnapshotData projectile;
		private Player.SnapshotData[] player;
	    private Rectangle worldBounds;
		private Ground.SnapshotData ground;
	}

    private PlayScreen screen;
    private WindHandler windHandler;
    private int currentPlayer;
    private GameState currentGameState = GameState.NONE;
    private boolean wormDied = false;

    private LinkedList<Object> objectRegisterQueue;
    private LinkedList<Object> objectForgetQueue;
    private ArrayList<Renderable> renderableObjects;
    private ArrayList<Updatable> updatableObjects;
    private ArrayList<Joint> jointsToDestroy;

    private com.badlogic.gdx.physics.box2d.World world;
    private Rectangle worldBounds;
    private float targetLimit;

    private Player[] players;

    private Ground ground;
    private ExplosionMaskRenderer explosionMaskRenderer;

    private Projectile projectile;

    private GameCamera camera;
    private boolean isRenderDebug = false;
    private Box2DDebugRenderer debugRenderer;
    private boolean isReplayWorld;
    private boolean skipFrame = false;
    
    private Airdrop airdrop;
    private AirdropCrate airdropCrate;
    private boolean dropping = false;
    private boolean initDrop = false;
    private int winningPlayer = -1;

    private InputHandler.KeyListener keyListener = (keyCode, keyDown) -> {
        if (keyDown) {
            switch (keyCode) {
                case Constants.KEY_TOGGLE_DEBUG_RENDER:
                    toggleDebugRender();
                    return true;
            }
        }

        return true;
    };

    private ContactFilter contactFilter = (fixtureA, fixtureB) -> {
        if (UserData.getType(fixtureA) == UserData.ObjectType.Worm && UserData.getType(fixtureB) == UserData.ObjectType.Projectile) {
            Projectile projectile = UserData.getObject(fixtureB);
            if (!projectile.isWormContactEnded() && projectile.getShootingWorm() == UserData.getObject(fixtureA))
                return false;
        }
        else if (UserData.getType(fixtureB) == UserData.ObjectType.Worm && UserData.getType(fixtureA) == UserData.ObjectType.Projectile) {
            Projectile projectile = UserData.getObject(fixtureA);
            if (!projectile.isWormContactEnded() && projectile.getShootingWorm() == UserData.getObject(fixtureB))
                return false;
        }

        return true;
    };

    public World() {
    	//f�r den Test
    }

    public World(PlayScreen screen) {
        this.screen = screen;
        isReplayWorld = false;

        objectRegisterQueue = new LinkedList<>();
        objectForgetQueue = new LinkedList<>();
        renderableObjects = new ArrayList<>();
        updatableObjects = new ArrayList<>();
        jointsToDestroy = new ArrayList<>();

        world = new com.badlogic.gdx.physics.box2d.World(Constants.GRAVITY, true);
        world.setContactListener(new CollisionHandler());
        world.setContactFilter(contactFilter);
        debugRenderer = new Box2DDebugRenderer();

        worldBounds = new Rectangle();

        camera = new GameCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        explosionMaskRenderer = new ExplosionMaskRenderer(camera.getOrthoCamera());
        
        airdrop = new Airdrop();
    }

    public void initializeNew(int mapNumber, int numWorms, int numPlayers) {
        /*DEBUG*/numPlayers = 2;
    	ground = new Ground(this, screen.getAssetManager().get(Assets.getMapByIndex(mapNumber)), explosionMaskRenderer);
        explosionMaskRenderer.setGround(ground);

        windHandler = new WindHandler();

        worldBounds.set(ground.getWorldOriginX(), ground.getWorldOriginY(),
                ground.getWorldWidth(), ground.getWorldHeight());

        players = new Player[numPlayers];
        for(int i = 0; i < numPlayers; i++) {
        	initializePlayer(i, numWorms);
        }

        worldBounds.set(ground.getWorldOriginX(), ground.getWorldOriginY(),
                ground.getWorldWidth(), ground.getWorldHeight());
        camera.setBottomLimit(worldBounds.y);

        registerAfterUpdate(ground);
        registerAfterUpdate(windHandler);

        currentPlayer = Constants.PLAYER_NUMBER_1;
        setGameState(GameState.WAITING);

        InputHandler.getInstance().registerKeyListener(Constants.KEY_TOGGLE_DEBUG_RENDER, keyListener);
    }

    public void initializeFromSnapshot(SnapshotData data) {
        isReplayWorld = true;

        ground = new Ground(this, explosionMaskRenderer, data.ground);
        explosionMaskRenderer.setGround(ground);

        windHandler = new WindHandler(data.windHandler);

        worldBounds.set(data.worldBounds);
        camera.setBottomLimit(worldBounds.y);

        players = new Player[data.player.length];
        for (Player.SnapshotData playerData : data.player)
            initializePlayer(playerData);

        registerAfterUpdate(ground);
        registerAfterUpdate(windHandler);

        projectile = new Projectile(this, data.projectile);
        spawnProjectile(projectile);
        camera.setCameraPosition(data.projectile.getPosition());
    }

    private void initializePlayer(int playerNumber, int numWorms) {
        players[playerNumber] = new Player(playerNumber, numWorms, this);
        players[playerNumber].setWindHandler(windHandler);
    }

    private void initializePlayer(Player.SnapshotData data) {
        players[data.getPlayerNumber()] = new Player(data, this);
        players[data.getPlayerNumber()].setWindHandler(windHandler);
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        explosionMaskRenderer.dispose();
    }

    public Worm getWormForPlayer(int playerNumber, int characterNumber) {
        Player player = players[playerNumber];

        if (player != null)
            return player.getWormByNumber(characterNumber);

        return null;
    }

    public void toggleDebugRender() {
        isRenderDebug = !isRenderDebug;
    }

    public void doGameLoop(SpriteBatch batch, float delta) {
        registerObjects();

        if (!isReplayWorld() || !skipFrame) {
        	updatePhase(delta);
        	physicsPhase(delta);
        	skipFrame = true;
        }
        else {
        	skipFrame = false;
        }

        renderPhase(batch, delta);

        forgetObjects();
    }

    private void updatePhase(float delta) {
        if (currentGameState == GameState.WAITING) {
            boolean advance = true;

            players: for (Player player : players) {
                for (Worm worm : player.getCharacters()) {
                    if (worm != null && worm.getBody() != null && worm.getBody().isAwake()) {
                        advance = false;
                        break players;
                    }
                }
            }

            if (advance)
                advanceGameState();
        }
        else if (currentGameState == GameState.ROUND_END) {
            if(dropping) {
            	this.camera.setCameraFocus(this.airdropCrate);
            }
        	
        	if(worldBounds.y < targetLimit) {
	            worldBounds.y += Constants.RAISE_LIMIT_SPEED * delta;
	            camera.setBottomLimit(worldBounds.y);
        	} else if (worldBounds.y >= targetLimit) {
        		if(!initDrop) {
                	this.airdropCrate = (AirdropCrate) airdrop.trigger(this);
                	dropping = true;
                	initDrop = true;
                }
        		if(initDrop && !dropping)
        			advanceGameState();
        	}
        }

        for (Updatable updatable : updatableObjects) {
            updatable.update(delta, currentGameState);
        }
        
        //if(this.projectile == null) camera.setCameraFocus(this.getCurrentPlayer().getCurrentWorm());
    }

    private void physicsPhase(float delta) {
        float timeStep = 1.0f / 60.0f;
        world.step(timeStep, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
    }

    private void renderPhase(SpriteBatch batch, float delta) {
        camera.update(delta);

        explosionMaskRenderer.renderDepthMask();

        batch.setProjectionMatrix(camera.getScreenProjection());
        batch.begin();

        for (Renderable renderable : renderableObjects) {
            renderable.render(batch, delta);
        }

        batch.end();

        if (isRenderDebug)
            debugRenderer.render(world, camera.getDebugProjection());
    }

    public void registerAfterUpdate(Object gameObject) {
        // add object to queue
        objectRegisterQueue.add(gameObject);
    }

    public void forgetAfterUpdate(Object gameObject) {
        // add object to queue
        objectForgetQueue.add(gameObject);
    }
    
    public void registerDestroyJoint(Joint joint) {
    	jointsToDestroy.add(joint);
    }

    public void setWormDied(boolean wormDied) {
    	this.wormDied = wormDied;
    }

    public boolean isWormDied() {
    	return wormDied;
    }

    public boolean isReplayWorld() {
    	return isReplayWorld;
    }

    public SnapshotData makeSnapshot() {
    	SnapshotData data = new SnapshotData();

    	data.currentPlayer = currentPlayer;
    	data.worldBounds = new Rectangle(worldBounds);
    	data.windHandler = windHandler.makeSnapshot();
    	data.player = new Player.SnapshotData[players.length];

    	for (int i = 0; i < players.length; i++)
    		data.player[i] = players[i].makeSnapshot();

    	data.ground = ground.makeSnapshot();
    	data.projectile = projectile.makeSnapshot();

    	return data;
    }

    public ArrayList<Worm> addExplosion(Explosion explosion) {
        if (explosion.getBlastPower() > 0.0f)
            ground.addExplosion(explosion);

        final ArrayList<Worm> affectedWorms = new ArrayList<>();

        world.QueryAABB((fixture -> {
            if (UserData.getType(fixture) == UserData.ObjectType.Worm) {
                Worm worm = UserData.getObject(fixture);

                if (!affectedWorms.contains(worm))
                    affectedWorms.add(worm);
            }
            return true;
        }), explosion.getLowerX(), explosion.getLowerY(), explosion.getUpperX(), explosion.getUpperY());

        affectedWorms.removeIf(worm -> !explosion.applyBlastImpulse(worm));

        return affectedWorms;
    }

    public Body createBody(BodyDef bodyDef) {
        return world.createBody(bodyDef);
    }

    public Joint createJoint(JointDef jointDef) {
        return world.createJoint(jointDef);
    }
    
    public void destroyJoint(Joint joint) {
    	world.destroyJoint(joint);
    }

    public com.badlogic.gdx.physics.box2d.World getWorld() {
        return world;
    }

    public GameCamera getCamera() {
        return camera;
    }

    public AssetManager getAssetManager() {
        return screen.getAssetManager();
    }

    private void setWormsStatic(boolean isStatic) {
        for (Player player : players) {
            player.setWormsStatic(isStatic);
        }
    }

    private void shiftPlayers() {
        currentPlayer = (currentPlayer + 1) % Constants.NUM_PLAYERS;
    }

    private void setGameState(GameState gameState) {
        switch (this.currentGameState) {
            case PLAYERTURN:
                getCurrentPlayer().onEndTurn();
                shiftPlayers();
                setWormsStatic(false);
                break;
            case SHOOTING:
            	projectile = null;
            	break;
        }

        this.currentGameState = gameState;

        screen.setGameState(this, gameState, currentPlayer);


        switch (gameState) {
            case PLAYERTURN:
                setWormsStatic(true);
                getCurrentPlayer().onBeginTurn();
                camera.setCameraFocus(getCurrentPlayer().getCurrentWorm());
                windHandler.setNextWind();
                setWormDied(false);
                break;
            case GAMEOVER:
                screen.setGameOver(winningPlayer);
                break;
            case SHOOTING:
                break;
            case ROUND_END:
                //this.airdropCrate = (AirdropCrate)airdrop.trigger(this);
                //this.dropping = true;
            	targetLimit = worldBounds.y + Constants.RAISE_LIMIT_LENGTH;

                for (Player player : players)
                    player.setIsRoundEnded(false);
                break;
        }
    }

    public void advanceGameState() {
        switch (currentGameState) {
            case PLAYERTURN:
                setGameState(GameState.WAITING);
                break;
            case SHOOTING:
                setGameState(GameState.WAITING);
                break;
            case WAITING:
            	if (isReplayWorld())
            		setGameState(GameState.REPLAY_ENDED);
            	else {
            	    int remainingPlayers = 0;
            	    for(int i = 0; i < players.length; i++) {
            	    	if(!players[i].isDefeated()) {
            	    		remainingPlayers++;
            	    		winningPlayer = i;
            	    	}
            	    }
            		if (remainingPlayers < 2)
            	        setGameState(GameState.GAMEOVER);
            	    else {
                        winningPlayer = -1;
            	    	boolean raiseLimit = true;

                        for (Player player : players) {
                            if (!player.isRoundEnded())
                                raiseLimit = false;
                        }

                        if (raiseLimit)
                            setGameState(GameState.ROUND_END);
                        else
                            setGameState(GameState.PLAYERTURN);
            	    }
                }
                break;
            case ROUND_END:
                setGameState(GameState.PLAYERTURN);
                dropping = false;
                initDrop = false;
                break;
        }
    }

    public GameState getGameState() {
        return currentGameState;
    }

    public Vector2 generateSpawnPosition() {
        return ground.getRandomSpawnPosition();
    }
    
    public Vector2 generateDropPosition() {
    	return ground.getRandomDropPoint();
    }

    public boolean isInWorldBounds(Body body) {
        return body.getPosition().y > worldBounds.y;
    }

    public Rectangle getWorldBounds() {
        return worldBounds;
    }

    public void spawnProjectile(Projectile projectile) {
    	this.projectile = projectile;
        setGameState(GameState.SHOOTING);
        windHandler.setProjectile(projectile);
        registerAfterUpdate(projectile);
        camera.setCameraFocus(projectile);
    }

    private void registerObjects() {
        // add all objects from queue
        for (Object gameObject : objectRegisterQueue) {
            if (gameObject instanceof Updatable) {
                updatableObjects.add((Updatable)gameObject);
            }
            if (gameObject instanceof PhysicsObject) {
                PhysicsObject physicsObject = (PhysicsObject)gameObject;
                physicsObject.setupBody();
                physicsObject.getBody().setUserData(gameObject);
            }
            if (gameObject instanceof Renderable) {
                renderableObjects.add((Renderable)gameObject);
            }
        }

        objectRegisterQueue.clear();
    }

    private void forgetObjects() {
        // remove all objects from queue
        for (Object gameObject : objectForgetQueue) {
            if (gameObject instanceof Updatable) {
                updatableObjects.remove((Updatable)gameObject);
            }
            if (gameObject instanceof PhysicsObject) {
                PhysicsObject physicsObject = (PhysicsObject)gameObject;
                if (physicsObject.getBody() != null)
                    world.destroyBody(physicsObject.getBody());
                physicsObject.setBodyToNullReference();
            }
            if (gameObject instanceof Renderable) {
                renderableObjects.remove((Renderable)gameObject);
            }
            if (gameObject instanceof Disposable) {
                ((Disposable)gameObject).dispose();
            }

            if (gameObject == camera.getCameraFocus())
                camera.setCameraFocus(null);
        }
        
        if(!jointsToDestroy.isEmpty()) {
	        for(Joint toDestroy : jointsToDestroy) {
	        	destroyJoint(toDestroy);
	        }
        }

        objectForgetQueue.clear();
        jointsToDestroy.clear();
    }

    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }
    
    public void spawnDebugDrop(WeaponType drop, float x) {
    	this.airdrop.debugSpawn(this, new Vector2(Math.round(x), Math.round(ground.getWorldWidth())), drop);
    }
    
    public void setCrateLanded() {
    	this.dropping = false;
    	//this.camera.setCameraFocus(this.getCurrentPlayer().getCurrentWorm());
    	//if(this.currentGameState == GameState.ROUND_END) advanceGameState();
    }

}
