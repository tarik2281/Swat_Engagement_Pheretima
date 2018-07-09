package de.paluno.game.gameobjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.Assets;
import de.paluno.game.Constants;

public class WindDirectionIndicator extends WorldObject {

    private Texture greenTexture;
    private Texture orangeTexture;
    private Texture redTexture;
    private Sprite sprite;  // Graphical object which implements a texture to draw the object
    private WindHandler windHandler;

    private Sound windMediumSound;
    private Sound windHeavySound;

    public WindDirectionIndicator(WindHandler windHandler) {
        this.windHandler = windHandler;
    }

    @Override
    public void setupAssets(AssetManager manager) {
        greenTexture = manager.get(Assets.windGreen);
        orangeTexture = manager.get(Assets.windOrange);
        redTexture = manager.get(Assets.windRed);

        sprite = new Sprite(greenTexture);

        //sounds
        windMediumSound = manager.get(Assets.windMedium);
        windHeavySound = manager.get(Assets.windHeavy);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (Math.abs(windHandler.getWind()) <= 1)
            sprite.setTexture(greenTexture);
        else if (Math.abs(windHandler.getWind()) <= 3)
            sprite.setTexture(orangeTexture);
        else
            sprite.setTexture(redTexture);

        Vector2 screenPosition = Constants.getScreenSpaceVector(getParent().getPosition());
        sprite.setOriginBasedPosition(screenPosition.x, screenPosition.y + 100);
        sprite.setFlip(windHandler.flipped(), false);
        sprite.draw(batch);
    }
}
