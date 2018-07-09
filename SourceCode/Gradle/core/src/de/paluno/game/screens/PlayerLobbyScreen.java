package de.paluno.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.paluno.game.Assets;
import de.paluno.game.NetworkClient;
import de.paluno.game.SEPGame;

public class PlayerLobbyScreen extends ScreenAdapter implements Loadable {
    private ScrollPane scrollPane;
    private List<String> list;
    private Skin skin;
    private Stage stage;
    private SEPGame game;
    private TextButton textButtonMenu,textButtonPlay;
    private ElementGUI elementGUI;
    private TextField textField, textFieldBackground;
    private Table table;
    private Image imageMap, imageNumWorms;
    private Container<Table> chatWindowContainer;
    private ChatWindow chatWindow;
    private LobbyScreen lobbyScreen;
    private int numWorms, mapNumber;


    public PlayerLobbyScreen(SEPGame game, int mapNumber, int numWorms ) {
        elementGUI = new ElementGUI();
        this.game = game;
        NetworkClient client = new NetworkClient("localhost");
        chatWindow = new ChatWindow(client);
        lobbyScreen = new LobbyScreen(game);
        this.numWorms = numWorms;
        this.mapNumber = mapNumber;
    }

    @Override
    public void show() {
        stage = new Stage();
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
                game.setLobbyScreen();
            }
        });

        textButtonPlay = elementGUI.createTextButton("Spielen");
        textButtonPlay.setPosition(380,85);
        textButtonPlay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setPlayScreen(mapNumber,numWorms);
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

        switch (mapNumber) {
            case 1:
                imageMap = new Image(game.getAssetManager().get(Assets.map1Thumbnail));
                break;
            case 2:
                imageMap = new Image(game.getAssetManager().get(Assets.map2Thumbnail));
                break;
            case 3:
                imageMap = new Image(game.getAssetManager().get(Assets.map3Thumbnail));
                break;
            case 4:
                imageMap = new Image(game.getAssetManager().get(Assets.map4Thumbnail));
                break;

        }

        switch (numWorms) {
            case 1:
                imageNumWorms = new Image(game.getAssetManager().get(Assets.worms1Button));
                break;
            case 2:
                imageNumWorms = new Image(game.getAssetManager().get(Assets.worms2Button));
                break;
            case 3:
                imageNumWorms = new Image(game.getAssetManager().get(Assets.worms3Button));
                break;
            case 4:
                imageNumWorms = new Image(game.getAssetManager().get(Assets.worms4Button));
                break;
            case 5:
                imageNumWorms = new Image(game.getAssetManager().get(Assets.worms5Button));
                break;
        }
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
    public boolean load(AssetManager manager) {
        Assets.loadAssets(manager, Assets.LobbyScreenAssets);
        return false;
    }
}