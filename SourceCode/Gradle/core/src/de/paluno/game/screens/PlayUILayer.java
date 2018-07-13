package de.paluno.game.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.paluno.game.Constants;
import de.paluno.game.EventManager;
import de.paluno.game.GameState;
import de.paluno.game.NetworkClient;

public class PlayUILayer implements Disposable {

    private Stage stage;
    private Table messageTable;
    private ElementGUI elementGUI;
    private WeaponUI weaponUI;
    private ChatWindow chatWindow;

    private EventManager.Listener eventListener = new EventManager.Listener() {
        @Override
        public void handleEvent(EventManager.Type eventType, Object data) {

        }
    };


    public PlayUILayer(AssetManager manager) {
        stage = new Stage(new ScreenViewport());

        messageTable = new Table();
        stage.addActor(messageTable);

        elementGUI = new ElementGUI();
        weaponUI = new WeaponUI(manager, elementGUI);
        stage.addActor(weaponUI.getTable());
    }

    public void addChatWindow(NetworkClient client) {
        if (client != null) {
            chatWindow = new ChatWindow(client);
            chatWindow.initialize(elementGUI.getSkin());
            chatWindow.addToStage(stage, null);
        }
    }

    public WeaponUI getWeaponUI() {
        return weaponUI;
    }

    @Override
    public void dispose() {
        if (chatWindow != null)
            chatWindow.dispose();

        elementGUI.getSkin().dispose();
        stage.dispose();
    }

    public void render(float delta) {
        // apply the ui camera to the SpriteBatch
        stage.act(delta);
        stage.draw();
    }

    public InputProcessor getInputProcessor() {
        return stage;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void showMessage(String message, Color color) {
        if (message == null)
            return;

        Label label = new Label(message, elementGUI.getSkin(), "title", color);
        RemoveActorAction removeActorAction = new RemoveActorAction();
        removeActorAction.setActor(label);
        DelayAction delayAction = new DelayAction(Constants.MESSAGE_DURATION);
        delayAction.setAction(removeActorAction);
        label.addAction(delayAction);

        messageTable.add(label);
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
