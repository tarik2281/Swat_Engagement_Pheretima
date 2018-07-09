package de.paluno.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.paluno.game.Assets;
import de.paluno.game.Constants;
import de.paluno.game.SEPGame;

public class LobbyScreen extends ScreenAdapter implements Loadable {
    private ScrollPane scrollPane;
    private List<String> list;
    private Skin skin;
    private Stage stage;
    private SEPGame game;
    private TextButton textButton;
    private ElementGUI elementGUI;
    private TextField textField;
    private Table table;
    private Image imageMap,imageNumWorms;
    int screenHeight = Gdx.graphics.getHeight();
    int screenWidth = Gdx.graphics.getWidth();



    public LobbyScreen(SEPGame game){
        elementGUI = new ElementGUI();
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        table = new Table(skin);
        table.right();
//        table.setBackground(new Image(game.getAssetManager().get(Assets.menuBackground)).getDrawable());


        skin = new Skin(Gdx.files.internal("uiskin.json"));
        textButton = new TextButton("Play",skin);
        textButton.setPosition(300,150);
        textButton.setSize(200,60);

        list = new List<String>(skin);
        String[] strings = new String[50];
        for (int i = 0, k = 0; i < 50; i++) {
            strings[k++] = "Lobby: " + i;

        }
        list.setItems(strings);
        scrollPane = new ScrollPane(list, skin);

        scrollPane.setBounds( 300, 300, screenWidth/2,screenHeight/2);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setTransform(true);
        scrollPane.setScale(1f);

        imageMap = new Image(game.getAssetManager().get(Assets.map1Thumbnail));
        imageNumWorms = new Image(game.getAssetManager().get(Assets.worms2Button));
        textField = elementGUI.createTextField("Map:");
        textField.setSize(400,400);


        table.add(textField).row();
        table.add(imageMap).row();
        table.add(imageNumWorms);


        table.setFillParent(true);


        stage.addActor(textButton);
        stage.addActor(scrollPane);
        stage.addActor(table);
//        stage.setDebugAll(true);


        list.addListener(new ClickListener(){

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