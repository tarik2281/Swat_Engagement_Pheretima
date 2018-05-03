package de.paluno.game;

import com.badlogic.gdx.math.Vector2;

public class Constants {
	
	public static final float PHYSICSSTEPLENGTH = 0.00800000037997961f;
	public static final Vector2 GRAVITY = new Vector2(0.0f, -9.81f);

	public static final int NUM_PLAYERS = 2;

	public static final float SCREEN_SCALE = 100.0f;
	public static final float WORLD_SCALE = 1.0f / SCREEN_SCALE;

	// number of iterations for the physics simulation
	public static final int VELOCITY_ITERATIONS = 8;
	public static final int POSITION_ITERATIONS = 3;

	private Constants() {
		// An instance should not be created of this class
	}

	public static Vector2 getScreenSpaceVector(Vector2 v) {
		return v.scl(SCREEN_SCALE);
	}

	public static Vector2 getWorldSpaceVector(Vector2 v) {
		return v.scl(WORLD_SCALE);
	}
}
