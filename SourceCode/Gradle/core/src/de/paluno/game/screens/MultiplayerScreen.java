package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
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
                game.setNextScreen(new PlayScreen(game, 0, 1, client, data));
            });
        }
    };

    public MultiplayerScreen(SEPGame game, int mapNumber, int numWorms) {
        this.game = game;

        this.mapNumber = mapNumber;
        this.numWorms = numWorms;
    }

    @Override
    public void show() {
        client = new NetworkClient("localhost");
        client.connect();
        client.registerDataHandler(GameSetupRequest.class, gameSetupRequestHandler);
        client.registerDataHandler(GameSetupData.class, gameSetupDataHandler);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void hide() {
        client.unregisterDataHandler(GameSetupRequest.class, gameSetupRequestHandler);
        client.unregisterDataHandler(GameSetupData.class, gameSetupDataHandler);
        //client.unregisterDataHandler(MessageData.class, messageHandler);
        //client.stop();
    }
}
