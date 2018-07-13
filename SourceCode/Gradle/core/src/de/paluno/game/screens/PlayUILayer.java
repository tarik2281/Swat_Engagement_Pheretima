package de.paluno.game.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.paluno.game.Constants;
import de.paluno.game.EventManager;
import de.paluno.game.NetworkClient;
import de.paluno.game.gameobjects.Player;

public class PlayUILayer implements Disposable {

    private Stage stage;
    private ElementGUI elementGUI;
    private WeaponUI weaponUI;
    private ChatWindow chatWindow;
    private Container<Label> messageContainer;
    private Container<Label> replayContainer;
    private boolean showingReplay = false;

    private EventManager.Listener eventListener = new EventManager.Listener() {
        @Override
        public void handleEvent(EventManager.Type eventType, Object data) {
            switch (eventType) {
                case Replay:
                    showReplay();
                    break;
                case ReplayEnded:
                    hideReplay();
                    break;
                case StartPlayerTurn:
                    if (!showingReplay) {
                        Player player = (Player) data;
                        showMessage(player.getName() + " ist am Zug!", Constants.PLAYER_COLORS[player.getPlayerNumber()]);
                    }
                    break;
            }
        }
    };


    public PlayUILayer(AssetManager manager) {
        stage = new Stage(new ScreenViewport());

        elementGUI = new ElementGUI();

        Label messageLabel = new Label("", elementGUI.getSkin(), "title");
        messageContainer = new Container<>(messageLabel);
        messageContainer.setFillParent(true);
        messageContainer.center();
        messageContainer.padBottom(300);

        Label replayLabel = new Label("Replay", elementGUI.getSkin(), "title", Color.WHITE);
        replayContainer = new Container<>(replayLabel);
        replayContainer.setFillParent(true);
        replayContainer.align(Align.topLeft);
        replayContainer.pad(200);

        weaponUI = new WeaponUI(manager, elementGUI);
        stage.addActor(weaponUI.getTable());

        EventManager.getInstance().addListener(eventListener, EventManager.Type.StartPlayerTurn, EventManager.Type.Replay, EventManager.Type.ReplayEnded);
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
        EventManager.getInstance().removeListener(eventListener, EventManager.Type.StartPlayerTurn, EventManager.Type.Replay, EventManager.Type.ReplayEnded);

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

    private void showReplay() {
        showingReplay = true;
        stage.addActor(replayContainer);
    }

    private void hideReplay() {
        showingReplay = false;
        replayContainer.remove();
    }

    public void showMessage(String message, Color color) {
        if (message == null)
            return;

        messageContainer.getActor().setText(message);
        messageContainer.getActor().setColor(color);

        RemoveActorAction removeActorAction = new RemoveActorAction();
        removeActorAction.setActor(messageContainer);
        DelayAction delayAction = new DelayAction(Constants.MESSAGE_DURATION);
        delayAction.setAction(removeActorAction);
        messageContainer.addAction(delayAction);

        stage.addActor(messageContainer);
    }
}
