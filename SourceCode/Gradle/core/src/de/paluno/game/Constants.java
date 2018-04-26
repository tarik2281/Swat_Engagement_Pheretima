package de.paluno.game;

import com.badlogic.gdx.math.Vector2;

public class Constants {
	
	public static final float PHYSICSSTEPLENGTH = 0.00800000037997961f;
	public static final Vector2 GRAVITY = new Vector2(0.0f, -9.81f);

	// number of iterations for the physics simulation
	public static final int VELOCITY_ITERATIONS = 8;
	public static final int POSITION_ITERATIONS = 3;

	private Constants() {
		// An instance should not be created of this class
	}
}
