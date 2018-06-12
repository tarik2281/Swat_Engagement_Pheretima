package de.paluno.game;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

public class Assets {
    public static final AssetDescriptor<TiledMap> map =
            new AssetDescriptor<TiledMap>("map.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> map1 =
            new AssetDescriptor<TiledMap>("Map1SEP.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> map2 =
            new AssetDescriptor<TiledMap>("Map2SEP.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> map3 =
            new AssetDescriptor<TiledMap>("Map3SEP.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> map4 =
            new AssetDescriptor<TiledMap>("Map4SEP.tmx", TiledMap.class);

    public static final AssetDescriptor[] maps = {
            map1, map2, map3, map4
    };

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

    public static final AssetDescriptor[] PlayScreenAssets = new AssetDescriptor[] {
            map, arrow, wormBreath, wormWalk, wormFly, iconGun, iconGrenade, iconBazooka, projectileGun, projectileGrenade, projectileBazooka,
            weaponGun, weaponGrenade, weaponBazooka, iconSpecial, projectileSpecial, weaponSpecial, weaponUI, map1, map2, map3, map4
    };
}
