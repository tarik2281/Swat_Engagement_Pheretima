package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.paluno.game.Assets;
import de.paluno.game.DataHandler;
import de.paluno.game.NetworkClient;
import de.paluno.game.SEPGame;
import de.paluno.game.interfaces.LobbyCreateRequest;
import de.paluno.game.interfaces.LobbyData;
import de.paluno.game.interfaces.LobbyJoinRequest;
import de.paluno.game.interfaces.LobbyListRequest;

public class LobbyScreen extends ScreenAdapter implements Loadable {
    private ScrollPane scrollPane;
    private List<LobbyData> list;
    private Skin skin;
    private Stage stage;
    private SEPGame game;
    private TextButton textButtonJoinLobby, textButtonMenu, textButtonCreateLobby;

    private ElementGUI elementGUI;
    private LobbyDialog lobbyDialog;
    private TextField textField;
    private Table tableBackground;
    private Image imageBackground;

    private Timer.Task updateTimer = new Timer.Task() {
        @Override
        public void run() {
            client.send(new LobbyListRequest());
        }
    };

    public ImageButton buttonMap1, buttonMap2, buttonMap3, buttonMap4,
            buttonWorm1, buttonWorm2, buttonWorm3, buttonWorm4, buttonWorm5;

    private ImageButton selectedWormButton;
    private ImageButton selectedMapButton;

    private NetworkClient client;
    private DataHandler dataHandler = new DataHandler() {
        @Override
        public void handleData(NetworkClient client, Object data) {
            if (data instanceof LobbyListRequest.Result) {
                list.setItems(((LobbyListRequest.Result) data).lobbies);

                Timer.schedule(updateTimer, 10.0f);
            } else if (data instanceof LobbyCreateRequest.Result) {
                game.setNextScreen(new PlayerLobbyScreen(game, client, ((LobbyCreateRequest.Result) data).lobbyId));
            } else if (data instanceof LobbyJoinRequest.Result) {
                if (((LobbyJoinRequest.Result) data).success) {
                    game.setNextScreen(new PlayerLobbyScreen(game, client, ((LobbyJoinRequest.Result) data).lobbyId));
                }
            }
        }
    };


    private int mapNumber;

    public int getMapNumber() {
        return mapNumber;
    }

    public int getNumWorms() {
        return numWorms;
    }

    private int numWorms;
    private Table menuTable, menuTable2;


    public class LobbyDialog extends Dialog {



        public LobbyDialog(String title, Skin skin) {
            super(title, skin);
        }

        @Override
        protected void result(Object object) {
            if ("create".equals(object)) {
                if (!textField.getText().isEmpty())
                    client.send(new LobbyCreateRequest(textField.getText(), mapNumber, numWorms));
            }
        }
    }

    public LobbyScreen(SEPGame game, NetworkClient client) {
        elementGUI = new ElementGUI();
        this.client = client;
        stage = new Stage(new ScreenViewport());
        this.game = game;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        client.send(new LobbyListRequest());
        client.registerDataHandler(dataHandler);


        tableBackground = new Table();
        skin = elementGUI.getSkin();
        menuTable = new Table(skin);
        menuTable2 = new Table(skin);
        lobbyDialog = new LobbyDialog("Lobby erstellen", skin);

        TextButton cancelButton = new TextButton("Abbrechen", skin);
        TextButton createButton = new TextButton("Erstellen", skin);



        lobbyDialog.getButtonTable().add(cancelButton).size(200, 60).padBottom(10.0f);
        lobbyDialog.getButtonTable().add(createButton).size(200, 60).padBottom(10.0f);
        lobbyDialog.setObject(cancelButton, "cancel");
        lobbyDialog.setObject(createButton, "create");


        buttonMap1 = elementGUI.createButton(game.getAssetManager().get(Assets.map1Thumbnail));
        buttonMap1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedMapButton(buttonMap1);
                mapNumber = 0;
                //System.out.println("Map1 Clicked");
            }
        });

        buttonMap2 = elementGUI.createButton(game.getAssetManager().get(Assets.map2Thumbnail));
        buttonMap2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedMapButton(buttonMap2);
                mapNumber = 1;
                //System.out.println("Map2 Clicked");
            }
        });


        buttonMap3 = elementGUI.createButton(game.getAssetManager().get(Assets.map3Thumbnail));
        buttonMap3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedMapButton(buttonMap3);

                mapNumber = 2;
                //System.out.println("Map3 Clicked");
            }
        });


        buttonMap4 = elementGUI.createButton(game.getAssetManager().get(Assets.map4Thumbnail));
        buttonMap4.addListener(new ClickListener() {


            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedMapButton(buttonMap4);
                mapNumber = 3;
            }
        });


        buttonWorm1 = elementGUI.createButton(game.getAssetManager().get(Assets.worms1Button));
        buttonWorm1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm1);
                numWorms = 1;
            }
        });


        buttonWorm2 = elementGUI.createButton(game.getAssetManager().get(Assets.worms2Button));
        buttonWorm2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm2);
                numWorms = 2;
            }
        });

        buttonWorm3 = elementGUI.createButton(game.getAssetManager().get(Assets.worms3Button));
        buttonWorm3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm3);
                numWorms = 3;
            }
        });

        buttonWorm4 = elementGUI.createButton(game.getAssetManager().get(Assets.worms4Button));
        buttonWorm4.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm4);
                numWorms = 4;
            }
        });

        buttonWorm5 = elementGUI.createButton(game.getAssetManager().get(Assets.worms5Button));
        buttonWorm5.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm5);
                numWorms = 5;
            }
        });

        textField = elementGUI.createTextField("");
        textField.setMessageText("Lobbyname eingeben");

        lobbyDialog.getContentTable().center();
        lobbyDialog.getContentTable().add(buttonMap1, buttonMap2);
        lobbyDialog.getContentTable().row();
        lobbyDialog.getContentTable().add(buttonMap3, buttonMap4);
        lobbyDialog.getContentTable().row();
        lobbyDialog.getContentTable().add(new Table()).colspan(2).getActor().add(buttonWorm1, buttonWorm2, buttonWorm3, buttonWorm4, buttonWorm5);
        lobbyDialog.getContentTable().row();
        lobbyDialog.getContentTable().add(textField).colspan(2).size(200, 50);


        textButtonMenu = elementGUI.createTextButton("Menu");
        textButtonMenu.setPosition(400, 50);
        textButtonMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setModiScreen();
            }
        });

        textButtonCreateLobby = elementGUI.createTextButton("Lobby erstellen");
        textButtonCreateLobby.setPosition(700, 50);
        textButtonCreateLobby.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                lobbyDialog.setVisible(true);
                lobbyDialog.show(stage);
            }
        });

        textButtonJoinLobby = elementGUI.createTextButton("Lobby beitreten");
        textButtonJoinLobby.setPosition(1000, 50);
        textButtonJoinLobby.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LobbyData data = list.getSelected();
                if (data != null) {
                    LobbyJoinRequest request = new LobbyJoinRequest();
                    request.lobbyId = data.id;
                    client.send(request);
                }
            }
        });


        list = new List<>(skin);

        scrollPane = new ScrollPane(list, skin);
        scrollPane.setBounds(550, 300, 500, 300);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setTransform(true);
        scrollPane.setScale(1f);

        tableBackground.setFillParent(true);
        imageBackground = elementGUI.createBackground(game.getAssetManager().get(Assets.menuBackground));
        tableBackground.setBackground(imageBackground.getDrawable());

        tableBackground.center();
        tableBackground.add(scrollPane).colspan(3).size(500, 300);
        tableBackground.row().size(200, 60).pad(20);
        tableBackground.add(textButtonMenu, textButtonCreateLobby, textButtonJoinLobby);

        stage.addActor(tableBackground);

        Gdx.input.setInputProcessor(stage);

        setSelectedMapButton(buttonMap1);
        mapNumber = 1;
        setSelectedWormButton(buttonWorm1);
        numWorms = 1;
    }


    public void setSelectedWormButton(ImageButton button) {
        if (selectedWormButton != null)
            selectedWormButton.setColor(1.0f, 1.0f, 1.0f, 0.4f);

        selectedWormButton = button;

        if (selectedWormButton != null)
            selectedWormButton.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void setSelectedMapButton(ImageButton button) {
        //GREY Transparent 1,1,1,0.4
        if (selectedMapButton != null)
            selectedMapButton.setColor(1.0f, 1.0f, 1.0f, 0.4f);

        selectedMapButton = button;
        // WHITE Transparent 1,1,1,1
        if (selectedMapButton != null)
            selectedMapButton.setColor(1.0f, 1.0f, 1.0f, 1.0f);
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

        updateTimer.cancel();
    }

    @Override
    public boolean load(AssetManager manager) {
        Assets.loadAssets(manager, Assets.MenuScreenAssets);
        return false;
    }
}