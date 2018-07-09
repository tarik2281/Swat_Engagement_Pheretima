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

import java.util.*;


public class LoginScreen extends ScreenAdapter implements Loadable {
    private SEPGame game;
    private Stage stage;
    private ElementGUI elementGUI;
    private TextField textFieldWorm1, textFieldWorm2, textFieldWorm3, textFieldWorm4, textFieldWorm5, textFieldUsername;
    private TextButton textButtonRandom, textButtonStart;
    private Table table;
    private Image imageBackground;
    private ArrayList<String> randomPlayerNames = new ArrayList<>(Arrays.asList("Julian", "Tarik", "Ibo", "Jan", "Steve",
            "Messi", "Ronaldo", "Kroos", "Neuer", "Ibrahimovic",
            "Batman", "Hulk", "Superman", "Spiderman", "Ant-Man",
            "Spongebob", "Patrick", "Sandy", "Plankton", "Mr. Krabs",
            "The Undertaker", "Rey Mysterio", "Jeff Hardy", "Hornswoggle", "The Rock",
            "Charlie Sheen", "Dexter", "Michael Scofield", "Barney Stinson", "Walter White",
            "Chris Brown", "Drake", "Frank Ocean", "Trey Songz", "Eminem",
            "Rihanna", "Beyonce", "Amy Winehouse", "Britney Spears", "J-Lo"));

    public LoginScreen(SEPGame game) {
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


    @Override
    public void show() {
        table = new Table(elementGUI.getSkin());
        imageBackground = elementGUI.createBackground(game.getAssetManager().get(Assets.menuBackground));
        table.setBackground(imageBackground.getDrawable());
        table.setFillParent(true);

        textFieldWorm1 = elementGUI.createTextField("Worm 1");
        textFieldWorm2 = elementGUI.createTextField("Worm 2");
        textFieldWorm3 = elementGUI.createTextField("Worm 3");
        textFieldWorm4 = elementGUI.createTextField("Worm 4");
        textFieldWorm5 = elementGUI.createTextField("Worm 5");
        textFieldUsername = elementGUI.createTextField("Username");
        textButtonRandom = elementGUI.createTextButton("Auto-Fill");
        textButtonStart = elementGUI.createTextButton("Einloggen");

        textButtonRandom.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });

        textButtonStart.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setLobbyScreen();
            }
        });

        textFieldWorm1.setPosition(150,370);
        textFieldWorm2.setPosition(350,370);
        textFieldWorm3.setPosition(550,370);
        textFieldWorm4.setPosition(750,370);
        textFieldWorm5.setPosition(950,370);
        textFieldUsername.setPosition(550,450);
        textButtonRandom.setPosition(550,200);
        textButtonStart.setPosition(550,120);

        stage.addActor(table);
        stage.addActor(textFieldWorm1);
        stage.addActor(textFieldWorm2);
        stage.addActor(textFieldWorm3);
        stage.addActor(textFieldWorm4);
        stage.addActor(textFieldWorm5);
        stage.addActor(textFieldUsername);
        stage.addActor(textButtonRandom);
        stage.addActor(textButtonStart);


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
        stage.dispose();
    }


}
