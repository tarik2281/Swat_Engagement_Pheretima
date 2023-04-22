package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.paluno.game.Assets;
import de.paluno.game.Constants;

public class Water extends WorldObject {

    private Texture texture;
    private TextureRegion region;
    private float position = 0.0f;

    @Override
    public void setupAssets(AssetManager manager) {
        texture = manager.get(Assets.waterTexture);
        int width = (int)(getWorld().getWorldBounds().width * Constants.SCREEN_SCALE);
        if (Gdx.graphics.getWidth() > width)
            width = Gdx.graphics.getWidth();
        region = new TextureRegion(texture, width + 32, 25);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        position -= delta * 16.0f;
        if (position <= -32.0f)
            position = 0.0f;

        batch.setColor(1, 1, 1, 0.6f);
        batch.draw(region, position, 0);
        batch.setColor(1, 1, 1, 1);
    }

    public void setLevel(float level) {
        region.setRegionHeight((int)level + 25);
    }
}
