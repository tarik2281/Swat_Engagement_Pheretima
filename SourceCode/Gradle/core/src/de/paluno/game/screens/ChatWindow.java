package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import de.paluno.game.DataHandler;
import de.paluno.game.NetworkClient;
import de.paluno.game.interfaces.ChatMessage;
import de.paluno.game.interfaces.UserMessage;

public class ChatWindow implements Disposable {

    private NetworkClient client;
    private Stage stage;
    private Table table;
    private Skin skin;

    private Table chatTable;
    private ScrollPane chatScrollPane;
    private ElementGUI elementGUI;

    private DataHandler messageHandler = (client, data) -> {
        if (data instanceof ChatMessage) {
            ChatMessage chatMessage = (ChatMessage)data;
            addMessage(chatMessage.getUserName(), chatMessage.getMessage());
        }
        else if (data instanceof UserMessage) {
            UserMessage message = (UserMessage)data;
            switch (message.getType()) {
                case UserJoined:
                    addMessage("User " + message.getName() + " joined the lobby.");
                    break;
                case UserLeft:
                    addMessage("User " + message.getName() + " left the lobby.");
                    break;
            }
        }
    };

    public ChatWindow(NetworkClient client) {
        this.client = client;
    }

    public Table getTable() {
        return table;
    }

    private void addMessage(String userName, String message) {
        String outMessage = userName + ": " + message;

        chatTable.row();
        chatTable.add(outMessage).left();
        Timer.post(new Timer.Task() {
            @Override
            public void run() {
                chatScrollPane.scrollTo(0, 0, 0, 0);
            }
        });
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message);
        client.send(chatMessage);
    }

    public void addMessage(String message) {
        chatTable.row();
        chatTable.add(message, "small", Color.FIREBRICK).left();
        Timer.post(new Timer.Task() {
            @Override
            public void run() {
                chatScrollPane.scrollTo(0, 0, 0, 0);
            }
        });
    }




    public void initialize() {
        client.registerDataHandler(messageHandler);
        elementGUI = new ElementGUI();

        skin = elementGUI.getSkin();

        stage = new Stage();
        table = new Table(skin);
        table.setFillParent(true);
        table.align(Align.bottomLeft);

        stage.addActor(table);

        TextField input = new TextField("", skin);
        input.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (event.getKeyCode()) {
                    case Input.Keys.ENTER:
                        sendMessage(input.getText());
                        input.setText(null);
                        break;
                    case Input.Keys.ESCAPE:
                        stage.unfocusAll();
                        break;
                }
                return super.keyDown(event, keycode);
            }

        });

        chatTable = new Table(skin);
        chatScrollPane = new ScrollPane(chatTable, skin);

        table.add(chatScrollPane).size(500, 200);
        table.row();
        table.add(input).width(500);

        chatTable.align(Align.bottomLeft);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        client.unregisterDataHandler(messageHandler);

        stage.dispose();
        skin.dispose();
    }

    public InputProcessor getInputProcessor() {
        return stage;
    }
}
