package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.paluno.game.DataHandler;
import de.paluno.game.NetworkClient;
import de.paluno.game.SEPGame;
import de.paluno.game.interfaces.*;

public class MultiplayerScreen extends ScreenAdapter {

    private SEPGame game;

    private int mapNumber;
    private int numWorms;

    private NetworkClient client;
    private Stage stage;
    private Table table;
    private Skin skin;
    private ScrollPane scrollPane;
    private Table scrollTable;
    private List<LobbyData> lobbyList;
    private TextButton startButton;
    private List<String> userList;
    private Array<String> users;
    private Label lobbyName;

    private boolean inLobby = false;
    private ChatWindow chatWindow;

    private InputMultiplexer inputMultiplexer;

    private DataHandler dataHandler = (client, data) -> {
        if (data instanceof UserLoginRequest.Result) {
            UserLoginRequest.Result result = (UserLoginRequest.Result)data;
            showLobbies();
        }
        else if (data instanceof LobbyListRequest.Result) {
            LobbyListRequest.Result result = (LobbyListRequest.Result)data;
            lobbyList.setItems(result.lobbies);
        }
        else if (data instanceof LobbyCreateRequest.Result) {
            if (users == null)
                users = new Array<>();
            showLobby(((LobbyCreateRequest.Result) data).lobbyId);
        }
        else if (data instanceof LobbyJoinRequest.Result) {
            if (((LobbyJoinRequest.Result) data).lobbyId != -1)
                showLobby(((LobbyJoinRequest.Result) data).lobbyId);
        }
        else if (data instanceof UserMessage) {
            if (inLobby) {
                UserMessage message = (UserMessage)data;
                switch (message.getType()) {
                    case UserJoined:
                        users.add(message.getName());
                        userList.setItems(users);
                        break;
                    case UserLeft:
                        users.removeValue(message.getName(), false);
                        userList.setItems(users);
                        break;
                }
            }
        }
        else if (data instanceof LobbyLeaveRequest.Result) {
            showLobbies();
        }
        else if (data instanceof LobbyDataRequest.Result) {
            LobbyDataRequest.Result result = (LobbyDataRequest.Result)data;
            lobbyName.setText(result.lobbyData.name);
            if (users == null)
                users = new Array<>();

            users.clear();
            users.addAll(result.users);
            userList.setItems(users);
        }
        else if (data instanceof Message) {
            switch (((Message) data).getType()) {
                case LobbyDestroyed:
                    showLobbies();
                    break;
            }
        }
        else if (data instanceof GameSetupRequest) {
            GameSetupRequest request = (GameSetupRequest)data;
            game.setNextScreen(new PlayScreen(game, client, request));
        }
        else if (data instanceof GameSetupData) {
            GameSetupData setupData = (GameSetupData)data;
            game.setNextScreen(new PlayScreen(game, client, setupData));
        }
    };

    public MultiplayerScreen(SEPGame game, int mapNumber, int numWorms) {
        this.game = game;

        this.mapNumber = mapNumber;
        this.numWorms = numWorms;
    }

    private void showLogin() {
        table.clearChildren();

        table.center();

        TextField textField = new TextField("", skin);
        table.add(textField).colspan(3);
        table.row();
        TextButton menu = new TextButton("Menu", skin);
        TextButton button = new TextButton("Login", skin);
        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setMenuScreen();
                client.disconnect();
                client = null;
            }
        });
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                client.send(new UserLoginRequest(textField.getText(), new String[] { "Worm1", "Worm2", "Worm3", "Worm4", "Worm5" }));
                super.clicked(event, x, y);
            }
        });
        table.add(menu, button);
        //table.add("Waiting for players...");
    }

    private void showLobbies() {
        if (chatWindow != null) {
            inputMultiplexer.removeProcessor(chatWindow.getInputProcessor());
            chatWindow.dispose();
            chatWindow = null;
        }

        table.clearChildren();

        table.center();

        lobbyList = new List<>(skin);
        table.add(lobbyList);
        table.row();
        TextField field = new TextField("", skin);
        table.add(field).colspan(3).width(300);
        table.row();
        TextButton menu = new TextButton("Menu", skin);
        TextButton join = new TextButton("Join", skin);
        TextButton create = new TextButton("Create", skin);
        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setMenuScreen();
                client.disconnect();
                client = null;
            }
        });
        join.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LobbyJoinRequest request = new LobbyJoinRequest();
                request.lobbyId = lobbyList.getSelected().id;
                client.send(request);
            }
        });
        create.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String[] split = field.getText().split(",");
                LobbyCreateRequest request = new LobbyCreateRequest(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                client.send(request);
            }
        });
        table.add(menu, join, create);

        client.send(new LobbyListRequest());
    }

    private void showLobby(int lobbyId) {
        inLobby = true;

        LobbyDataRequest request = new LobbyDataRequest();
        request.lobbyId = lobbyId;
        client.send(request);

        table.clearChildren();

        table.center();

        lobbyName = table.add("").getActor();
        table.row();

        userList = new List<>(skin);
        table.add(userList);
        table.row();

        TextButton leave = new TextButton("Leave", skin);
        startButton = new TextButton("Start", skin);
        leave.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LobbyLeaveRequest request1 = new LobbyLeaveRequest();
                client.send(request1);
            }
        });
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StartMatchRequest startMatchRequest = new StartMatchRequest();
                client.send(startMatchRequest);
            }
        });
        table.add(leave, startButton);

        chatWindow = new ChatWindow(client);
        chatWindow.initialize();
        inputMultiplexer.addProcessor(chatWindow.getInputProcessor());
    }

    private void showError() {
        table.clearChildren();

        table.center();

        table.add("Connection to server failed");
        table.row();

        TextButton back = new TextButton("Back", skin);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setMenuScreen();
            }
        });
        table.add(back);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        //stage.setDebugAll(true);
        skin = new Skin(Gdx.files.internal("sgx-ui/sgx-ui.json"));

        table = new Table(skin);

        stage.addActor(table);
        table.center();
        table.add("Connecting to server...");
        table.setFillParent(true);

        client = new NetworkClient("localhost");
        client.setConnectionListener((client, result) -> {
            switch (result) {
                case NetworkClient.RESULT_CONNECTION_SUCCESS:
                    showLogin();
                    break;
                case NetworkClient.RESULT_CONNECTION_FAILED:
                    showError();
                    break;
            }
        });
        client.registerDataHandler(dataHandler);
        client.connect();

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (chatWindow != null) {
            chatWindow.render(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();

        client.unregisterDataHandler(dataHandler);
        //client.unregisterDataHandler(Message.class, messageHandler);
        //client.stop();
    }
}
