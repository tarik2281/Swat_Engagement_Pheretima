package de.paluno.game.gameobjects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Align;
import de.paluno.game.Constants;

public class HealthBar extends WorldObject {
    private Worm worm;

    private BitmapFont font;
    private GlyphLayout layout;

    public HealthBar(Worm worm) {
        this.worm = worm;
    }

    @Override
    public void setupAssets(AssetManager manager) {
        // TODO: load font from AssetManager
        font = new BitmapFont();
        // the text moves shaky if we use integer positions
        font.setUseIntegerPositions(false);

        layout = new GlyphLayout();
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        Body body = worm.getBody();
        /*if (body == null) {
            // the worm associated with this HealthBar does not exist anymore so just remove this object from the game
            world.forgetAfterUpdate(this);
            return;
        }*/

        setText(String.valueOf(worm.getHealth()));

        Vector2 position = Constants.getScreenSpaceVector(body.getPosition());

        font.draw(batch, layout, position.x, position.y + 30);
    }

    private void setText(CharSequence text) {
        layout.setText(font, text, getColor(), 0, Align.center, false);
    }

    private Color getColor() {
        Color color = Color.WHITE;

        switch (worm.getPlayerNumber()) {
            case Constants.PLAYER_NUMBER_1:
                color = Constants.PLAYER_1_COLOR;
                break;
            case Constants.PLAYER_NUMBER_2:
                color = Constants.PLAYER_2_COLOR;
                break;
            case 2:
                color = Constants.PLAYER_3_COLOR;
                break;
            case 3:
                color = Constants.PLAYER_4_COLOR;
                break;
            case 4:
                color = Constants.PLAYER_5_COLOR;
                break;
        }

        return color;
    }
}
