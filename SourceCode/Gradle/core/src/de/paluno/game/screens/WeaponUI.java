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
import de.paluno.game.Assets;
import de.paluno.game.gameobjects.WeaponType;
import de.paluno.game.gameobjects.Player;

public class WeaponUI {

    // TODO:  density of projectiles ok?


    private PlayScreen playScreen;

    //takes the whole screen
    private Stage stage;
    // hold the image of our button
    private Texture gun, grenade, bazooka, weaponSpecial;
    // defines rectangular area of a texture
    private TextureRegion textureRegionGun, textureRegionGrenade, textureRegionBazooka, textureRegionWeaponSpecial;
    // draws the texture in the given size
    private TextureRegionDrawable regionDrawableGun, regionDrawableGrenade, regionDrawableBazooka, regionDrawableWeaponSpecial;
    // Icons
    private ImageButton buttonGun, buttonGrenade, buttonBazooka, buttonWeaponSpecial;
    // Is implemented into the stage
    private Table table;

    // Background of the table
    private Image image;
    private Texture textureBackground;
    private Player player;


    public WeaponUI(PlayScreen playScreen) {
        this.playScreen = playScreen;


        // Table Background
        textureBackground = playScreen.getAssetManager().get(Assets.weaponUI);
        image = new Image((new TextureRegionDrawable(new TextureRegion(textureBackground))));

        //Gun Button
        gun = playScreen.getAssetManager().get(Assets.iconGun);
        textureRegionGun = new TextureRegion(gun);
        regionDrawableGun = new TextureRegionDrawable(textureRegionGun);
        buttonGun = new ImageButton(regionDrawableGun);
        buttonGun.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Gun Button Clicked");
                player.equipWeapon(WeaponType.WEAPON_GUN);

            }
        }));


        // Grenade Button
        grenade = playScreen.getAssetManager().get(Assets.iconGrenade);
        textureRegionGrenade = new TextureRegion(grenade);
        regionDrawableGrenade = new TextureRegionDrawable(textureRegionGrenade);
        buttonGrenade = new ImageButton(regionDrawableGrenade);
        buttonGrenade.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Grenade Button Clicked");
                player.equipWeapon(WeaponType.WEAPON_GRENADE);
            }
        }));

        // Bazooka Button
        bazooka = playScreen.getAssetManager().get(Assets.iconBazooka);
        textureRegionBazooka = new TextureRegion(bazooka);
        regionDrawableBazooka = new TextureRegionDrawable(textureRegionBazooka);
        buttonBazooka = new ImageButton(regionDrawableBazooka);
        buttonBazooka.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Bazooka Button Clicked");
                player.equipWeapon(WeaponType.WEAPON_BAZOOKA);
            }
        }));

        // SpecialWeapon Button
        weaponSpecial = playScreen.getAssetManager().get(Assets.iconSpecial);
        textureRegionWeaponSpecial = new TextureRegion(weaponSpecial);
        regionDrawableWeaponSpecial = new TextureRegionDrawable(textureRegionWeaponSpecial);
        buttonWeaponSpecial = new ImageButton(regionDrawableWeaponSpecial);
        buttonWeaponSpecial.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("SpecialWeapon Button Clicked");
                player.equipWeapon(WeaponType.WEAPON_SPECIAL);
            }
        }));


        stage = new Stage();
        table = new Table();

        table.setBackground(image.getDrawable());
        table.setPosition(1255, 120);

        // Positioning of Buttons
        table.top().right();
        table.row();
        table.add(buttonGun);
        table.row();
        table.add(buttonGrenade);
        table.row();
        table.add(buttonBazooka);
        table.row();
        table.add(buttonWeaponSpecial);
        stage.setDebugAll(false);

        //sets space to the edge of table
        table.padRight(7);
        table.padTop(12);
        table.setSize(92, 147);
        stage.addActor(table);


    }


    public void render(SpriteBatch batch, float delta) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

    }

    // Listener for Buttons
    public Stage getInputProcessor() {
        return stage;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
