package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.paluno.game.Assets;
import de.paluno.game.DataHandler;
import de.paluno.game.EventManager;
import de.paluno.game.NetworkClient;
import de.paluno.game.SEPGame;
import de.paluno.game.interfaces.UserLoginRequest;

import java.util.*;


public class LoginScreen extends ScreenAdapter implements Loadable {
    private SEPGame game;
    private Stage stage;
    private ElementGUI elementGUI;
    private TextField textFieldWorm1, textFieldWorm2, textFieldWorm3, textFieldWorm4, textFieldWorm5, textFieldUsername;
    private TextButton textButtonPlayerNum, textButtonLogin, textButtonAutoFill;
    private Table tableBackground, tableTextField, tableMap, table4Worm, tableTextButton;
    private Image imageBackground;
    private NetworkClient client;
    public ImageButton buttonMap1, buttonMap2, buttonMap3, buttonMap4,
            buttonWorm1, buttonWorm2, buttonWorm3, buttonWorm4, buttonWorm5;
    private ArrayList<String> names = new ArrayList<>();
    private int mapNumber, numWorms;

    int playerNum = 1;

    private DataHandler dataHandler = new DataHandler() {
        @Override
        public void handleData(NetworkClient client, Object data) {
            if (data instanceof UserLoginRequest.Result) {
                game.setLobbyScreen(client);
            }
        }
    };

    private ArrayList<String> randomPlayerNames = new ArrayList<>(Arrays.asList("Julian", "Tarik", "Ibo", "Jan", "Steve",
            "Messi", "Ronaldo", "Kroos", "Neuer", "Ibrahimovic",
            "Batman", "Hulk", "Superman", "Spiderman", "Ant-Man",
            "Spongebob", "Patrick", "Sandy", "Plankton", "Mr. Krabs",
            "The Undertaker", "Rey Mysterio", "Jeff Hardy", "Hornswoggle", "The Rock",
            "Charlie Sheen", "Dexter", "Michael Scofield", "Barney Stinson", "Walter White",
            "Chris Brown", "Drake", "Michael Jackson", "Trey Songz", "Eminem"));

    public LoginScreen(SEPGame game, NetworkClient client) {
        super();
        this.game = game;
        this.client = client;
        stage = new Stage(new ScreenViewport());
        elementGUI = new ElementGUI();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public boolean load(AssetManager manager) {
  //    Assets.loadAssets(manager, Assets.PlayerMenuScreenAssets);
        Assets.loadAssets(manager, Assets.Music);
        Assets.loadAssets(manager, Assets.MenuScreenAssets);
        return false;
    }

    @Override
    public void show() {
        tableBackground = new Table();
        tableTextField = new Table();
        tableMap = new Table();
        table4Worm = new Table();
        tableTextButton = new Table();
        if (client != null) {
            client.registerDataHandler(dataHandler);
        }
        tableBackground = new Table(elementGUI.getSkin());
        imageBackground = elementGUI.createBackground(game.getAssetManager().get(Assets.menuBackground));
        tableBackground.setBackground(imageBackground.getDrawable());
        tableBackground.setFillParent(true);

        textFieldWorm1 = elementGUI.createTextField("Wurm 1");
        textFieldWorm2 = elementGUI.createTextField("Wurm 2");
        textFieldWorm3 = elementGUI.createTextField("Wurm 3");
        textFieldWorm4 = elementGUI.createTextField("Wurm 4");
        textFieldWorm5 = elementGUI.createTextField("Wurm 5");
        textFieldUsername = elementGUI.createTextField("Username");
        textButtonAutoFill = elementGUI.createTextButton("Auto-Fill");
        textButtonAutoFill.addListener(new ClickListener() {
            int i = 0;

            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                if (i < randomPlayerNames.size()) {
                    textFieldWorm1.setText(randomPlayerNames.get(0 + i));
                    textFieldWorm2.setText(randomPlayerNames.get(1 + i));
                    textFieldWorm3.setText(randomPlayerNames.get(2 + i));
                    textFieldWorm4.setText(randomPlayerNames.get(3 + i));
                    textFieldWorm5.setText(randomPlayerNames.get(4 + i));
                    i += 5;
                }else{
                    i = 0;
                }
            }
        });
        textButtonPlayerNum = elementGUI.createTextButton("Spieler " + playerNum);
        textButtonPlayerNum.setVisible(false);


        textButtonPlayerNum.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                if (playerNum < 6) {
                    playerNum++;
                    names.add(textFieldUsername.getText());
                    names.add(textFieldWorm1.getText());
                    names.add(textFieldWorm2.getText());
                    names.add(textFieldWorm3.getText());
                    names.add(textFieldWorm4.getText());
                    names.add(textFieldWorm5.getText());
                    if (playerNum <= 5) {
                        textButtonPlayerNum.setText("Spieler " + playerNum);
                    }

                    String res = String.join(" " , names);
                    System.out.println(res);
                } else {
                    playerNum = 1;
                    textButtonPlayerNum.setText(playerNum + " Spieler");
                    names.clear();
                }
            }
        });

        TextButton menuButton = elementGUI.createTextButton("Menu");
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                client.disconnect();
                game.setModiScreen();
            }
        });
        textButtonLogin = elementGUI.createTextButton("Einloggen");

        textButtonPlayerNum.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                super.clicked(event, x, y);
            }
        });

        textButtonLogin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                if (client != null) {
                    client.send(new UserLoginRequest(textFieldUsername.getText(), new String[]{textFieldWorm1.getText(),
                            textFieldWorm2.getText(), textFieldWorm3.getText(), textFieldWorm4.getText(), textFieldWorm5.getText()}));
                }
            }
        });

        buttonMap1 = elementGUI.createButton(game.getAssetManager().get(Assets.map1Thumbnail));
        buttonMap1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                elementGUI.setSelectedImageButton(buttonMap1);
                mapNumber = 0;
                System.out.println("Map1 Clicked");
            }
        });


        buttonMap2 = elementGUI.createButton(game.getAssetManager().get(Assets.map2Thumbnail));
        buttonMap2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                elementGUI.setSelectedImageButton(buttonMap2);
                mapNumber = 1;
                System.out.println("Map2 Clicked");
            }
        });


        buttonMap3 = elementGUI.createButton(game.getAssetManager().get(Assets.map3Thumbnail));
        buttonMap3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                elementGUI.setSelectedImageButton(buttonMap3);

                mapNumber = 2;
                System.out.println("Map3 Clicked");
            }
        });


        buttonMap4 = elementGUI.createButton(game.getAssetManager().

                get(Assets.map4Thumbnail));
        buttonMap4.addListener(new ClickListener() {


            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                elementGUI.setSelectedImageButton(buttonMap4);

                mapNumber = 3;
                System.out.println("Map4 Clicked");
            }
        });


        buttonWorm1 = elementGUI.createButton(game.getAssetManager().get(Assets.worms1Button));
        buttonWorm1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                elementGUI.setSelectedImageButton2(buttonWorm1);
                numWorms = 1;
                System.out.println("Worm 1 Clicked");
            }
        });


        buttonWorm2 = elementGUI.createButton(game.getAssetManager().get(Assets.worms2Button));
        buttonWorm2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                elementGUI.setSelectedImageButton2(buttonWorm2);
                numWorms = 2;
                System.out.println("Worm 2 Clicked");
            }
        });

        buttonWorm3 = elementGUI.createButton(game.getAssetManager().get(Assets.worms3Button));
        buttonWorm3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                elementGUI.setSelectedImageButton2(buttonWorm3);
                numWorms = 3;
                System.out.println("Worm 3 Clicked");
            }
        });

        buttonWorm4 = elementGUI.createButton(game.getAssetManager().get(Assets.worms4Button));
        buttonWorm4.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                elementGUI.setSelectedImageButton2(buttonWorm4);
                numWorms = 4;
                System.out.println("Worm 4 Clicked");
            }
        });

        buttonWorm5 = elementGUI.createButton(game.getAssetManager().get(Assets.worms5Button));
        buttonWorm5.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                elementGUI.setSelectedImageButton2(buttonWorm5);
                numWorms = 5;
                System.out.println("Worm 5 Clicked");
            }
        });


        tableTextField.add(textFieldUsername).size(200, 50).colspan(5).row();
        tableTextField.add(textFieldWorm1).size(200, 50);
        tableTextField.add(textFieldWorm2).size(200, 50);
        tableTextField.add(textFieldWorm3).size(200, 50);
        tableTextField.add(textFieldWorm4).size(200, 50);
        tableTextField.add(textFieldWorm5).size(200, 50).row();
        tableTextField.add(textButtonAutoFill).colspan(5);

        tableBackground.center();
        tableBackground.add(tableTextField);
        tableBackground.row();

        Table menuButtonsTable = new Table();
        menuButtonsTable.row().size(200, 60).pad(10);
        menuButtonsTable.add(menuButton, textButtonLogin);

        tableBackground.add(menuButtonsTable);

        stage.addActor(tableBackground);

        elementGUI.setSelectedImageButton(buttonMap1);
        elementGUI.setSelectedImageButton2(buttonWorm1);


    }


    @Override
    public void render(float delta) {
        // clears screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        if (client != null) {
            client.unregisterDataHandler(dataHandler);
        }
        stage.dispose();
    }


}
