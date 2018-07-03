package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.paluno.game.Assets;
import de.paluno.game.SEPGame;


public class MenuScreen extends ScreenAdapter implements Loadable {

    private ElementGUI elementGUI;
    private SEPGame game;
    private Stage menuStage;
    private Table menuTable, menuTable2, menuTable3, menuTableModi;
    private Image image;
    // Icons
    private ImageButton buttonMap1, buttonMap2, buttonMap3, buttonMap4, buttonPlay, buttonWorm1, buttonWorm2,
            buttonWorm3, buttonWorm4, buttonWorm5;
    private TextField username;

    private ImageButton selectedWormButton;
    private ImageButton selectedMapButton;



    private int mapNumber;
    private int numWorms;
    private int playerNumber;

    public MenuScreen(SEPGame game, int playerNumber) {
        super();
        this.game = game;
        menuStage = new Stage();
        elementGUI = new ElementGUI();
        this.playerNumber = playerNumber;
        Gdx.input.setInputProcessor(menuStage);


    }

    @Override
    public boolean load(AssetManager manager) {
        Assets.loadAssets(manager, Assets.MenuScreenAssets);

        return false;
    }

    public void show() {

        //Username Textfield
        username = elementGUI.createTextField("Lobbyname");

        // Map Buttons
        buttonMap1 = elementGUI.createButton(game.getAssetManager().get(Assets.map1Thumbnail));
        buttonMap1.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        buttonMap1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedMapButton(buttonMap1);
                mapNumber = 0;
                System.out.println("Map1 Clicked");
            }
        });


        buttonMap2 = elementGUI.createButton(game.getAssetManager().get(Assets.map2Thumbnail));
        buttonMap2.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        buttonMap2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedMapButton(buttonMap2);
                mapNumber = 1;
                System.out.println("Map2 Clicked");
            }
        });

        buttonMap3 = elementGUI.createButton(game.getAssetManager().get(Assets.map3Thumbnail));
        buttonMap3.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        buttonMap3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedMapButton(buttonMap3);
                mapNumber = 2;
                System.out.println("Map3 Clicked");
            }
        });

        buttonMap4 = elementGUI.createButton(game.getAssetManager().get(Assets.map4Thumbnail));
        buttonMap4.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        buttonMap4.addListener(new ClickListener() {


            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedMapButton(buttonMap4);
                mapNumber = 3;
                System.out.println("Map4 Clicked");
            }
        });


        // Play ElementGUI
        buttonPlay = elementGUI.createButton(game.getAssetManager().get(Assets.playButton));
        buttonPlay.addListener(new ClickListener() {


            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setPlayerMenuScreen(mapNumber,numWorms,playerNumber);
//                game.setPlayScreen(mapNumber,numWorms,modi);
                System.out.println("Play Clicked");
            }
        });


        // Worm Number Buttons

        buttonWorm1 = elementGUI.createButton( game.getAssetManager().get(Assets.worms1Button));
        buttonWorm1.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        buttonWorm1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm1);
                numWorms = 1;
                System.out.println("Worm 1 Clicked");
            }
        });

        buttonWorm2 = elementGUI.createButton(game.getAssetManager().get(Assets.worms2Button));
        buttonWorm2.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        buttonWorm2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm2);
                numWorms = 2;
                System.out.println("Worm 2 Clicked");
            }
        });

        buttonWorm3 = elementGUI.createButton(game.getAssetManager().get(Assets.worms3Button));
        buttonWorm3.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        buttonWorm3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm3);
                numWorms = 3;
                System.out.println("Worm 3 Clicked");
            }
        });

        buttonWorm4 = elementGUI.createButton(game.getAssetManager().get(Assets.worms4Button));
        buttonWorm4.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        buttonWorm4.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm4);
                numWorms = 4;
                System.out.println("Worm 4 Clicked");
            }
        });

        buttonWorm5 = elementGUI.createButton(game.getAssetManager().get(Assets.worms5Button));
        buttonWorm5.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        buttonWorm5.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedWormButton(buttonWorm5);
                numWorms = 5;
                System.out.println("Worm 5 Clicked");
            }
        });





        menuTable = new Table();
        menuTable2 = new Table();

        // Menu Background
        image = elementGUI.createBackground(game.getAssetManager().get(Assets.menuBackground));

        menuStage.setDebugAll(false);
        //menuTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        menuStage.addActor(menuTable);
        menuTable.setFillParent(true);
        menuTable.setBackground(image.getDrawable());

        menuTable.add(buttonMap1);
        buttonMap1.pad(10);
        menuTable.add(buttonMap2);
        buttonMap1.pad(10);
        menuTable.row();
        menuTable.add(buttonMap3);
        buttonMap1.pad(10);
        menuTable.add(buttonMap4);
        buttonMap1.pad(10);
        menuTable.left();


        menuStage.addActor(menuTable2);
        menuTable2.add(buttonWorm1);
        buttonWorm1.pad(10);
        menuTable2.add(buttonWorm2);
        buttonWorm2.pad(10);
        menuTable2.add(buttonWorm3);
        buttonWorm3.pad(10);
        menuTable2.add(buttonWorm4);
        buttonWorm4.pad(10);
        menuTable2.add(buttonWorm5);
        buttonWorm5.pad(10);
        menuTable2.left().bottom();
        menuTable2.padBottom(150);
        menuTable2.padLeft(50);

        menuTable3 = new Table();
        menuTableModi = new Table();

        menuStage.addActor(menuTable3);
        menuTable3.left().bottom();
        menuTable3.add(buttonPlay);
        buttonPlay.padLeft(250);
        buttonPlay.padBottom(70);

        menuTableModi.setFillParent(true);
        menuStage.addActor(menuTableModi);
        menuTableModi.top().left();
        menuTableModi.padLeft(160);
        menuTableModi.padTop(200);

        menuStage.addActor(username);
        username.setPosition(250,620);

        setSelectedMapButton(buttonMap1);
        mapNumber = 0;
        setSelectedWormButton(buttonWorm1);
        numWorms = 1;
    }

    private void setSelectedMapButton(ImageButton button) {
        //GREY Transparent 1,1,1,0.4
        if (selectedMapButton != null)
            selectedMapButton.setColor(1.0f, 1.0f, 1.0f, 0.4f);

        selectedMapButton = button;
        // WHITE Transparent 1,1,1,1
        if (selectedMapButton != null)
            selectedMapButton.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void setSelectedWormButton(ImageButton button) {
        if (selectedWormButton != null)
            selectedWormButton.setColor(1.0f, 1.0f, 1.0f, 0.4f);

        selectedWormButton = button;

        if (selectedWormButton != null)
            selectedWormButton.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }



    public void render(float delta) {
        // clears screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        menuStage.act(Gdx.graphics.getDeltaTime());
        menuStage.draw();

    }

    public void hide() {
        menuStage.dispose();
    }
}

