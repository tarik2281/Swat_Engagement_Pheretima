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
import de.paluno.game.SEPGame;

public class LobbyScreen extends ScreenAdapter implements Loadable {
    private ScrollPane scrollPane;
    private List<String> list;
    private Skin skin;
    float gameWidth, gameHeight;
    private Stage stage;
    private SEPGame game;
    private TextButton textButton;

    public LobbyScreen(SEPGame game){
        this.game = game;
    }

    @Override
    public void show() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        textButton = new TextButton("Play",skin);
        textButton.setPosition(300,300);
        textButton.setSize(200,60);

        list = new List<String>(skin);
        String[] strings = new String[50];
        for (int i = 0, k = 0; i < 50; i++) {
            strings[k++] = "Lobby: " + i;

        }
        list.setItems(strings);

        scrollPane = new ScrollPane(list, skin);
        scrollPane.setBounds(0 , 0, 300, 300);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setTransform(true);
        scrollPane.setScale(1f);
        scrollPane.setPosition(20,500);

        stage = new Stage();
        stage.addActor(textButton);
        stage.addActor(scrollPane);

        list.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println(list.getSelectedIndex());
            }
        });
        Gdx.input.setInputProcessor(stage);

    }

    float backgroundX = 0;

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
        return false;
    }
}