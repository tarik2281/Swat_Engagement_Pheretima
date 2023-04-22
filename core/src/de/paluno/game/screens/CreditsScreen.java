package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.paluno.game.Assets;
import de.paluno.game.EventManager;
import de.paluno.game.SEPGame;

public class CreditsScreen extends ScreenAdapter implements Loadable {

    private SEPGame game;

    private Stage stage;
    private Skin skin;
    private Texture creditsTexture;

    public CreditsScreen(SEPGame game) {
        this.game = game;
    }

    @Override
    public boolean load(AssetManager manager) {
        Assets.loadAssets(manager, Assets.MenuScreenAssets);
        return false;
    }

    @Override
    public void show() {
        creditsTexture = new Texture(Gdx.files.internal("Credits.png"));

        stage = new Stage(new ScreenViewport());

        ElementGUI elementGUI = new ElementGUI();
        skin = elementGUI.getSkin();

        Table table = new Table(skin);
        table.setBackground(elementGUI.createBackground(game.getAssetManager().get(Assets.menuBackground)).getDrawable());
        table.setFillParent(true);
        table.center();
        table.add(elementGUI.createBackground(creditsTexture));
        table.row();

        TextButton menuButton = new TextButton("Menu", skin);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                game.setModiScreen();
            }
        });

        table.add(menuButton).padTop(100).size(200, 60);
        table.padTop(100);

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        creditsTexture.dispose();
        stage.dispose();
        skin.dispose();
    }
}
