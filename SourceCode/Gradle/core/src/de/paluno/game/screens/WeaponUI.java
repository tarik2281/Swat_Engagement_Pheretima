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
import de.paluno.game.gameobjects.Renderable;

public class WeaponUI implements Renderable {

    // TODO:  density of projectiles ok?


    private PlayScreen playScreen;

    //takes the whole screen
    private Stage stage;
    // hold the image of our button
    private Texture gun, grenade, bazooka, weaponSpecial, airStrike, teleport, mine, artillery;
    // defines rectangular area of a texture
    private TextureRegion textureRegionGun, textureRegionGrenade, textureRegionBazooka, textureRegionWeaponSpecial,
            textureRegionAirStrike, textureRegionTeleport, textureRegionMine, textureRegionArtillery;
    // draws the texture in the given size
    private TextureRegionDrawable regionDrawableGun, regionDrawableGrenade, regionDrawableBazooka, regionDrawableWeaponSpecial,
            regionDrawableAirStrike, regionDrawableTeleport, regionDrawableMine, regionDrawableArtillery;
    // Icons
    private ImageButton buttonGun, buttonGrenade, buttonBazooka, buttonWeaponSpecial,
            buttonAirStrike, buttonTeleport, buttonMine, buttonArtillery;
    // Is implemented into the stage
    private Table table, table2;

    // Background of the table
    private Image image, image2;
    private Texture textureBackground, textureBackground2;
    private Player player;


    public WeaponUI(PlayScreen playScreen) {
        this.playScreen = playScreen;


        // Table Background
        textureBackground = playScreen.getAssetManager().get(Assets.weaponUI);
        image = new Image((new TextureRegionDrawable(new TextureRegion(textureBackground))));

        textureBackground2 = playScreen.getAssetManager().get(Assets.weaponUI2);
        image2 = new Image(new TextureRegionDrawable(new TextureRegion(textureBackground2)));

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
        table.setSize(92, 150);
        stage.addActor(table);


        // Airstrike Button
        airStrike = playScreen.getAssetManager().get(Assets.iconAirStrike);
        textureRegionAirStrike = new TextureRegion(airStrike);
        regionDrawableAirStrike = new TextureRegionDrawable(textureRegionAirStrike);
        buttonAirStrike = new ImageButton(regionDrawableAirStrike);
        buttonAirStrike.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Airstrike Button Clicked");
                //player.equipWeapon(WeaponType.WEAPON_SPECIAL);
            }
        }));

        // Teleport Button
        teleport = playScreen.getAssetManager().get(Assets.iconTeleport);
        textureRegionTeleport = new TextureRegion(teleport);
        regionDrawableTeleport = new TextureRegionDrawable(textureRegionTeleport);
        buttonTeleport = new ImageButton(regionDrawableTeleport);
        buttonTeleport.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Teleport Button Clicked");
               // player.equipWeapon(WeaponType.WEAPON_SPECIAL);
            }
        }));

        // Mine Button
        mine = playScreen.getAssetManager().get(Assets.iconMine);
        textureRegionMine = new TextureRegion(mine);
        regionDrawableMine = new TextureRegionDrawable(textureRegionMine);
        buttonMine = new ImageButton(regionDrawableMine);
        buttonMine.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Mine Button Clicked");
              //  player.equipWeapon(WeaponType.WEAPON_SPECIAL);
            }
        }));

        // SpecialWeapon Button
        artillery = playScreen.getAssetManager().get(Assets.iconArtillery);
        textureRegionArtillery = new TextureRegion(artillery);
        regionDrawableArtillery = new TextureRegionDrawable(textureRegionArtillery);
        buttonArtillery = new ImageButton(regionDrawableArtillery);
        buttonArtillery.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Artillery Button Clicked");
               // player.equipWeapon(WeaponType.WEAPON_SPECIAL);
            }
        }));

        table2 = new Table();

        table2.setBackground(image2.getDrawable());
        table2.setPosition(1180, 2);

        table2.add(buttonAirStrike);
        table2.add(buttonTeleport);
        table2.add(buttonArtillery);
        table2.add(buttonMine);
        table2.setSize(166, 75);
        table2.padTop(35);
        stage.setDebugAll(false);
        buttonMine.pad(5);

        buttonArtillery.pad(5);
        buttonAirStrike.pad(5);
        buttonTeleport.pad(5);


        stage.addActor(table2);
       // stage.setDebugAll(true);




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

    public void setPlayer(Player player) {
        this.player = player;
    }
}
