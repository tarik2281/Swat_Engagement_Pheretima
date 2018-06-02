package de.paluno.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.gameobjects.Renderable;
import de.paluno.game.gameobjects.Updatable;
import de.paluno.game.gameobjects.Worm;
import de.paluno.game.screens.PlayScreen;

public class WindDirectionIndicator implements Renderable {
    private PlayScreen playScreen;
   private Worm worm;
    private int playerNumber;
    private Texture texture;
    private Sprite sprite;  // Graphical object which implements a texture to draw the object
    private WindHandler windHandler;

    public WindDirectionIndicator(int playerNumber, PlayScreen playScreen, Worm worm, WindHandler windHandler) {
        this.playScreen = playScreen;
        this.playerNumber = playerNumber;
        this.worm = worm;
        this.windHandler = windHandler;

        texture = new Texture(Gdx.files.internal("wind.png"));
        sprite = new Sprite(texture);

    }


    @Override
    public void render(SpriteBatch batch, float delta) {
        // sets the indicator at the position of the current worm
        Vector2 windIndicator = Constants.getScreenSpaceVector(this.worm.getBody().getPosition());


        sprite.setOriginCenter();
        sprite.setOriginBasedPosition(windIndicator.x, windIndicator.y + 100);

        // rotates the indicator after every turn, based of the random x coordinate
        if (this.windHandler != null) {
            sprite.setRotation(this.windHandler.getDegreeWind());
        }
        sprite.draw(batch);
    }


    public void setCloningParameters(WindDirectionIndicator clone) {
        // TODO Auto-generated method stub

    }
}
