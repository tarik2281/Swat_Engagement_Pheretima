package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import de.paluno.game.Assets;
import de.paluno.game.SEPGame;


public class MenuScreen extends ScreenAdapter {


    private SEPGame game;
    protected Sprite sprite;
    private Stage menuStage;
    private Table menuTable, menuTable2,menuTable3;
    private Image image;
    private Texture map1, map2, map3, map4, textureBackground, play, worm1, worm2, worm3, worm4, worm5;
    // defines rectangular area of a texture
    private TextureRegion textureRegionMap1, textureRegionMap2, textureRegionMap3, textureRegionMap4,
            textureRegionPlay, textureRegionWorm1, textureRegionWorm2, textureRegionWorm3, textureRegionWorm4, textureRegionWorm5;
    // draws the texture in the given size
    private TextureRegionDrawable regionDrawableMap1, regionDrawableMap2, regionDrawableMap3, regionDrawableMap4, regionDrawablePlay,
            regionDrawableWorm1, regionDrawableWorm2, regionDrawableWorm3, regionDrawableWorm4, regionDrawableWorm5;
    // Icons
    private ImageButton buttonMap1, buttonMap2, buttonMap3, buttonMap4, buttonPlay, buttonWorm1, buttonWorm2, buttonWorm3, buttonWorm4, buttonWorm5;

    private int mapNumber;
    private int numWorms;

    public MenuScreen(SEPGame game) {
        super();
        this.game = game;
        menuStage = new Stage();
        Gdx.input.setInputProcessor(menuStage);


    }

    public void show() {
        textureBackground = new Texture("MENU.png");

        image = new Image((new TextureRegionDrawable(new TextureRegion(textureBackground))));

        map1 = new Texture(Gdx.files.internal("Map1SEP.png"));
        textureRegionMap1 = new TextureRegion(map1);
        regionDrawableMap1 = new TextureRegionDrawable(textureRegionMap1);
        buttonMap1 = new ImageButton(regionDrawableMap1);
        buttonMap1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapNumber = 0;
                System.out.println("Map1 Clicked");
            }
        });

        map2 = new Texture(Gdx.files.internal("Map2SEP.png"));
        textureRegionMap2 = new TextureRegion(map2);
        regionDrawableMap2 = new TextureRegionDrawable(textureRegionMap2);
        buttonMap2 = new ImageButton(regionDrawableMap2);
        buttonMap2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapNumber = 1;
                System.out.println("Map2 Clicked");
            }
        });

        map3 = new Texture(Gdx.files.internal("Map3SEP.png"));
        textureRegionMap3 = new TextureRegion(map3);
        regionDrawableMap3 = new TextureRegionDrawable(textureRegionMap3);
        buttonMap3 = new ImageButton(regionDrawableMap3);
        buttonMap3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapNumber = 2;
                System.out.println("Map3 Clicked");
            }
        });

        map4 = new Texture(Gdx.files.internal("Map4SEP.png"));
        textureRegionMap4 = new TextureRegion(map4);
        regionDrawableMap4 = new TextureRegionDrawable(textureRegionMap4);
        buttonMap4 = new ImageButton(regionDrawableMap4);
        buttonMap4.addListener(new ClickListener() {


            @Override
            public void clicked(InputEvent event, float x, float y) {
                mapNumber = 3;
                System.out.println("Map4 Clicked");
            }
        });

        play = new Texture(Gdx.files.internal("PlayButton.png"));
        textureRegionPlay = new TextureRegion(play);
        regionDrawablePlay = new TextureRegionDrawable(textureRegionPlay);
        buttonPlay = new ImageButton(regionDrawablePlay);
        buttonPlay.addListener(new ClickListener() {


            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setPlayScreen(mapNumber, numWorms);
                System.out.println("Play Clicked");
            }
        });


        // Buttons Multiplayer


        worm1 = new Texture(Gdx.files.internal("1.png"));
        textureRegionWorm1 = new TextureRegion(worm1);
        regionDrawableWorm1 = new TextureRegionDrawable(textureRegionWorm1);
        buttonWorm1 = new ImageButton(regionDrawableWorm1);
        buttonWorm1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                numWorms = 1;
                System.out.println("Worm 1 Clicked");
            }
        });

        worm2 = new Texture(Gdx.files.internal("2.png"));
        textureRegionWorm2 = new TextureRegion(worm2);
        regionDrawableWorm2 = new TextureRegionDrawable(textureRegionWorm2);
        buttonWorm2 = new ImageButton(regionDrawableWorm2);
        buttonWorm2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                numWorms = 2;
                System.out.println("Worm 2 Clicked");
            }
        });
        worm3 = new Texture(Gdx.files.internal("3.png"));
        textureRegionWorm3 = new TextureRegion(worm3);
        regionDrawableWorm3 = new TextureRegionDrawable(textureRegionWorm3);
        buttonWorm3 = new ImageButton(regionDrawableWorm3);
        buttonWorm3.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                numWorms = 3;
                System.out.println("Worm 3 Clicked");
            }
        });
        worm4 = new Texture(Gdx.files.internal("4.png"));
        textureRegionWorm4 = new TextureRegion(worm4);
        regionDrawableWorm4 = new TextureRegionDrawable(textureRegionWorm4);
        buttonWorm4 = new ImageButton(regionDrawableWorm4);
        buttonWorm4.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                numWorms = 4;
                System.out.println("Worm 4 Clicked");
            }
        });

        worm5 = new Texture(Gdx.files.internal("5.png"));
        textureRegionWorm5 = new TextureRegion(worm5);
        regionDrawableWorm5 = new TextureRegionDrawable(textureRegionWorm5);
        buttonWorm5 = new ImageButton(regionDrawableWorm5);
        buttonWorm5.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                numWorms = 5;
                System.out.println("Worm 5 Clicked");
            }
        });


        menuTable = new Table();
        menuTable2 = new Table();


        menuStage.setDebugAll(false);
        menuStage.addActor(menuTable);
        menuTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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

        menuStage.addActor(menuTable3);
        menuTable3.left().bottom();
        menuTable3.add(buttonPlay);
        buttonPlay.padLeft(250);
        buttonPlay.padBottom(70);



    }

    public void render(float delta) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        menuStage.act(Gdx.graphics.getDeltaTime());
        menuStage.draw();

    }

    public void hide() {
        menuStage.dispose();
    }
}

