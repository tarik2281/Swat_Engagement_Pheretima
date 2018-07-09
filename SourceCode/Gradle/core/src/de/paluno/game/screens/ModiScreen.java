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
import de.paluno.game.SEPGame;

import javax.xml.soap.Text;

public class ModiScreen extends ScreenAdapter implements Loadable {
    private Stage stage;
    private Table table;
    private Skin skin;
    private SEPGame game;
    private  ElementGUI elementGUI;
    private Image imageBackground;
    private TextButton textButtonPlay,textButtonOnline, textButtonLocal;
    private TextButton selectedModiButton;

    public ModiScreen(SEPGame game) {
        super();
        this.game = game;
        elementGUI = new ElementGUI();
    }

    @Override
    public void show() {
        stage = new Stage();
        table = new Table();
        skin = elementGUI.getSkin();

        table.setFillParent(true);
        imageBackground = elementGUI.createBackground(game.getAssetManager().get(Assets.menuBackground));
        table.setBackground(imageBackground.getDrawable());

        textButtonLocal = elementGUI.createTextButton("Local");
        textButtonLocal.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                elementGUI.setSelectedTextButton(textButtonLocal);
            }
        });
        textButtonLocal.setSize(300,100);
        textButtonOnline = elementGUI.createTextButton("Online");
        textButtonOnline.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
               elementGUI.setSelectedTextButton(textButtonOnline);
            }
        });
        textButtonOnline.setSize(300,100);
        textButtonPlay = elementGUI.createTextButton("Start");
        textButtonPlay.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setLoginScreen();
            }
        });
        textButtonLocal.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        textButtonOnline.setColor(1.0f, 1.0f, 1.0f, 0.4f);


        textButtonLocal.setPosition(330,300);
        textButtonOnline.setPosition(730,300);
        textButtonPlay.setPosition(570,200);

        setSelectedModiButton(textButtonLocal);


        stage.addActor(table);
        stage.addActor(textButtonLocal);
        stage.addActor(textButtonOnline);
        stage.addActor(textButtonPlay);



        Gdx.input.setInputProcessor(stage);
    }

    public void setSelectedModiButton(TextButton button) {
        //GREY Transparent 1,1,1,0.4
        if (selectedModiButton != null)
            selectedModiButton.setColor(1.0f, 1.0f, 1.0f, 0.4f);

        selectedModiButton = button;
        // WHITE Transparent 1,1,1,1
        if (selectedModiButton != null)
            selectedModiButton.setColor(1.0f, 1.0f, 1.0f, 1.0f);
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
