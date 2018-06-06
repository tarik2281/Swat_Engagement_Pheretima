package de.paluno.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.gameobjects.Renderable;
import de.paluno.game.gameobjects.Updatable;
import de.paluno.game.gameobjects.World;
import de.paluno.game.gameobjects.Worm;
import de.paluno.game.screens.PlayScreen;

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
        public void attachToWorm(Worm worm) {
            this.worm = worm;
        }




    @Override
    public void render(SpriteBatch batch, float delta) {

        if (worm != null) {
            if ((windHandler.getX() > 0 && windHandler.getX() <= 2) || (windHandler.getX() < 0 && windHandler.getX() >= -2)) {
                texture = world.getAssetManager().get(Assets.windGreen);
                System.out.println(windHandler.getX());
            } else if ((windHandler.getX() > 2 && windHandler.getX() <= 4) ||
                    (windHandler.getX() < -2 && windHandler.getX() >= -4)) {
                texture = world.getAssetManager().get(Assets.windOrange);
                System.out.println(windHandler.getX());
            } else {
                texture = world.getAssetManager().get(Assets.windRed);
                System.out.println(windHandler.getX());
            }

            sprite = new Sprite(texture);


            // sets the indicator at the position of the current worm
            Vector2 windIndicator = Constants.getScreenSpaceVector(this.worm.getBody().getPosition());


            //sprite.setOriginCenter();
            sprite.setOriginBasedPosition(windIndicator.x, windIndicator.y + 100);

            // rotates the indicator after every turn, based of the random x coordinate
            if (this.windHandler != null) {
                sprite.setFlip(windHandler.flipped(), false);
            }
            sprite.draw(batch);
        }

    }


    public void setCloningParameters(WindDirectionIndicator clone) {
        // TODO Auto-generated method stub

    }
}
