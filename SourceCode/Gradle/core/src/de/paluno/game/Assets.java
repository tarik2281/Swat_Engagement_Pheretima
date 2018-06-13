package de.paluno.game;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class Assets {
    public static final AssetDescriptor<Texture> menuBackground =
            new AssetDescriptor<Texture>("MENU.png", Texture.class);
    public static final AssetDescriptor<Texture> map1Thumbnail =
            new AssetDescriptor<Texture>("Map1SEPThumb.png", Texture.class);
    public static final AssetDescriptor<Texture> map2Thumbnail =
            new AssetDescriptor<Texture>("Map2SEPThumb.png", Texture.class);
    public static final AssetDescriptor<Texture> map3Thumbnail =
            new AssetDescriptor<Texture>("Map3SEPThumb.png", Texture.class);
    public static final AssetDescriptor<Texture> map4Thumbnail =
            new AssetDescriptor<Texture>("Map4SEPThumb.png", Texture.class);
    public static final AssetDescriptor<Texture> playButton =
            new AssetDescriptor<Texture>("PlayButton.png", Texture.class);
    public static final AssetDescriptor<Texture> worms1Button =
            new AssetDescriptor<Texture>("Worms1.png", Texture.class);
    public static final AssetDescriptor<Texture> worms2Button =
            new AssetDescriptor<Texture>("Worms2.png", Texture.class);
    public static final AssetDescriptor<Texture> worms3Button =
            new AssetDescriptor<Texture>("Worms3.png", Texture.class);
    public static final AssetDescriptor<Texture> worms4Button =
            new AssetDescriptor<Texture>("Worms4.png", Texture.class);
    public static final AssetDescriptor<Texture> worms5Button =
            new AssetDescriptor<Texture>("Worms5.png", Texture.class);

    public static final AssetDescriptor<TiledMap> map1 =
            new AssetDescriptor<TiledMap>("Map1SEP.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> map2 =
            new AssetDescriptor<TiledMap>("Map2SEP.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> map3 =
            new AssetDescriptor<TiledMap>("Map3SEP.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> map4 =
            new AssetDescriptor<TiledMap>("Map4SEP.tmx", TiledMap.class);

    public static AssetDescriptor<TiledMap> getMapByIndex(int index) {
        switch (index) {
            case 0:
                return map1;
            case 1:
                return map2;
            case 2:
                return map3;
            case 3:
                return map4;
        }

        return null;
    }

    public static final AssetDescriptor<Texture> arrow =
            new AssetDescriptor<Texture>("Arrow.png", Texture.class);
    
    public static final AssetDescriptor<Texture> gameOverScreen1 =
            new AssetDescriptor<Texture>("GameOverPlay1.png", Texture.class);
    public static final AssetDescriptor<Texture> gameOverScreen2 =
            new AssetDescriptor<Texture>("GameOverPlay2.png", Texture.class);

    public static final AssetDescriptor<AnimationData> wormBreath =
            new AssetDescriptor<AnimationData>("WormBreath.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> wormWalk =
            new AssetDescriptor<AnimationData>("WormWalk.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> wormFly =
            new AssetDescriptor<AnimationData>("WormFly.xml", AnimationData.class);
    // WeaponUI
    public static final AssetDescriptor<Texture> weaponUI =
            new AssetDescriptor<Texture>("weaponUI.png", Texture.class);

    //WindIndicator
    public static final AssetDescriptor<Texture> windGreen =
            new AssetDescriptor<Texture>("Green.png", Texture.class);

    public static final AssetDescriptor<Texture> windOrange =
            new AssetDescriptor<Texture>("Orange.png", Texture.class);

    public static final AssetDescriptor<Texture> windRed =
            new AssetDescriptor<Texture>("Red.png", Texture.class);

    public static final AssetDescriptor<Texture> iconGun =
            new AssetDescriptor<Texture>("IconGun.png", Texture.class);
    public static final AssetDescriptor<Texture> iconGrenade =
            new AssetDescriptor<Texture>("IconGrenade.png", Texture.class);
    public static final AssetDescriptor<Texture> iconBazooka =
            new AssetDescriptor<Texture>("IconBazooka.png", Texture.class);
    public static final AssetDescriptor<Texture> iconSpecial =
            new AssetDescriptor<Texture>("IconSpecial.png", Texture.class);

    public static final AssetDescriptor<Texture> projectileGun =
            new AssetDescriptor<Texture>("ProjectileGun.png", Texture.class);
    public static final AssetDescriptor<Texture> projectileGrenade =
            new AssetDescriptor<Texture>("ProjectileGrenade.png", Texture.class);
    public static final AssetDescriptor<Texture> projectileBazooka =
            new AssetDescriptor<Texture>("ProjectileBazooka.png", Texture.class);
    public static final AssetDescriptor<Texture> projectileSpecial =
            new AssetDescriptor<Texture>("ProjectileSpecial.png", Texture.class);

    public static final AssetDescriptor<AnimationData> weaponGun =
            new AssetDescriptor<AnimationData>("WeaponGun.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> weaponGrenade =
            new AssetDescriptor<AnimationData>("WeaponGrenade.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> weaponBazooka =
            new AssetDescriptor<AnimationData>("WeaponBazooka.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> weaponSpecial =
            new AssetDescriptor<AnimationData>("WeaponSpecial.xml", AnimationData.class);

    public static void loadAssets(AssetManager assetManager, AssetDescriptor[] assets) {
        for (AssetDescriptor asset : assets)
            assetManager.load(asset);
    }

    public static final AssetDescriptor[] MenuScreenAssets = {
            menuBackground, map1Thumbnail, map2Thumbnail, map3Thumbnail, map4Thumbnail, playButton, worms1Button,
            worms2Button, worms3Button, worms4Button, worms5Button
    };

    public static final AssetDescriptor[] PlayScreenAssets = {
            arrow, wormBreath, wormWalk, wormFly, iconGun, iconGrenade, iconBazooka, projectileGun, projectileGrenade, projectileBazooka,
            weaponGun, weaponGrenade, weaponBazooka, iconSpecial, projectileSpecial, weaponSpecial, weaponUI, windGreen, windOrange, windRed
    };

    public static final AssetDescriptor[] GameOverScreenAssets = {
            gameOverScreen1, gameOverScreen2
    };
}
