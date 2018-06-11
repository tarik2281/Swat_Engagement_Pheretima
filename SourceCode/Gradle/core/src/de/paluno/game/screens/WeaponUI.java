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
import de.paluno.game.WeaponType;
import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.Renderable;
import de.paluno.game.gameobjects.Weapon;
import de.paluno.game.gameobjects.World;

public class WeaponUI implements Renderable {


    private PlayScreen playScreen;

    //takes the whole screen
    private Stage stage;
    // hold the image of our button
    private Texture gun, grenade, bazooka, specialWeapon;
    // defines rectangular area of a texture
    private TextureRegion textureRegionGun, textureRegionGrenade, textureRegionBazooka, textureRegionSpecialWeapon;
    // draws the texture in the given size
    private TextureRegionDrawable regionDrawableGun, regionDrawableGrenade, regionDrawableBazooka,regionDrawableSpecialWeapon;
    // Icons
    private ImageButton buttonGun, buttonGrenade, buttonBazooka, buttonSpecialWeapon;
    // Is implemented into the stage
    private Table table;

    // Background of the table
    private Image image;
    private Texture textureBackground;


    private Player player;
    private Weapon weapon;


    // TODO: Assetmanager nutzen

    WeaponUI(PlayScreen playScreen) {
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
                //  weapon = new Weapon(player,WeaponType.WEAPON_GUN);
                //  player.getCurrentWorm().equipWeapon(weapon);
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
            }
        }));

        // SpecialWeapon Button
//        specialWeapon = world.getAssetManager().get(Assets.specialWeapon);
//        textureRegionSpecialWeapon = new TextureRegion(specialWeapon);
//        regionDrawableSpecialWeapon = new TextureRegionDrawable(textureRegionSpecialWeapon);
//        buttonSpecialWeapon = new ImageButton(regionDrawableSpecialWeapon);
//        buttonSpecialWeapon.addListener((new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                System.out.println("SpecialWeapon Button Clicked");
//            }
//        }));


        stage = new Stage();
        table = new Table();

        table.setBackground(image.getDrawable());
        table.setPosition(1220, 120);

        // Positioning of Buttons
        table.top().right();
        table.add(buttonGun);
        table.row();
        table.add(buttonGrenade);
        table.row();
        table.add(buttonBazooka);
        //sets space to the edge of table
        table.pad(7);
        table.setSize(92, 147);
        stage.addActor(table);


    }

    public void setPlayer(Player player) {
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