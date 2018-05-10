package de.paluno.game.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Align;
import de.paluno.game.Constants;
import de.paluno.game.screens.PlayScreen;

public class HealthBar implements Renderable {

    private PlayScreen screen;
    private Worm worm;

    private BitmapFont font;
    private GlyphLayout layout;

    public HealthBar(PlayScreen screen, Worm worm) {
        this.screen = screen;
        this.worm = worm;

        font = new BitmapFont();
        // the text moves shaky if we use integer positions
        font.setUseIntegerPositions(false);

        layout = new GlyphLayout();
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        Body body = worm.getBody();
        if (body == null) {
            // the worm associated with this HealthBar does not exist anymore so just remove this object from the game
            screen.forgetAfterUpdate(this);
            return;
        }

        setText(String.valueOf(worm.getHealth()));

        Vector2 position = Constants.getScreenSpaceVector(body.getPosition());

        font.draw(batch, layout, position.x, position.y + 30);
    }

    private void setText(CharSequence text) {
        layout.setText(font, text, getColor(), 0, Align.center, false);
    }

    private Color getColor() {
        Color color = Color.BLACK;

        switch (worm.getPlayerNumber()) {
            case Constants.PLAYER_NUMBER_1:
                color = Constants.PLAYER_1_COLOR;
                break;
            case Constants.PLAYER_NUMBER_2:
                color = Constants.PLAYER_2_COLOR;
                break;
        }

        return color;
    }
}
