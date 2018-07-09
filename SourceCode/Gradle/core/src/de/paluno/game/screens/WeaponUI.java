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

import java.util.ArrayList;

public class WeaponUI {

    private PlayScreen playScreen;
    private ElementGUI elementGUI;
    //takes the whole screen
    private Stage stage;
    // Icons
    private ImageButton buttonGun, buttonGrenade, buttonBazooka, buttonWeaponSpecial,
            buttonAirStrike, buttonTeleport, buttonMine, buttonArtillery, weaponMenuButton;
    // Is implemented into the stage
    private Table table, table2;

    // Background of the table
    private Image image, image2;
    private Player player;


    public WeaponUI(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.elementGUI = new ElementGUI();

        // Table Background
        image = new Image((new TextureRegionDrawable(new TextureRegion(playScreen.getAssetManager().get(Assets.weaponUI)))));
        image2 = new Image(new TextureRegionDrawable(new TextureRegion(playScreen.getAssetManager().get(Assets.weaponUI2))));

        //Gun ElementGUI
        buttonGun = elementGUI.createButton(playScreen.getAssetManager().get(Assets.iconGun));
        buttonGun.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Gun ElementGUI Clicked");
                player.equipWeapon(WeaponType.WEAPON_GUN);

            }
        }));


        // Grenade ElementGUI
        buttonGrenade = elementGUI.createButton(playScreen.getAssetManager().get(Assets.iconGrenade));
        buttonGrenade.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Grenade ElementGUI Clicked");
                player.equipWeapon(WeaponType.WEAPON_GRENADE);
            }
        }));

        // Bazooka ElementGUI
        buttonBazooka = elementGUI.createButton(playScreen.getAssetManager().get(Assets.iconBazooka));
        buttonBazooka.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Bazooka ElementGUI Clicked");
                player.equipWeapon(WeaponType.WEAPON_BAZOOKA);
            }
        }));

        // SpecialWeapon ElementGUI
        buttonWeaponSpecial = elementGUI.createButton(playScreen.getAssetManager().get(Assets.iconSpecial));
        buttonWeaponSpecial.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("SpecialWeapon ElementGUI Clicked");
                player.equipWeapon(WeaponType.WEAPON_SPECIAL);
            }
        }));

        // Airstrike Button
        airstrike = playScreen.getAssetManager().get(Assets.iconAirstrike);
        textureRegionAirstrike = new TextureRegion(airstrike);
        regionDrawableAirstrike = new TextureRegionDrawable(textureRegionAirstrike);
        buttonAirstrike = new ImageButton(regionDrawableAirstrike);
        buttonAirstrike.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("SpecialWeapon Button Clicked");
                player.equipWeapon(WeaponType.WEAPON_AIRSTRIKE);
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
        table.row();
        table.add(buttonAirstrike);
        stage.setDebugAll(false);

        buttonAirstrike.padTop(5);

        //sets space to the edge of table
        table.padRight(7);
        table.padTop(12);
        table.setSize(92, 150);
        stage.addActor(table);


        // Airstrike ElementGUI
        buttonAirStrike = elementGUI.createButton(playScreen.getAssetManager().get(Assets.iconAirStrike));
        buttonAirStrike.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Airstrike ElementGUI Clicked");
                //player.equipWeapon(WeaponType.WEAPON_SPECIAL);
            }
        }));

        // Teleport ElementGUI
        buttonTeleport = elementGUI.createButton(playScreen.getAssetManager().get(Assets.iconTeleport));
        buttonTeleport.addListener((new ClickListener() {

//                        Vector3 selectedTeleportPosition = player.getWorld().getCamera().getWorldCamera().unproject(new Vector3(x, y, 0));
//                        player.getCurrentWorm().getBody().setTransform(selectedTeleportPosition.x, selectedTeleportPosition.y, 0);

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Teleport Clicked");
                player.equipWeapon(WeaponType.TELEPORTER);

            }
        }));
        // player.equipWeapon(WeaponType.WEAPON_SPECIAL);


        // Mine ElementGUI
        buttonMine = elementGUI.createButton(playScreen.getAssetManager().get(Assets.iconMine));
        buttonMine.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Mine ElementGUI Clicked");
                //  player.equipWeapon(WeaponType.WEAPON_SPECIAL);
            }
        }));

        // SpecialWeapon ElementGUI
        buttonArtillery = elementGUI.createButton(playScreen.getAssetManager().get(Assets.iconArtillery));
        buttonArtillery.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Artillery ElementGUI Clicked");
                // player.equipWeapon(WeaponType.WEAPON_SPECIAL);
            }
        }));

        //Weapon Menu Button
        weaponMenuButton = elementGUI.createButton(playScreen.getAssetManager().get(Assets.weaponMenuButton));
        weaponMenuButton.addListener(new ClickListener() {
            int weaponClick = 0;

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (weaponClick == 0) {
                    table.setVisible(true);
                    table2.setVisible(true);
                    weaponClick = 1;
                } else {
                    table.setVisible(false);
                    table2.setVisible(false);
                    weaponClick = 0;
                }


                System.out.println("Weapon Menu Clicked");
            }
        });
        weaponMenuButton.setPosition(1275, 320);

        table2 = new Table();

        table2.setBackground(image2.getDrawable());
        table2.setPosition(1180, 25);

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

        stage.addActor(weaponMenuButton);
        stage.addActor(table2);
        // stage.setDebugAll(true);

        table.setVisible(false);
        table2.setVisible(false);
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


