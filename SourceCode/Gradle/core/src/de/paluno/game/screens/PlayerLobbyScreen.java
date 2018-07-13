package de.paluno.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.paluno.game.Assets;
import de.paluno.game.DataHandler;
import de.paluno.game.NetworkClient;
import de.paluno.game.SEPGame;
import de.paluno.game.interfaces.*;

public class PlayerLobbyScreen extends ScreenAdapter implements Loadable {
    private ScrollPane scrollPane;
    private List<String> list;
    private Array<String> usersArray;
    private Skin skin;
    private Stage stage;
    private SEPGame game;
    private TextButton textButtonMenu, textButtonPlay;
    private ElementGUI elementGUI;
    private TextField textField;
    private Table table;
    private Image imageMap, imageNumWorms;
    private Container<Table> chatWindowContainer;
    private ChatWindow chatWindow;
    private LobbyScreen lobbyScreen;
    private int numWorms, mapNumber, lobbyID;
    private NetworkClient client;
    private DataHandler dataHandler = new DataHandler() {
        @Override
        public void handleData(NetworkClient client, Object data) {
            if (data instanceof LobbyDataRequest.Result) {
                LobbyDataRequest.Result result = (LobbyDataRequest.Result) data;
                if (usersArray == null)
                    usersArray = new Array<>();

                usersArray.clear();
                usersArray.addAll(result.users);
                list.setItems(usersArray);

                AssetDescriptor<Texture> mapTexture = null;
                switch (result.lobbyData.mapNumber) {
                    case 0:
                        mapTexture = Assets.map1Thumbnail;
                        break;
                    case 1:
                        mapTexture = Assets.map2Thumbnail;
                        break;
                    case 2:
                        mapTexture = Assets.map3Thumbnail;
                        break;
                    case 3:
                        mapTexture = Assets.map4Thumbnail;
                        break;
                }
                imageMap.setDrawable(new TextureRegionDrawable(new TextureRegion(game.getAssetManager().get(mapTexture))));

                AssetDescriptor<Texture> wormTexture = null;
                switch (result.lobbyData.numWorms) {
                    case 1:
                        wormTexture = Assets.worms1Button;
                        break;
                    case 2:
                        wormTexture = Assets.worms2Button;
                        break;
                    case 3:
                        wormTexture = Assets.worms3Button;
                        break;
                    case 4:
                        wormTexture = Assets.worms4Button;
                        break;
                    case 5:
                        wormTexture = Assets.worms5Button;
                        break;
                }
                imageNumWorms.setDrawable(new TextureRegionDrawable(new TextureRegion(game.getAssetManager().get(wormTexture))));

                if (result.lobbyData.creatingUserId != client.getClientId())
                    textButtonPlay.setVisible(false);

            } else if (data instanceof LobbyLeaveRequest.Result) {
                game.setLobbyScreen(client);

            } else if (data instanceof UserMessage) {
                UserMessage message = (UserMessage) data;
                switch (message.getType()) {
                    case UserJoined:
                        usersArray.add(message.getName());
                        list.setItems(usersArray);
                        break;
                    case UserLeft:
                        usersArray.removeValue(message.getName(), false);
                        list.setItems(usersArray);
                        break;
                }

            } else if (data instanceof Message) {
                switch (((Message) data).getType()) {
                    case LobbyDestroyed:
                        game.setLobbyScreen(client);
                        break;
                }
            }
            else if (data instanceof GameSetupRequest) {
                game.setNextScreen(new PlayScreen(game, client, (GameSetupRequest)data));
            }
            else if (data instanceof GameSetupData) {
                game.setNextScreen(new PlayScreen(game, client, (GameSetupData)data));
            }
        }
    };


    public PlayerLobbyScreen(SEPGame game, NetworkClient client, int lobbyID) {
        this.game = game;
        this.client = client;
        this.lobbyID = lobbyID;
        elementGUI = new ElementGUI();
        chatWindow = new ChatWindow(client);
        lobbyScreen = new LobbyScreen(game, client);

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        client.registerDataHandler(dataHandler);
        LobbyDataRequest lobbyDataRequest = new LobbyDataRequest();
        lobbyDataRequest.lobbyId = lobbyID;
        client.send(lobbyDataRequest);

        stage = new Stage(new ScreenViewport());
        table = new Table(skin);
        chatWindowContainer = new Container<>();
        chatWindow.initialize();
        chatWindowContainer.setActor(chatWindow.getTable());
        table.setFillParent(true);
        table.setBackground(new Image(game.getAssetManager().get(Assets.menuBackground)).getDrawable());

        skin = elementGUI.getSkin();
        textButtonMenu = new TextButton("Lobby", skin);
        textButtonMenu.setPosition(380, 190);
        textButtonMenu.setSize(200, 60);
        textButtonMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                client.send(new LobbyLeaveRequest());
            }
        });

        textButtonPlay = elementGUI.createTextButton("Spielen");
        textButtonPlay.setPosition(380, 85);
        textButtonPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                client.send(new StartMatchRequest());
            }
        });

        list = new List<String>(skin);
        String[] strings = new String[5];
        for (int i = 1, k = 0; i <= strings.length; i++) {
            strings[k++] = "Player: " + i;

        }
        list.setItems(strings);
        scrollPane = new ScrollPane(list, skin);

        scrollPane.setBounds(360, 380, 300, 280);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setTransform(true);
        scrollPane.setScale(1f);


        imageMap = new Image(game.getAssetManager().get(Assets.map1Thumbnail));
        imageNumWorms = new Image(game.getAssetManager().get(Assets.worms1Button));
        imageMap.setPosition(860, 500);
        imageNumWorms.setPosition(960, 400);
        chatWindowContainer.setPosition(980, 200);


        textField = new TextField("Chat", skin);
        textField.setDisabled(true);
        textField.setSize(506, 50);
        textField.setPosition(727, 318);


        stage.addActor(table);
        stage.addActor(textButtonPlay);
        stage.addActor(textButtonMenu);
        stage.addActor(scrollPane);
        stage.addActor(imageMap);
        stage.addActor(imageNumWorms);
        stage.addActor(chatWindowContainer);
        stage.addActor(textField);


        list.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println(list.getSelectedIndex());
            }
        });
        Gdx.input.setInputProcessor(stage);

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.act(delta);
        stage.draw();
    }


    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void hide() {
        client.unregisterDataHandler(dataHandler);
    }

    @Override
    public boolean load(AssetManager manager) {
        Assets.loadAssets(manager, Assets.LobbyScreenAssets);
        return false;
    }
}