package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.paluno.game.Assets;
import de.paluno.game.SEPGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class TeamMenuScreen extends ScreenAdapter implements Loadable {
    private SEPGame game;
    private Stage menuStage;
    private ElementGUI elementGUI;
    private Table table;
    private ImageButton imageButtonPlay,imageButtonRandomTeamNames;
    private Image image;
    private Texture play, background,random;
    private MenuScreen menuScreen;
    private ArrayList<String> playerNames;
    private int mapNumber, numWorms, playerNumber;
    private ArrayList<TextField> textFieldList;
    private ArrayList<String> randomTeamNames;
    private ScrollPane scrollPane;

    public TeamMenuScreen(SEPGame game, int mapNumber, int numWorms, int playerNumber) {
        super();
        this.game = game;
        this.menuScreen = new MenuScreen(game, playerNumber);
        menuStage = new Stage();
        elementGUI = new ElementGUI();
        this.mapNumber = mapNumber;
        this.numWorms = numWorms;
        this.playerNumber = playerNumber;
        playerNames = new ArrayList<>();
        Gdx.input.setInputProcessor(menuStage);
    }

    @Override
    public boolean load(AssetManager manager) {
        Assets.loadAssets(manager, Assets.TeamMenuScreenAssets);

        return false;
    }


    @Override
    public void show() {
        table = new Table();
        table.setFillParent(true);
        textFieldList = new ArrayList<>();
        play = game.getAssetManager().get(Assets.playButton);

        random = game.getAssetManager().get(Assets.random);
        imageButtonRandomTeamNames = elementGUI.createButton(random);
        imageButtonRandomTeamNames.addListener(new ClickListener(){
            int j;
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Collections.shuffle(randomTeamNames);
                for (int i = 0 ; i < textFieldList.size(); i++) {
                    textFieldList.get(i).setText(randomTeamNames.get(i));

                }
            }
        });


        imageButtonPlay = elementGUI.createButton(play);
        imageButtonPlay.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked");
                for (int i = 0; i < 5; i++) {
                    playerNames.add(textFieldList.get(i).getText());
                }
                game.setPlayerMenuScreen(mapNumber, numWorms, playerNumber);
            }
        });

        for (int i = 0; i < 5; i++) {
            textFieldList.add(elementGUI.createTextField());
        }

        imageButtonPlay.setPosition(715, 10);
        menuStage.addActor(table);
        menuStage.addActor(imageButtonPlay);


        for (int i = 0, y = 610; i < playerNumber; i++, y -= 140) {
            textFieldList.get(i).setPosition(380, y);
            menuStage.addActor(textFieldList.get(i));
        }

        switch (playerNumber) {
            case 2:
                background = game.getAssetManager().get(Assets.teamMenuScreen2);
                break;
            case 3:
                background = game.getAssetManager().get(Assets.teamMenuScreen3);
                break;
            case 4:
                background = game.getAssetManager().get(Assets.teamMenuScreen4);
                break;
            case 5:
                background = game.getAssetManager().get(Assets.teamMenuScreen5);
                break;
        }
        image = elementGUI.createBackground(background);
        table.setBackground(image.getDrawable());

    }


    @Override
    public void render(float delta) {
        // clears screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // System.out.println( "x" + Gdx.input.getX() + "y" + Gdx.input.getY());
        menuStage.act(Gdx.graphics.getDeltaTime());
        menuStage.draw();

    }

    @Override
    public void hide() {
        menuStage.dispose();
    }


}
