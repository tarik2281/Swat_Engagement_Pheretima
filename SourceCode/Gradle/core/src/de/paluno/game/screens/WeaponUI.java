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
import de.paluno.game.WeaponType;
import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.Renderable;
import de.paluno.game.gameobjects.Weapon;
import de.paluno.game.gameobjects.World;

public class WeaponUI implements Renderable {


    private PlayScreen playScreen;
    private Stage stage;
    private Texture gun, grenade, bazooka;
    private TextureRegion textureRegionGun, textureRegionGrenade, textureRegionBazooka;
    private TextureRegionDrawable regionDrawableGun, regionDrawableGrenade, regionDrawableBazooka;
    private ImageButton buttonGun, buttonGrenade, buttonBazooka;
    private Table table;
    private Image image;
    private Texture textureBackground;
    private Player player;
    private Weapon weapon;
    private World world;



    WeaponUI(PlayScreen playScreen) {
        this.playScreen = playScreen;
     //   this.player = new Player(1,world);
        textureBackground = new Texture(Gdx.files.internal("weaponUI.png"));
        image = new Image((new TextureRegionDrawable(new TextureRegion(textureBackground))));

        //Gun
        gun = new Texture(Gdx.files.internal("IconGun.png"));
        textureRegionGun = new TextureRegion(gun);
        regionDrawableGun = new TextureRegionDrawable(textureRegionGun);
        buttonGun = new ImageButton(regionDrawableGun);
        buttonGun.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Gun Button Clicked");
              //  weapon = new Weapon(player,WeaponType.WEAPON_GUN);
              //  player.getCurrentWorm().equipWeapon(weapon);
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
        stage.setDebugAll(false);


        table.setBackground(image.getDrawable());

        table.setPosition(1220, 120);


        // Positioning of Buttons
        table.top().right();
        table.add(buttonGun);
        table.row();
        table.add(buttonGrenade);
        table.row();
        table.add(buttonBazooka);
        table.pad(7); //sets space to the edge of table


        table.setSize(92, 147);
        stage.addActor(table);


    }


    @Override
    public void render(SpriteBatch batch, float delta) {
        stage.act(Gdx.graphics.getDeltaTime());

        stage.draw();

    }

    // Listener for Buttons
    public Stage getInputProcessor() {
        return stage;
    }
}
