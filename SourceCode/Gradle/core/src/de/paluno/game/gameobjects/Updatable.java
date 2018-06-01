package de.paluno.game.gameobjects;

import de.paluno.game.GameState;

public interface Updatable {
    void update(float delta, GameState gamestate);
}
