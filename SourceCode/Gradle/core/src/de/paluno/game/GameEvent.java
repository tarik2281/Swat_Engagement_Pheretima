package de.paluno.game;

import de.paluno.game.gameobjects.World;

public interface GameEvent {
	Object trigger(World world);
}
