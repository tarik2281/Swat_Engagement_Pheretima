package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import de.paluno.game.Assets;
import de.paluno.game.DataHandler;
import de.paluno.game.NetworkClient;
import de.paluno.game.SEPGame;
import de.paluno.game.interfaces.LobbyData;
import de.paluno.game.interfaces.UserLoginRequest;
import de.paluno.game.interfaces.UserName;

import java.util.*;


public class LocalScreen extends ScreenAdapter implements Loadable {
    private SEPGame game;
    private Stage stage;
    private ElementGUI elementGUI;
    private TextField textFieldWorm1, textFieldWorm2, textFieldWorm3, textFieldWorm4, textFieldWorm5, textFieldUsername;
    private TextButton textButtonPlayerNum, textButtonSpielen, textButtonAutoFill,textButtonAdd,textButtonDelete;;
    private Table tableBackground, tableTextField, tableMap, table4Worm, tableTextButton, tableTextButtonPlayer;
    private Image imageBackground;
    private NetworkClient client;
    public ImageButton buttonMap1, buttonMap2, buttonMap3, buttonMap4,
            buttonWorm1, buttonWorm2, buttonWorm3, buttonWorm4, buttonWorm5;
    //libgdx Array
    private Array<UserName> names = new Array<>();
    private int mapNumber, numWorms;
    int playerNum = 1;
    private ScrollPane scrollPane;
    private List<UserName> list;

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
            "Chris Brown", "Drake", "Frank Ocean", "Trey Songz", "Eminem",
            "Rihanna", "Beyonce", "Amy Winehouse", "Britney Spears", "J-Lo"));

    public LocalScreen(SEPGame game) {
        super();
        this.game = game;
        stage = new Stage();
        elementGUI = new ElementGUI();
        Gdx.input.setInputProcessor(stage);

    }


    @Override
    public boolean load(AssetManager manager) {
        Assets.loadAssets(manager, Assets.PlayerMenuScreenAssets);

        return false;
    }

    private void updateTextFields() {
        textFieldUsername.setText(list.getSelected().getUserName());
        textFieldWorm1.setText(list.getSelected().getWormNames()[0]);
        textFieldWorm2.setText(list.getSelected().getWormNames()[1]);
        textFieldWorm3.setText(list.getSelected().getWormNames()[2]);
        textFieldWorm4.setText(list.getSelected().getWormNames()[3]);
        textFieldWorm5.setText(list.getSelected().getWormNames()[4]);
    }

    @Override
    public void show() {
        names.add(new UserName("Spieler 1", new String[] { "Wurm 1", "Wurm 2", "Wurm 3", "Wurm 4", "Wurm 5"}));
        names.add(new UserName("Spieler 2", new String[] { "Wurm 1", "Wurm 2", "Wurm 3", "Wurm 4", "Wurm 5"}));

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

        textFieldWorm1 = elementGUI.createTextField("Worm 1");
        textFieldWorm1.addListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                list.getSelected().getWormNames()[0] = textFieldWorm1.getText();
                return true;
            }
        });
        textFieldWorm2 = elementGUI.createTextField("Worm 2");
        textFieldWorm2.addListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                list.getSelected().getWormNames()[1] = textFieldWorm2.getText();
                return true;
            }
        });
        textFieldWorm3 = elementGUI.createTextField("Worm 3");
        textFieldWorm3.addListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                list.getSelected().getWormNames()[2] = textFieldWorm3.getText();
                return true;
            }
        });
        textFieldWorm4 = elementGUI.createTextField("Worm 4");
        textFieldWorm4.addListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                list.getSelected().getWormNames()[3] = textFieldWorm4.getText();
                return true;
            }
        });
        textFieldWorm5 = elementGUI.createTextField("Worm 5");
        textFieldWorm5.addListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                list.getSelected().getWormNames()[4] = textFieldWorm5.getText();
                return true;
            }
        });
        textFieldUsername = elementGUI.createTextField("Username");
        textFieldUsername.addListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                list.getSelected().setUserName(textFieldUsername.getText());
                list.setItems(names);
                return true;
            }
        });
        textButtonAutoFill = elementGUI.createTextButton("Auto-Fill");
        textButtonAutoFill.addListener(new ClickListener() {
            int i = 0;

            @Override
            public void clicked(InputEvent event, float x, float y) {
                    UserName name = list.getSelected();
                    name.getWormNames()[0] = randomPlayerNames.get(0 + i);
                    name.getWormNames()[1] = randomPlayerNames.get(1 + i);
                    name.getWormNames()[2] = randomPlayerNames.get(2 + i);
                    name.getWormNames()[3] = randomPlayerNames.get(3 + i);
                    name.getWormNames()[4] = randomPlayerNames.get(4 + i);
                    updateTextFields();
                    i += 5;
                    if (i >= randomPlayerNames.size())
                        i = 0;
            }
        });
        textButtonPlayerNum = elementGUI.createTextButton("Menu");
        textButtonPlayerNum.setVisible(false);


        textButtonPlayerNum.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        textButtonSpielen = elementGUI.createTextButton("Spielen");

        textButtonPlayerNum.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });

        textButtonSpielen.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (client != null) {
                    client.send(new UserLoginRequest(textFieldUsername.getText(), new String[]{textFieldWorm1.getText(),
                            textFieldWorm2.getText(), textFieldWorm3.getText(), textFieldWorm4.getText(), textFieldWorm5.getText()}));
                    //game.setLobbyScreen();
                } else {
                    game.setPlayScreen(mapNumber, numWorms, names);
                }
            }
        });

        buttonMap1 = elementGUI.createButton(game.getAssetManager().get(Assets.map1Thumbnail));
        buttonMap1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                elementGUI.setSelectedImageButton(buttonMap1);
                mapNumber = 0;
                System.out.println("Map1 Clicked");
            }
        });


        buttonMap2 = elementGUI.createButton(game.getAssetManager().get(Assets.map2Thumbnail));
        buttonMap2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                elementGUI.setSelectedImageButton(buttonMap2);
                mapNumber = 1;
                System.out.println("Map2 Clicked");
            }
        });


        buttonMap3 = elementGUI.createButton(game.getAssetManager().get(Assets.map3Thumbnail));
        buttonMap3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
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
                elementGUI.setSelectedImageButton(buttonMap4);

                mapNumber = 3;
                System.out.println("Map4 Clicked");
            }
        });


        buttonWorm1 = elementGUI.createButton(game.getAssetManager().get(Assets.worms1Button));
        buttonWorm1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                elementGUI.setSelectedImageButton2(buttonWorm1);
                numWorms = 1;
                System.out.println("Worm 1 Clicked");
            }
        });


        buttonWorm2 = elementGUI.createButton(game.getAssetManager().get(Assets.worms2Button));
        buttonWorm2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                elementGUI.setSelectedImageButton2(buttonWorm2);
                numWorms = 2;
                System.out.println("Worm 2 Clicked");
            }
        });

        buttonWorm3 = elementGUI.createButton(game.getAssetManager().get(Assets.worms3Button));
        buttonWorm3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                elementGUI.setSelectedImageButton2(buttonWorm3);
                numWorms = 3;
                System.out.println("Worm 3 Clicked");
            }
        });

        buttonWorm4 = elementGUI.createButton(game.getAssetManager().get(Assets.worms4Button));
        buttonWorm4.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                elementGUI.setSelectedImageButton2(buttonWorm4);
                numWorms = 4;
                System.out.println("Worm 4 Clicked");
            }
        });

        buttonWorm5 = elementGUI.createButton(game.getAssetManager().get(Assets.worms5Button));
        buttonWorm5.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                elementGUI.setSelectedImageButton2(buttonWorm5);
                numWorms = 5;
                System.out.println("Worm 5 Clicked");
            }
        });


        tableTextField.setFillParent(true);
        tableTextField.add(textFieldUsername).size(200, 50).colspan(5).row();
        tableTextField.add(textFieldWorm1).size(200, 50);
        tableTextField.add(textFieldWorm2).size(200, 50);
        tableTextField.add(textFieldWorm3).size(200, 50);
        tableTextField.add(textFieldWorm4).size(200, 50);
        tableTextField.add(textFieldWorm5).size(200, 50).row();
        tableTextField.add(textButtonAutoFill).colspan(5);
        tableTextField.right().bottom().setY(250);

        tableMap.setFillParent(true);
        tableMap.add(buttonMap1);
        tableMap.add(buttonMap2);
        tableMap.add(buttonMap3);
        tableMap.add(buttonMap4);
        tableMap.setPosition(tableBackground.getPadX(), tableBackground.getPadY()+185);


        table4Worm.setFillParent(true);
        table4Worm.add(buttonWorm1);
        table4Worm.add(buttonWorm2);
        table4Worm.add(buttonWorm3);
        table4Worm.add(buttonWorm4);
        table4Worm.add(buttonWorm5).row();
        table4Worm.setPosition(tableMap.getPadX(), tableMap.getPadY()+80);

        tableTextButton.setFillParent(true);
        tableTextButton.add(textButtonPlayerNum).size(200, 60);
        tableTextButton.add(textButtonSpielen).size(200, 60);
        tableTextButton.bottom().setY(20);


        textButtonAdd = elementGUI.createTextButton("Hinzufügen");
        textButtonAdd.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (names.size < 5) {
                    names.add(new UserName("Spieler " + (names.size + 1), new String[] { "Wurm 1", "Wurm 2", "Wurm 3", "Wurm 4", "Wurm 5"}));
                    list.setItems(names);
                }
            }
        });

        textButtonDelete = elementGUI.createTextButton("Entfernen");
        textButtonDelete.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (names.size > 2) {
                    names.removeIndex(list.getSelectedIndex());
                    list.setItems(names);
                    updateTextFields();
                }
            }
        });
        tableTextButtonPlayer = new Table();
        tableTextButtonPlayer.setPosition(160,420);
        tableTextButtonPlayer.add(textButtonAdd);
        tableTextButtonPlayer.add(textButtonDelete);

        list = new List<UserName>(elementGUI.getSkin());
        list.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateTextFields();
            }
        });
        String[] strings = new String[5];
        for (int i = 1, k = 0; i <= strings.length; i++) {
            strings[k++] = "Player " + i + ": ";

        }
        list.setItems(names);

        scrollPane = new ScrollPane(list, elementGUI.getSkin());
        scrollPane.setBounds(20, 40, 300, 350);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setTransform(true);
        scrollPane.setScale(1f);

//        textFieldWorm1.setPosition(150,370);
//        textFieldWorm2.setPosition(350,370);
//        textFieldWorm3.setPosition(550,370);
//        textFieldWorm4.setPosition(750,370);
//        textFieldWorm5.setPosition(950,370);
//        textFieldUsername.setPosition(550,450);
//        textButtonPlayerNum.setPosition(550200);
//        textButtonSpielen.setPosition(550,120);

//        tableBackground.center();
        stage.addActor(tableBackground);
        stage.addActor(tableTextField);
        if (client == null) {
            stage.addActor(tableMap);
            stage.addActor(table4Worm);
            textButtonPlayerNum.setVisible(true);
        }

        stage.addActor(tableTextButton);
        stage.addActor(scrollPane);
        stage.addActor(tableTextButtonPlayer);
//        stage.setDebugAll(true);

//        stage.addActor(textFieldWorm1);
//        stage.addActor(textFieldWorm2);
//        stage.addActor(textFieldWorm3);
//        stage.addActor(textFieldWorm4);
//        stage.addActor(textFieldWorm5);
//        stage.addActor(textFieldUsername);
//        stage.addActor(textButtonPlayerNum);
//        stage.addActor(textButtonSpielen);

        elementGUI.setSelectedImageButton(buttonMap1);
        elementGUI.setSelectedImageButton2(buttonWorm1);

        updateTextFields();
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
    public void hide() {
        if (client != null) {
            client.unregisterDataHandler(dataHandler);
        }
        stage.dispose();
    }


}
