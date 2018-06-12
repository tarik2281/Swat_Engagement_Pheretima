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

    private com.badlogic.gdx.physics.box2d.World world;
    private Rectangle worldBounds;

    private Player[] players;

    private Ground ground;
    private ExplosionMaskRenderer explosionMaskRenderer;
    
    private Projectile projectile;

    private GameCamera camera;
    private boolean isRenderDebug = false;
    private Box2DDebugRenderer debugRenderer;
    private boolean isReplayWorld;
    private boolean skipFrame = false;

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
        if (fixtureA.getUserData() == "Worm" && fixtureB.getUserData() == "Projectile") {
            Projectile projectile = (Projectile)fixtureB.getBody().getUserData();
            if (!projectile.isWormContactEnded() && projectile.getShootingWorm() == fixtureA.getBody().getUserData())
                return false;
        }
        else if (fixtureB.getUserData() == "Worm" && fixtureA.getUserData() == "Projectile") {
            Projectile projectile = (Projectile)fixtureA.getBody().getUserData();
            if (!projectile.isWormContactEnded() && projectile.getShootingWorm() == fixtureB.getBody().getUserData())
                return false;
        }

        return true;
    };

    public World(PlayScreen screen) {
        this.screen = screen;
        isReplayWorld = false;

        objectRegisterQueue = new LinkedList<>();
        objectForgetQueue = new LinkedList<>();
        renderableObjects = new ArrayList<>();
        updatableObjects = new ArrayList<>();

        world = new com.badlogic.gdx.physics.box2d.World(Constants.GRAVITY, true);
        world.setContactListener(new CollisionHandler());
        world.setContactFilter(contactFilter);
        debugRenderer = new Box2DDebugRenderer();

        worldBounds = new Rectangle();

        camera = new GameCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        explosionMaskRenderer = new ExplosionMaskRenderer(camera.getOrthoCamera());

        players = new Player[Constants.NUM_PLAYERS];
    }

    public void initializeNew() {
        ground = new Ground(this, screen.getAssetManager().get(Assets.map), explosionMaskRenderer);
        explosionMaskRenderer.setGround(ground);

        windHandler = new WindHandler();

        worldBounds.set(ground.getWorldOriginX(), ground.getWorldOriginY(),
                ground.getWorldWidth(), ground.getWorldHeight());

        initializePlayer(Constants.PLAYER_NUMBER_1);
        initializePlayer(Constants.PLAYER_NUMBER_2);

        worldBounds.set(ground.getWorldOriginX(), ground.getWorldOriginY(),
                ground.getWorldWidth(), ground.getWorldHeight());
        camera.setBottomLimit(worldBounds.y);

        registerAfterUpdate(ground);
        registerAfterUpdate(windHandler);

        currentPlayer = Constants.PLAYER_NUMBER_1;
        setGameState(GameState.PLAYERTURN);

        InputHandler.getInstance().registerKeyListener(Constants.KEY_TOGGLE_DEBUG_RENDER, keyListener);
    }

    public void initializeFromSnapshot(SnapshotData data) {
        isReplayWorld = true;

        ground = new Ground(this, explosionMaskRenderer, data.ground);
        explosionMaskRenderer.setGround(ground);

        windHandler = new WindHandler(data.windHandler);

        worldBounds.set(data.worldBounds);
        camera.setBottomLimit(worldBounds.y);

        for (Player.SnapshotData playerData : data.player)
            initializePlayer(playerData);

        registerAfterUpdate(ground);
        registerAfterUpdate(windHandler);

        projectile = new Projectile(this, data.projectile);
        spawnProjectile(projectile);
        camera.setCameraPosition(data.projectile.getPosition());
    }

    private void initializePlayer(int playerNumber) {
        players[playerNumber] = new Player(playerNumber, this);
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

    public Worm getPlayerWorm(int playerNumber, int characterNumber) {
    	Player player = players[playerNumber];
    	
    	for (Worm worm : player.characters)
    		if (worm != null && worm.getCharacterNumber() == characterNumber)
    			return worm;
    	
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

    public void updatePhase(float delta) {
        if (currentGameState == GameState.WAITING) {
            boolean advance = true;

            for (Player player : players) {
                for (Worm worm : player.characters) {
                    if (worm != null && worm.getBody() != null)
                    if (worm.getBody().isAwake()) {
                        advance = false;
                    }
                }
            }

            if (advance)
                advanceGameState();
        }

        for (Updatable updatable : updatableObjects) {
            updatable.update(delta, currentGameState);
        }
    }

    public void physicsPhase(float delta) {
        float timeStep = 1.0f / 60.0f;
        world.step(timeStep, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
    }

    public void renderPhase(SpriteBatch batch, float delta) {
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

    public void addExplosion(Vector2 center, float radius) {
        ground.addExplosion(center, radius);

        final ArrayList<Worm> affectedWorms = new ArrayList<>();

        world.QueryAABB((fixture -> {
            if (fixture.getUserData() == "Worm") {
                Worm worm = (Worm)fixture.getBody().getUserData();

                if (!affectedWorms.contains(worm))
                    affectedWorms.add(worm);
            }
            return true;
        }), center.x - radius, center.y - radius, center.x + radius, center.y + radius);

        for (Worm worm : affectedWorms) {
            Vector2 bodyCom = worm.getBody().getWorldCenter();

            if (bodyCom.dst2(center) >= radius * radius)
                continue;

            applyBlastImpulse(worm.getBody(), center, bodyCom, 0.008f);
        }
    }

    private void applyBlastImpulse(Body body, Vector2 blastCenter, Vector2 applyPoint, float blastPower) {
        Vector2 diff = new Vector2(applyPoint).sub(blastCenter);
        float distance = diff.len();

        if (distance == 0)
            return;

        float invDistance = 1.0f / distance;
        diff.scl(invDistance);

        float impulse = blastPower * invDistance * invDistance;
        body.applyLinearImpulse(diff.scl(impulse), applyPoint, true);
    }

    public Body createBody(BodyDef bodyDef) {
        return world.createBody(bodyDef);
    }

    public Joint createJoint(JointDef jointDef) {
        return world.createJoint(jointDef);
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

    public void playerDefeated(Player player) {
        switch (player.getPlayerNumber()) {
            case Constants.PLAYER_NUMBER_1:
                screen.setGameOver(WinningPlayer.PLAYERTWO);
                break;
            case Constants.PLAYER_NUMBER_2:
                screen.setGameOver(WinningPlayer.PLAYERONE);
                break;
        }
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
            case GAMEOVERPLAYERONEWON:
                screen.setGameOver(WinningPlayer.PLAYERONE);
                break;
            case GAMEOVERPLAYERTWOWON:
                screen.setGameOver(WinningPlayer.PLAYERTWO);
                break;
            case SHOOTING:
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
            	else
            		setGameState(GameState.PLAYERTURN);
                break;
        }
    }

    public GameState getGameState() {
        return currentGameState;
    }

    public Vector2 generateSpawnPosition() {
        return ground.getRandomSpawnPosition();
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

        objectForgetQueue.clear();
    }

    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }
    
}
