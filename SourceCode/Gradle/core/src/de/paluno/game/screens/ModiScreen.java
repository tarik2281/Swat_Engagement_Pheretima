package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.paluno.game.Assets;
import de.paluno.game.SEPGame;


public class ModiScreen extends ScreenAdapter implements Loadable {

    private Stage stage;
    private Table table;
    private ImageButton local, multiplayer, playButton, selectedModiButton, imageButtonPlayer2,imageButtonPlayer3,imageButtonPlayer4, imageButtonPlayer5;
    private Texture background, player2,player3,player4,player5;
    private Image image;
    private ElementGUI elementGUI;
    private SEPGame game;
    private ImageButton selectedPlayerButton;
    private int playerNumber;


    public ModiScreen(SEPGame game){
        super();
        this.game = game;
        stage = new Stage();
        elementGUI = new ElementGUI();
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void show() {
        table = new Table();


        // Player Number Buttons
        player2 =  game.getAssetManager().get(Assets.playerNumber2);
        imageButtonPlayer2 = elementGUI.createButton(player2);
        imageButtonPlayer2.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        imageButtonPlayer2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedPlayerButton(imageButtonPlayer2);
                playerNumber = 2;
                System.out.println("Player2 Button Clicked");
            }
        });

        player3 = game.getAssetManager().get(Assets.playerNumber3);
        imageButtonPlayer3 = elementGUI.createButton(player3);
        imageButtonPlayer3.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        imageButtonPlayer3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedPlayerButton(imageButtonPlayer3);
                playerNumber = 3;
                System.out.println("Player3 Button Clicked");
            }
        });

        player4 = game.getAssetManager().get(Assets.playerNumber4);
        imageButtonPlayer4 = elementGUI.createButton(player4);
        imageButtonPlayer4.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        imageButtonPlayer4.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedPlayerButton(imageButtonPlayer4);
                playerNumber = 4;
                System.out.println("Player4 Button Clicked");
            }
        });

        player5 = game.getAssetManager().get(Assets.playerNumber5);
        imageButtonPlayer5 = elementGUI.createButton(player5);
        imageButtonPlayer5.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        imageButtonPlayer5.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedPlayerButton(imageButtonPlayer5);
                playerNumber = 5;
                System.out.println("Player5 Button Clicked");
            }
        });



        local = elementGUI.createButton(game.getAssetManager().get(Assets.local));
        local.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        local.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedModiButton(local);
                System.out.println("Local Button Clicked!");

            }
        });

        multiplayer = elementGUI.createButton(game.getAssetManager().get(Assets.multi));
        multiplayer.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        multiplayer.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedModiButton(multiplayer);
                System.out.println("Multiplayer Button Clicked");
            }
        });
        playButton = elementGUI.createButton(game.getAssetManager().get(Assets.playButton));

        background = game.getAssetManager().get(Assets.menuBackground);
        image = elementGUI.createBackground(background);

        table.setBackground(image.getDrawable());
        table.setFillParent(true);


        local.setPosition(200,300);
        multiplayer.setPosition(550,300);
        playButton.setPosition(450,200);
        imageButtonPlayer2.setPosition(130,450);
        imageButtonPlayer3.setPosition(330,450);
        imageButtonPlayer4.setPosition(530,450);
        imageButtonPlayer5.setPosition(730,450);


        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked");
                game.setMenuScreen(playerNumber);
            }
        });

        stage.addActor(table);
        stage.addActor(local);
        stage.addActor(multiplayer);
        stage.addActor(playButton);
        stage.addActor(imageButtonPlayer2);
        stage.addActor(imageButtonPlayer3);
        stage.addActor(imageButtonPlayer4);
        stage.addActor(imageButtonPlayer5);

        setSelectedModiButton(local);
        setSelectedPlayerButton(imageButtonPlayer2);
        playerNumber = 2;

    }

    @Override
    public boolean load(AssetManager manager) {
        Assets.loadAssets(manager, Assets.ModiScreenAssets);
        return false;
    }

    @Override
    public void render(float delta) {
        // clears screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    private void setSelectedModiButton(ImageButton button) {
        if (selectedModiButton != null)
            selectedModiButton.setColor(1.0f, 1.0f, 1.0f, 0.4f);

        selectedModiButton = button;

        if (selectedModiButton != null)
            selectedModiButton.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void setSelectedPlayerButton(ImageButton button) {
        if (selectedPlayerButton != null)
            selectedPlayerButton.setColor(1.0f, 1.0f, 1.0f, 0.4f);

        selectedPlayerButton = button;

        if (selectedPlayerButton != null)
            selectedPlayerButton.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void hide() {
        stage.dispose();
    }

}
