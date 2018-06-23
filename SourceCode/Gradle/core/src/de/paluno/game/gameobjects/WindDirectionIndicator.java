package de.paluno.game.gameobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.Assets;
import de.paluno.game.Constants;

public class WindDirectionIndicator implements Renderable {


    private int playerNumber;
    private Texture texture;
    private Sprite sprite;  // Graphical object which implements a texture to draw the object
    private WindHandler windHandler;
    private World world;
    private Worm worm;

    public WindDirectionIndicator(int playerNumber, World world, WindHandler windHandler) {
        this.world = world;
        this.playerNumber = playerNumber;

        this.windHandler = windHandler;


    }

    // sets the indicator above the worm
    public void attachToWorm(Worm worm) {
        this.worm = worm;
    }


    @Override
    public void render(SpriteBatch batch, float delta) {

        // loading of different assets based of the wind force
        if (worm != null) {
                if (windHandler.getX() == 0) {
                    return;
                } else if ((windHandler.getX() > 0 && windHandler.getX() <= 1) ||
                        (windHandler.getX() < 0 && windHandler.getX() >= -1)) {
                    texture = world.getAssetManager().get(Assets.windGreen);
                } else if ((windHandler.getX() > 1 && windHandler.getX() <= 3) ||
                        (windHandler.getX() < -1 && windHandler.getX() >= -3)) {
                    texture = world.getAssetManager().get(Assets.windOrange);
                } else {
                    texture = world.getAssetManager().get(Assets.windRed);
                }


                sprite = new Sprite(texture);


                // sets the indicator at the position of the current worm
                Vector2 windIndicator = Constants.getScreenSpaceVector(this.worm.getBody().getPosition());

                // Sets the position where the sprite will be drawn, relative to its current origin = center
                sprite.setOriginBasedPosition(windIndicator.x, windIndicator.y + 100);

                // flips the indicator sprite after every turn, based of the random x coordinate
                if (this.windHandler != null) {
                    sprite.setFlip(windHandler.flipped(), false);
                    //System.out.println(windHandler.getX());
                }
                sprite.draw(batch);
        }

    }


    public void setCloningParameters(WindDirectionIndicator clone) {
        // TODO Auto-generated method stub

    }
}