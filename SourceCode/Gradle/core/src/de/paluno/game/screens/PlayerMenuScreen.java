package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.paluno.game.Assets;
import de.paluno.game.SEPGame;

import java.util.*;


public class PlayerMenuScreen extends ScreenAdapter implements Loadable {
    private SEPGame game;
    private Stage menuStage;
    private ArrayList<TextField> playerTextFieldList;
    private ArrayList<TextField> teamTextFieldList;
    private ElementGUI elementGUI;
    private Table table;
    private ImageButton imageButtonPlay, imageButtonRandomNames;
    private Image image;
    private Texture background;
    private MenuScreen menuScreen;
    private ArrayList<String> playerNames = new ArrayList<>();
    private int mapNumber, numWorms, playerNumber;
    private ArrayList<String> randomTeamNames = new ArrayList<>(Arrays.asList("SEP Gruppe L", "Die Fussballer", "Die Unglaublichen", "Spongebob", "WWE", "Netflix", "Male Musicians", "Female Musicians"));
    private ArrayList<String> randomPlayerNames = new ArrayList<>(Arrays.asList("Julian", "Tarik", "Ibo", "Jan", "Steve",
            "Messi", "Ronaldo", "Kroos", "Neuer", "Ibrahimovic",
            "Batman", "Hulk", "Superman", "Spiderman", "Ant-Man",
            "Spongebob", "Patrick", "Sandy", "Plankton", "Mr. Krabs",
            "The Undertaker", "Rey Mysterio", "Jeff Hardy", "Hornswoggle", "The Rock",
            "Charlie Sheen", "Dexter", "Michael Scofield", "Barney Stinson", "Walter White",
            "Chris Brown", "Drake", "Frank Ocean", "Trey Songz", "Eminem",
            "Rihanna", "Beyonce", "Amy Winehouse", "Britney Spears", "J-Lo"));

    private Random rndNames = new Random();

    public PlayerMenuScreen(SEPGame game, int mapNumber, int numWorms, int playerNumber) {
        super();
        this.game = game;
        this.menuScreen = new MenuScreen(game, playerNumber);
        menuStage = new Stage();
        elementGUI = new ElementGUI();
        this.mapNumber = mapNumber;
        this.numWorms = numWorms;
        this.playerNumber = playerNumber;
        Gdx.input.setInputProcessor(menuStage);

    }

    @Override
    public boolean load(AssetManager manager) {
        Assets.loadAssets(manager, Assets.PlayerMenuScreenAssets);

        return false;
    }


    @Override
    public void show() {
        table = new Table();
        table.setFillParent(true);
        playerTextFieldList = new ArrayList<>();
        teamTextFieldList = new ArrayList<>();

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

        switch (numWorms) {
            case 1:

                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
        }

        image = elementGUI.createBackground(background);
        table.setBackground(image.getDrawable());

        imageButtonRandomNames = elementGUI.createButton(game.getAssetManager().get(Assets.random));
        imageButtonRandomNames.addListener(new ClickListener() {
            int j;

            @Override
            public void clicked(InputEvent event, float x, float y) {
//                Collections.shuffle(randomTeamNames);
//                Collections.shuffle(randomPlayerNames);
                int field = 0;
                int j = 0;
                int k = 0;
                int nameNum = 5 - numWorms;
                for (int i = 0; i < playerNumber; i++) {
                        k+= nameNum;
                    for (; j < (numWorms*playerNumber) ; j++,k++) {
                        playerTextFieldList.get(j).setText(randomPlayerNames.get(k));
                    }
                }

                for (int i = 0; i < playerNumber; i++) {
                    teamTextFieldList.get(i).setText(randomTeamNames.get(i));
                }
            }
        });

        imageButtonPlay = elementGUI.createButton(game.getAssetManager().get(Assets.playButton));
        imageButtonPlay.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked");
                for (int i = 0; i < (numWorms * playerNumber); i++) {
                    playerNames.add(playerTextFieldList.get(i).getText());
                }
                game.setPlayScreen(mapNumber, numWorms, playerNumber, 0, playerNames);
            }
        });


        for (int i = 0; i < (numWorms * playerNumber); i++) {
            playerTextFieldList.add(elementGUI.createTextField(""));
        }

        for (int i = 0; i < playerNumber; i++) {
            teamTextFieldList.add(elementGUI.createTextField(""));
        }


        imageButtonPlay.setPosition(1150, 10);
        imageButtonRandomNames.setPosition(1100, 10);
        menuStage.addActor(table);
        menuStage.addActor(imageButtonPlay);
        menuStage.addActor(imageButtonRandomNames);

        int x;
        int y = 580;
        int k = 0;
        for (int i = 0; i < playerNumber; i++, y -= 140) {
            x = 20;
            for (int j = 0; j < numWorms; j++, x += 200) {
                playerTextFieldList.get(k++).setPosition(x, y);
            }
        }
        int teamY = 655;
        for (int i = 0; i < playerNumber; i++) {
            teamTextFieldList.get(i).setPosition(220, teamY);
            teamY -= 140;
        }

        for (int i = 0; i < (playerNumber * numWorms); i++) {
            menuStage.addActor(playerTextFieldList.get(i));
        }

        for (int i = 0; i < playerNumber; i++) {
            menuStage.addActor(teamTextFieldList.get(i));

        }


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
