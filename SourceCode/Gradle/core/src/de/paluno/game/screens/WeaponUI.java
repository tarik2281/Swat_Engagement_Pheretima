package de.paluno.game.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.paluno.game.Assets;
import de.paluno.game.gameobjects.WeaponType;
import de.paluno.game.worldhandlers.WorldHandler;

public class WeaponUI {

    //takes the whole screen
    // Is implemented into the stage
    private Table table, table2, tableMain;

    private WorldHandler worldHandler;

    public WeaponUI(AssetManager manager, ElementGUI elementGUI) {

        // Table Background
        Image image = elementGUI.createBackground(manager.get(Assets.weaponUI));
        Image image2 = elementGUI.createBackground(manager.get(Assets.weaponUI2));

        //Gun
        ImageButton buttonGun = elementGUI.createWeaponButton(manager.get(Assets.iconGun));
        buttonGun.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Gun ElementGUI Clicked");
                worldHandler.applyEquipWeapon(WeaponType.WEAPON_GUN);
                table.setVisible(false);
                table2.setVisible(false);
            }
        }));


        // Grenade
        ImageButton buttonGrenade = elementGUI.createWeaponButton((manager.get(Assets.iconGrenade)));
        buttonGrenade.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Grenade ElementGUI Clicked");
                worldHandler.applyEquipWeapon(WeaponType.WEAPON_GRENADE);
                table.setVisible(false);
                table2.setVisible(false);

            }
        }));

        // Bazooka
        ImageButton buttonBazooka = elementGUI.createWeaponButton(manager.get(Assets.iconBazooka));
        buttonBazooka.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Bazooka ElementGUI Clicked");
                worldHandler.applyEquipWeapon(WeaponType.WEAPON_BAZOOKA);
                table.setVisible(false);
                table2.setVisible(false);
            }
        }));

        // SpecialWeapon
        ImageButton buttonWeaponSpecial = elementGUI.createWeaponButton(manager.get(Assets.iconSpecial));
        buttonWeaponSpecial.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("SpecialWeapon ElementGUI Clicked");
                worldHandler.applyEquipWeapon(WeaponType.WEAPON_SPECIAL);
                table.setVisible(false);
                table2.setVisible(false);
            }
        }));

        // Airstrike
        ImageButton buttonAirStrike = elementGUI.createWeaponButton(manager.get(Assets.iconAirStrike));
        buttonAirStrike.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Airstrike ElementGUI Clicked");
                worldHandler.applyEquipWeapon(WeaponType.WEAPON_AIRSTRIKE);
                table.setVisible(false);
                table2.setVisible(false);
            }
        }));

        // Teleport
        ImageButton buttonTeleport = elementGUI.createWeaponButton(manager.get(Assets.iconTeleport));
        buttonTeleport.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Teleport Clicked");
                table.setVisible(false);
                table2.setVisible(false);
                worldHandler.applyEquipWeapon(WeaponType.WEAPON_TELEPORTER);

            }
        }));


        // Mine
        ImageButton buttonMine = elementGUI.createWeaponButton(manager.get(Assets.iconMine));
        buttonMine.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Mine ElementGUI Clicked");
                table.setVisible(false);
                table2.setVisible(false);
                worldHandler.applyEquipWeapon(WeaponType.WEAPON_MINE);
            }
        }));

        // SpecialWeapon
        ImageButton buttonArtillery = elementGUI.createWeaponButton(manager.get(Assets.iconArtillery));
        buttonArtillery.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Artillery ElementGUI Clicked");
                table.setVisible(false);
                table2.setVisible(false);
                worldHandler.applyEquipWeapon(WeaponType.WEAPON_TURRET);
            }
        }));

        //WeaponMenu
        ImageButton weaponMenuButton = elementGUI.createWeaponButton(manager.get(Assets.weaponMenuButton));
        weaponMenuButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!table.isVisible()) {
                    table.setVisible(true);
                    table2.setVisible(true);
                } else {
                    table.setVisible(false);
                    table2.setVisible(false);
                }

                System.out.println("Weapon Menu Clicked");
            }
        });

        table = new Table().padRight(7).padTop(2);
        table.setBackground(image.getDrawable());

        // Positioning of Buttons
        table.add(buttonGun);
        table.row();
        table.add(buttonGrenade);
        table.row();
        table.add(buttonBazooka);
        table.row();
        table.add(buttonWeaponSpecial);


        table2 = new Table().padTop(35);
        table2.setBackground(image2.getDrawable());
        table2.add(buttonAirStrike.pad(5));
        table2.add(buttonTeleport.pad(5));
        table2.add(buttonArtillery.pad(5));
        table2.add(buttonMine.pad(5));

        tableMain = new Table();
        tableMain.setFillParent(true);
        tableMain.right();
        tableMain.add(weaponMenuButton).right().row();
        tableMain.add(table.right()).size(92, 150).right().padTop(10).row();
        tableMain.add(table2).padTop(10).size(166,75);

        table.setVisible(false);
        table2.setVisible(false);
    }

    public Table getTable() {
        return tableMain;
    }

    public void setWorldHandler(WorldHandler worldHandler) {
        this.worldHandler = worldHandler;
    }
}


