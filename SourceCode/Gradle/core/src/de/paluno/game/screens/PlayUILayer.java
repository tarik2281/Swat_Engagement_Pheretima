package de.paluno.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import de.paluno.game.Constants;
import de.paluno.game.GameState;

public class PlayUILayer {

    private static final int MESSAGE_POSITION_Y = 100;

    private OrthographicCamera camera;
    private BitmapFont font;
    private float messageDisplayTime;

    private GlyphLayout messageLayout;

    private float viewportWidth;
    private float viewportHeight;
    // TODO: use scene2d to build up UI

    public PlayUILayer(float viewportWidth, float viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;

        // setup separate ui camera which does not move with the game world
        camera = new OrthographicCamera();
        camera.setToOrtho(true, viewportWidth, viewportHeight);
        camera.position.set(viewportWidth / 2, viewportHeight / 2, 0);
        camera.update();

        font = new BitmapFont(true);
        messageDisplayTime = Constants.MESSAGE_DURATION;

        messageLayout = new GlyphLayout();
    }

    public void render(SpriteBatch batch, float delta) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (messageDisplayTime < Constants.MESSAGE_DURATION) {
            messageDisplayTime += delta;
            font.draw(batch, messageLayout, viewportWidth / 2, MESSAGE_POSITION_Y);
        }

        batch.end();
    }

    public void showMessage(CharSequence message, Color color) {
        if (message == null)
            return;

        messageDisplayTime = 0.0f;
        messageLayout.setText(font, message, color, 0, Align.center, false);
    }

    public void setGameState(GameState gameState) {
        String message = null;
        Color color = null;

        switch (gameState) {
            case PLAYERONETURN:
                message = "Spieler 1 ist am Zug!";
                color = Constants.PLAYER_1_COLOR;
                break;
            case PLAYERTWOTURN:
                message = "Spieler 2 ist am Zug!";
                color = Constants.PLAYER_2_COLOR;
                break;
            case GAMEOVERPLAYERONEWON:
                message = "Spieler 1 hat gewonnen!";
                color = Constants.PLAYER_1_COLOR;
                break;
            case GAMEOVERPLAYERTWOWON:
                message = "Spieler 2 hat gewonnen!";
                color = Constants.PLAYER_2_COLOR;
                break;
        }

        showMessage(message, color);
    }
}
