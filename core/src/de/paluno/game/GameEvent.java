package de.paluno.game;


import de.paluno.game.gameobjects.GameWorld;

public interface GameEvent {
	Object trigger(GameWorld world);
}
