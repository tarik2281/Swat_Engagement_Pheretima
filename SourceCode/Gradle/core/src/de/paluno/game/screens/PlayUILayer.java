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
    // messageDisplayTime is used as a timer for how long the message should be shown
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
        // apply the ui camera to the SpriteBatch
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (messageDisplayTime < Constants.MESSAGE_DURATION) {
            // message should be displayed for as long as our given duration
            messageDisplayTime += delta;
            font.draw(batch, messageLayout, viewportWidth / 2, MESSAGE_POSITION_Y);
        }

        batch.end();
    }

    public void showMessage(String message, Color color) {
        if (message == null)
            return;

        // setup the new message
        messageDisplayTime = 0.0f;
        messageLayout.setText(font, message, color, 0, Align.center, false);
    }

    public void setGameState(GameState gameState, int currentPlayer) {
        String message = null;
        Color color = null;

        // set the message and message color according to the current GameState
        switch (gameState) {
            case PLAYERTURN:
                   message = "Spieler "+(currentPlayer+1)+" ist am Zug!";
                   color = Constants.PLAYER_COLORS[currentPlayer];
                break;
            /*case GAMEOVER:
                if(currentPlayer != -1) {
                	message = "Spieler "+(currentPlayer+1)+" hat gewonnen!";
                	color = Constants.PLAYER_COLORS[currentPlayer];
                } else {
                	message = "Unentschieden!";
                	color = Color.LIGHT_GRAY;
                }
                
                break;*/
        }

        showMessage(message, color);
    }
}
