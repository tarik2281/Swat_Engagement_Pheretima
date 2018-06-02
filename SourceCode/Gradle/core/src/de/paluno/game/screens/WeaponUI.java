package de.paluno.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.paluno.game.GameState;
import de.paluno.game.gameobjects.Renderable;

public class WeaponUI implements Renderable {

    private PlayScreen playScreen;
    private Stage stage;
    private Texture gun;
    private TextureRegion textureRegionWeapon;
    private TextureRegionDrawable regionDrawableWeapon;
    private ImageButton buttonWeaponPistol;
    private Table table;
    private Image image;
    private Texture textureBackground = new Texture(Gdx.files.internal("weaponpanel.png"));


    WeaponUI(PlayScreen playScreen) {
        this.playScreen = playScreen;

        image = new Image((new TextureRegionDrawable(new TextureRegion(textureBackground))));

        //Weapon Pistol Button
        gun = new Texture(Gdx.files.internal("next.png"));
        textureRegionWeapon = new TextureRegion(gun);
        regionDrawableWeapon = new TextureRegionDrawable(textureRegionWeapon);
        buttonWeaponPistol = new ImageButton(regionDrawableWeapon);
        buttonWeaponPistol.addListener((new ClickListener() {


            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                System.out.println("Mouse touched");
                return true;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("ButtonWeapon1 Clicked");
                table.setVisible(false);


            }
        }));


        table = new Table();

        //  viewport = new FitViewport(60, 60, new OrthographicCamera());
        //Stage
        stage = new Stage();
        stage.addActor(buttonWeaponPistol);




        table.setBackground( image.getDrawable());

        table.setPosition(1260,120);


        table.add(buttonWeaponPistol).size(60, 60);
        table.setDebug(false);
        table.setSize(92, 147);
        stage.addActor(table);

    }


    @Override
    public void render(SpriteBatch batch, float delta) {
        stage.act(Gdx.graphics.getDeltaTime());

        stage.draw();

    }


    public Stage getStage() {
        return stage;
    }
}
