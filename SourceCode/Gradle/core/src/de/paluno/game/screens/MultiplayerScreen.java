package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.paluno.game.DataHandler;
import de.paluno.game.NetworkClient;
import de.paluno.game.SEPGame;
import de.paluno.game.interfaces.GameSetupData;
import de.paluno.game.interfaces.GameSetupRequest;

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

    private DataHandler<GameSetupRequest> gameSetupRequestHandler = new DataHandler<GameSetupRequest>() {
        @Override
        public void handleData(NetworkClient client, GameSetupRequest data) {
            Gdx.app.postRunnable(() -> {
                game.setNextScreen(new PlayScreen(game, mapNumber, numWorms, client, data));
            });
        }
    };

    private DataHandler<GameSetupData> gameSetupDataHandler = new DataHandler<GameSetupData>() {
        @Override
        public void handleData(NetworkClient client, GameSetupData data) {
            Gdx.app.postRunnable(() -> {
                game.setNextScreen(new PlayScreen(game, client, data));
            });
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

        table.add("Waiting for players...");
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

        client = new NetworkClient("178.202.241.10");
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
        client.connect();
        client.registerDataHandler(GameSetupRequest.class, gameSetupRequestHandler);
        client.registerDataHandler(GameSetupData.class, gameSetupDataHandler);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();

        client.unregisterDataHandler(GameSetupRequest.class, gameSetupRequestHandler);
        client.unregisterDataHandler(GameSetupData.class, gameSetupDataHandler);
        //client.unregisterDataHandler(MessageData.class, messageHandler);
        //client.stop();
    }
}
