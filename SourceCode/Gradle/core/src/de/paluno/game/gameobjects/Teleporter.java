package de.paluno.game.gameobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.GameState;

public class Teleporter implements  Updatable, Renderable {
    private Worm worm;
    private int playerNumber;
    private World world;
    private Texture texture;
    private Sprite sprite;
    Vector2 cursorPos;

    public Teleporter(World world, int playerNumber){
        this.world = world;
        this.playerNumber = playerNumber;

    }

    @Override
    public void update(float delta, GameState gamestate) {

    }

    @Override
    public void render(SpriteBatch batch, float delta) {

    }
}
