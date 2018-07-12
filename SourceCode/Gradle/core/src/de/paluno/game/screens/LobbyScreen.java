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
import de.paluno.game.Assets;
import de.paluno.game.DataHandler;
import de.paluno.game.NetworkClient;
import de.paluno.game.SEPGame;
import de.paluno.game.interfaces.LobbyCreateRequest;
import de.paluno.game.interfaces.LobbyData;
import de.paluno.game.interfaces.LobbyJoinRequest;
import de.paluno.game.interfaces.LobbyListRequest;

import javax.xml.crypto.Data;

public class LobbyScreen extends ScreenAdapter implements Loadable {
    private ScrollPane scrollPane;
    private List<LobbyData> list;
    private Skin skin;
    private Stage stage;
    private SEPGame game;
    private TextButton textButtonJoinLobby, textButtonMenu, textButtonCreateLobby, textButtonStart;

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
        @Override
        public float getPrefHeight() {
            return 700;
        }

        @Override
        public float getPrefWidth() {
            return 700;
        }

        public LobbyDialog(String title, Skin skin) {
            super(title, skin);
        }
    }

    public LobbyScreen(SEPGame game, NetworkClient client) {
        elementGUI = new ElementGUI();
        this.client = client;
        stage = new Stage();
        this.game = game;
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

        buttonMap1 = elementGUI.createButton(game.getAssetManager().get(Assets.map1Thumbnail));
        buttonMap1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedMapButton(buttonMap1);
                mapNumber = 0;
                System.out.println("Map1 Clicked");
            }
        });


        buttonMap2 = elementGUI.createButton(game.getAssetManager().get(Assets.map2Thumbnail));
        buttonMap2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedMapButton(buttonMap2);
                mapNumber = 1;
                System.out.println("Map2 Clicked");
            }
        });


        buttonMap3 = elementGUI.createButton(game.getAssetManager().get(Assets.map3Thumbnail));
        buttonMap3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedMapButton(buttonMap3);

                mapNumber = 2;
                System.out.println("Map3 Clicked");
            }
        });


        buttonMap4 = elementGUI.createButton(game.getAssetManager().get(Assets.map4Thumbnail));
        buttonMap4.addListener(new ClickListener() {


            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedMapButton(buttonMap4);

                mapNumber = 3;
                System.out.println("Map4 Clicked");
            }
        });


        buttonWorm1 = elementGUI.createButton(game.getAssetManager().get(Assets.worms1Button), menuTable2);
        buttonWorm1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm1);
                numWorms = 1;
                System.out.println("Worm 1 Clicked");
            }
        });


        buttonWorm2 = elementGUI.createButton(game.getAssetManager().get(Assets.worms2Button), menuTable2);
        buttonWorm2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm2);
                numWorms = 2;
                System.out.println("Worm 2 Clicked");
            }
        });

        buttonWorm3 = elementGUI.createButton(game.getAssetManager().get(Assets.worms3Button), menuTable2);
        buttonWorm3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm3);
                numWorms = 3;
                System.out.println("Worm 3 Clicked");
            }
        });

        buttonWorm4 = elementGUI.createButton(game.getAssetManager().get(Assets.worms4Button), menuTable2);
        buttonWorm4.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm4);
                numWorms = 4;
                System.out.println("Worm 4 Clicked");
            }
        });

        buttonWorm5 = elementGUI.createButton(game.getAssetManager().get(Assets.worms5Button), menuTable2);
        buttonWorm5.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm5);
                numWorms = 5;
                System.out.println("Worm 5 Clicked");
            }
        });


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
                //game.setPlayerLobbyScreen(mapNumber, numWorms);
            }
        });


        list = new List<LobbyData>(skin);
        String[] strings = new String[5];
        for (int i = 1, k = 0; i <= strings.length; i++) {
            strings[k++] = "Lobby " + i + ": ";

        }


        scrollPane = new ScrollPane(list, skin);
        scrollPane.setBounds(550, 300, 500, 300);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setTransform(true);
        scrollPane.setScale(1f);

        tableBackground.setFillParent(true);
        imageBackground = elementGUI.createBackground(game.getAssetManager().get(Assets.menuBackground));
        tableBackground.setBackground(imageBackground.getDrawable());

        stage.addActor(tableBackground);
        stage.addActor(textButtonCreateLobby);
        stage.addActor(textButtonJoinLobby);
        stage.addActor(textButtonMenu);
        stage.addActor(scrollPane);

        menuTable.padLeft(30);
        menuTable.padTop(50);
        menuTable.add(buttonMap1);
        menuTable.add(buttonMap2);
        menuTable.row();
        menuTable.add(buttonMap3);
        menuTable.add(buttonMap4);
        menuTable.left().top();

        menuTable.setFillParent(true);
        menuTable2.setPosition(330, 170);


        textButtonStart = elementGUI.createTextButton("Erstellen");
        textButtonStart.addListener(new ClickListener() {
            int i = 0;

            @Override
            public void clicked(InputEvent event, float x, float y) {
                lobbyDialog.setVisible(false);
                client.send(new LobbyCreateRequest(textField.getText(), mapNumber, numWorms));
            }
        });
        textField = elementGUI.createTextField("Lobbyname eingeben");
        lobbyDialog.addActor(menuTable);
        lobbyDialog.addActor(menuTable2);
        lobbyDialog.addActor(textButtonStart);
        lobbyDialog.addActor(textField);
        textButtonStart.setPosition(230, 50);
        textField.setPosition(215, 235);


        list.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println(list.getSelectedIndex());
            }
        });

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
        Assets.loadAssets(manager, Assets.LobbyScreenAssets);
        return false;
    }
}