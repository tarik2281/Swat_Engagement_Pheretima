package de.paluno.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Constants {

	public static final Vector2 GRAVITY = new Vector2(0.0f, -9.81f);

	public static final float UPDATE_FREQUENCY = 1.0f / 30.0f;

	// game play constants
	public static final int NUM_PLAYERS = 2;
	public static final int WORM_MAX_HEALTH = 100;

	public static final float END_TURN_TIMER_SECONDS = 3.0f;

	public static final Color PLAYER_1_COLOR = Color.ROYAL;
	public static final Color PLAYER_2_COLOR = Color.FIREBRICK;
	public static final Color PLAYER_3_COLOR = Color.GOLDENROD;
	public static final Color PLAYER_4_COLOR = Color.VIOLET;
	public static final Color PLAYER_5_COLOR = Color.LIME;
	public static final Color[] PLAYER_COLORS = new Color[] {
			Color.TEAL,
			Color.FIREBRICK,
			Color.CYAN,
			Color.LIME,
			Color.GOLDENROD
	};

	public static final String BACKGROUND_LAYER = "BackgroundLayer";
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

	//Turret
	public static final float TURRET_DENSITY = 13234.0f;
	public static final float TURRET_RADIUS = 25.0f * WORLD_SCALE;

	/**
	 * game object attributes in world space (in meters)
	 */
	public static final float CAMERA_FOCUS_TIME = 0.5f;
	public static final float WORM_WIDTH = 18.0f * WORLD_SCALE;
	public static final float WORM_HEIGHT = 25.0f * WORLD_SCALE;
	public static final float WORM_RADIUS = 7.5f * WORLD_SCALE;
	public static final float CRATE_RADIUS = 10.0f * WORLD_SCALE;
	public static final float VIRUS_RADIUS = 30.0f * WORLD_SCALE;
	public static final int VIRUS_DAMAGE = 5;

	public static final Vector2 AIRSTRIKE_SPAWNPOS = new Vector2(1340.0f * WORLD_SCALE, 950.0f * WORLD_SCALE);
	public static final Vector2 AIRSTRIKE_SPAWNPOS2 = new Vector2(1320.0f * WORLD_SCALE, 950.0f * WORLD_SCALE);
	public static final Vector2 AIRSTRIKE_SPAWNPOS3 = new Vector2(1300.0f * WORLD_SCALE, 950.0f * WORLD_SCALE);

	public static final float HEAD_AREA_RADIUS = 6.0f * WORLD_SCALE;
	public static final int HEADSHOT_DAMAGE = 100;

	public static final int DAMAGE_TYPE_VIRUS = 1;
	public static final int DAMAGE_TYPE_PROJECTILE = 2;
	public static final int DEATH_TYPE_FALL_DOWN = 1;
	public static final int DEATH_TYPE_NO_HEALTH = 2;
	public static final int DEATH_TYPE_DISCONNECTED = 3;

	public static float REFRESH_RATE;

	// key bindings
    public static final int KEY_MOVE_LEFT = Input.Keys.LEFT;
    public static final int KEY_MOVE_RIGHT = Input.Keys.RIGHT;
    public static final int KEY_JUMP = Input.Keys.SPACE;
    public static final int KEY_DO_ACTION = Input.Keys.ENTER;
    public static final int KEY_ROTATE_INDICATOR_UP = Input.Keys.UP;
    public static final int KEY_ROTATE_INDICATOR_DOWN = Input.Keys.DOWN;
    public static final int KEY_SELECT_WEAPON_1 = Input.Keys.F1;
    public static final int KEY_SELECT_WEAPON_2 = Input.Keys.F2;
    public static final int KEY_SELECT_WEAPON_3 = Input.Keys.F3;
    public static final int KEY_SELECT_WEAPON_4 = Input.Keys.F4;
    public static final int KEY_SELECT_WEAPON_5 = Input.Keys.F5;
    public static final int KEY_SELECT_WEAPON_6 = Input.Keys.F6;
    public static final int KEY_SELECT_WEAPON_7 = Input.Keys.F7;
    public static final int KEY_SELECT_WEAPON_8 = Input.Keys.F8;
    public static final int KEY_SELECT_WEAPON_MENU = Input.Keys.I;


    // debug key bindings
    public static final int KEY_MOVE_CAMERA_LEFT = Input.Keys.A;
    public static final int KEY_MOVE_CAMERA_RIGHT = Input.Keys.D;
    public static final int KEY_MOVE_CAMERA_UP = Input.Keys.W;
    public static final int KEY_MOVE_CAMERA_DOWN = Input.Keys.S;
    public static final int KEY_TOGGLE_DEBUG_RENDER = Input.Keys.Y;
    public static final int KEY_PAUSE = Input.Keys.ESCAPE;

    // Weapon constants
    public static final int WEAPON_AMMO_INF = -1;
    public static final int WEAPON_AMMO_SPECIAL = 1;

    public static final float RAISE_LEVEL_SPEED = 0.07f;
    public static final float RAISE_LEVEL_LENGTH = 0.3f;

	private Constants() {
		// An instance should not be created of this class
	}

	public static float toUnits(float pixels) {
		return pixels * WORLD_SCALE;
	}

	public static Vector2 getScreenSpaceVector(Vector2 v) {
		return new Vector2(v).scl(SCREEN_SCALE);
	}

	public static Vector2 getWorldSpaceVector(Vector2 v) {
		return new Vector2(v).scl(WORLD_SCALE);
	}
}
