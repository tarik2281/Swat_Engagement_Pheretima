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
    private Texture gun,grenade,bazooka;
    private TextureRegion textureRegionGun,textureRegionGrenade, textureRegionBazooka;
    private TextureRegionDrawable regionDrawableGun, regionDrawableGrenade,regionDrawableBazooka;
    private ImageButton buttonWeaponGun, buttonGrenade,buttonBazooka;
    private Table table;
    private Image image;
    private Texture textureBackground = new Texture(Gdx.files.internal("weaponpanel.png"));



    WeaponUI(PlayScreen playScreen) {
        this.playScreen = playScreen;

        image = new Image((new TextureRegionDrawable(new TextureRegion(textureBackground))));

        //Gun
        gun = new Texture(Gdx.files.internal("IconGun.png"));
        textureRegionGun = new TextureRegion(gun);
        regionDrawableGun = new TextureRegionDrawable(textureRegionGun);
        buttonWeaponGun = new ImageButton(regionDrawableGun);
        buttonWeaponGun.addListener((new ClickListener() {


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



        // Grenade
        grenade = new Texture(Gdx.files.internal("IconGrenade.png"));
        textureRegionGrenade = new TextureRegion(grenade);
        regionDrawableGrenade = new TextureRegionDrawable(textureRegionGrenade);
        buttonGrenade = new ImageButton(regionDrawableGrenade);
        buttonGrenade.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Grenade Button Clicked");
            }
        }));

        // Bazooka
        bazooka = new Texture(Gdx.files.internal("IconBazooka.png"));
        textureRegionBazooka = new TextureRegion(bazooka);
        regionDrawableBazooka = new TextureRegionDrawable(textureRegionBazooka);
        buttonBazooka = new ImageButton(regionDrawableBazooka);
        buttonBazooka.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Bazooka Button Clicked");
            }
        }));





        table = new Table();

        //Stage
        stage = new Stage();
        stage.addActor(table);
        stage.setDebugAll(true);



        table.setBackground( image.getDrawable());

        table.setPosition(1150,120);

        table.bottom().right();
        table.add(buttonWeaponGun);
        table.row();
        table.add(buttonGrenade);
        table.row();
        table.add(buttonBazooka);



        table.setSize(92, 147);
        stage.addActor(table);


    }


    @Override
    public void render(SpriteBatch batch, float delta) {
        stage.act(Gdx.graphics.getDeltaTime());

        stage.draw();

    }


    public Stage getInputProcessor() {
        return stage;
    }
}
