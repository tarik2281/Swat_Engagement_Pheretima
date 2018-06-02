package de.paluno.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Constants {
	
	public static final float PHYSICSSTEPLENGTH = 0.00800000037997961f;
	public static final Vector2 GRAVITY = new Vector2(0.0f, -9.81f);

	// game play constants
	public static final int NUM_PLAYERS = 2;
	public static final int WORM_MAX_HEALTH = 100;
	public static final int PROJECTILE_DAMAGE = 40;

	public static final int PLAYER_NUMBER_1 = 0;
	public static final int PLAYER_NUMBER_2 = 1;

	public static final Color PLAYER_1_COLOR = Color.TEAL;
	public static final Color PLAYER_2_COLOR = Color.FIREBRICK;

	public static final String TILE_LAYER = "TileLayer";
	public static final String COLLISION_LAYER = "CollisionLayer";
	public static final String SPAWN_LAYER = "SpawnPositions";

	// !!! all constants and parameters in methods and constructors should be given in world space (in meters),
	//     unless explicitly stated !!!
	public static final float SCREEN_SCALE = 100.0f;
	public static final float WORLD_SCALE = 1.0f / SCREEN_SCALE;

	// number of iterations for the physics simulation
	public static final int VELOCITY_ITERATIONS = 8;
	public static final int POSITION_ITERATIONS = 3;
	
	// Worm orientation constants
	public static final int WORM_DIRECTION_LEFT = -1;
	public static final int WORM_DIRECTION_RIGHT = 1;
	
	// Worm movement code constants
	public static final int MOVEMENT_LEFT = -1;
	public static final int MOVEMENT_RIGHT = 1;
	public static final int MOVEMENT_UP = 1;
	public static final int MOVEMENT_DOWN = -1;
	public static final int MOVEMENT_NO_MOVEMENT = 0;

	// Worm movement modifier constant
	public static final float MOVE_VELOCITY = 1.0f; // m/s
	public static final float JUMP_VELOCITY = 4.0f; // m/s

	public static final float CAMERA_MOVE_VELOCITY = 3.0f; // m/s

	public static final float MESSAGE_DURATION = 2.0f; // in seconds

	/**
	 * game object attributes in world space (in meters)
	 */
	public static final float WORM_WIDTH = 18.0f * WORLD_SCALE;
	public static final float WORM_HEIGHT = 25.0f * WORLD_SCALE;
	public static final float WORM_RADIUS_SQUARE = WORM_WIDTH * WORM_WIDTH / 4 + WORM_HEIGHT * WORM_HEIGHT / 4;

	public static final float PROJECTILE_SPAWN_OFFSET = 25.0f * WORLD_SCALE;

	// key bindings
    public static final int KEY_MOVE_LEFT = Input.Keys.LEFT;
    public static final int KEY_MOVE_RIGHT = Input.Keys.RIGHT;
    public static final int KEY_JUMP = Input.Keys.SPACE;
    public static final int KEY_DO_ACTION = Input.Keys.ENTER;
    public static final int KEY_ROTATE_INDICATOR_UP = Input.Keys.UP;
    public static final int KEY_ROTATE_INDICATOR_DOWN = Input.Keys.DOWN;

    // debug key bindings
    public static final int KEY_MOVE_CAMERA_LEFT = Input.Keys.A;
    public static final int KEY_MOVE_CAMERA_RIGHT = Input.Keys.D;
    public static final int KEY_MOVE_CAMERA_UP = Input.Keys.W;
    public static final int KEY_MOVE_CAMERA_DOWN = Input.Keys.S;
    public static final int KEY_TOGGLE_CAMERA_FOCUS = Input.Keys.F;
    public static final int KEY_TOGGLE_DEBUG_RENDER = Input.Keys.Y;

	private Constants() {
		// An instance should not be created of this class
	}

	public static Vector2 getScreenSpaceVector(Vector2 v) {
		return new Vector2(v).scl(SCREEN_SCALE);
	}

	public static Vector2 getWorldSpaceVector(Vector2 v) {
		return new Vector2(v).scl(WORLD_SCALE);
	}
}
