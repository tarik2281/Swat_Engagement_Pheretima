package de.paluno.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import de.paluno.game.Assets;
import de.paluno.game.gameobjects.Renderable;

public class WeaponUI implements Renderable {

    private PlayScreen playScreen;
    private Stage stage;
    private Texture weapon;
    private TextureRegion textureRegionWeapon;
    private TextureRegionDrawable regionDrawableWeapon;
    private ImageButton buttonWeapon;
    private Table table;
    private Image image;
    private Texture weaponPanel;
    private Button buttonGrenade, buttonBazooka, buttonGun, buttonSpecial;


    public WeaponUI(PlayScreen playScreen) {
        this.playScreen = playScreen;

        weaponPanel = new Texture(Gdx.files.internal("weaponpanel.png"));
        image = new Image((new TextureRegionDrawable(new TextureRegion(weaponPanel))));

        // Weapon
        weapon = new Texture(Gdx.files.internal("next.png"));
        textureRegionWeapon = new TextureRegion(weapon);
        regionDrawableWeapon = new TextureRegionDrawable(textureRegionWeapon);
        buttonWeapon = new ImageButton(regionDrawableWeapon);
        buttonWeapon.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Weapon Button Clicked");
            }
        }));

        // Grenade
        buttonGrenade = new ImageButton(new TextureRegionDrawable(new TextureRegion(playScreen.getAssetManager().get(Assets.iconGrenade))));
        buttonGrenade.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Grenade Button Clicked");
            }
        });


        // Bazooka
        buttonBazooka = new ImageButton(new TextureRegionDrawable(new TextureRegion(playScreen.getAssetManager().get(Assets.iconBazooka))));
        buttonBazooka.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Bazooka Button Clicked");
            }
        });

        // Gun
        buttonGun = new ImageButton(new TextureRegionDrawable(new TextureRegion(playScreen.getAssetManager().get(Assets.iconGun))));
        buttonGun.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Gun Button Clicked");
            }
        });

        // Special Weapon
        buttonSpecial = new ImageButton(new TextureRegionDrawable(new TextureRegion(playScreen.getAssetManager().get(Assets.iconGrenade))));
        buttonSpecial.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Special Weapon Button Clicked");
            }
        });


        table = new Table();
        //Stage
        stage = new Stage();
        stage.addActor(buttonGun);


        table.setBackground(image.getDrawable());
        table.setPosition(1260, 120);
        table.add(buttonGun).size(60, 60);
        table.add(buttonGrenade);
        table.add(buttonBazooka);
        table.setDebug(false);
        table.setSize(92, 147);
        stage.addActor(table);

    }

    public InputProcessor getInputProcessor() {
        return stage;
    }


    @Override
    public void render(SpriteBatch batch, float delta) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

    }


}
