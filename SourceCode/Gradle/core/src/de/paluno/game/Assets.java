package de.paluno.game;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class Assets {
    public static final AssetDescriptor<TiledMap> map =
            new AssetDescriptor<TiledMap>("map.tmx", TiledMap.class);

    public static final AssetDescriptor<Texture> arrow =
            new AssetDescriptor<Texture>("Arrow.png", Texture.class);

    public static final AssetDescriptor<AnimationData> wormBreath =
            new AssetDescriptor<AnimationData>("WormBreath.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> wormWalk =
            new AssetDescriptor<AnimationData>("WormWalk.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> wormFly =
            new AssetDescriptor<AnimationData>("WormFly.xml", AnimationData.class);

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

    public static final AssetDescriptor<Texture> projectileGun =
            new AssetDescriptor<Texture>("ProjectileGun.png", Texture.class);
    public static final AssetDescriptor<Texture> projectileGrenade =
            new AssetDescriptor<Texture>("ProjectileGrenade.png", Texture.class);
    public static final AssetDescriptor<Texture> projectileBazooka =
            new AssetDescriptor<Texture>("ProjectileBazooka.png", Texture.class);

    public static final AssetDescriptor<AnimationData> weaponGun =
            new AssetDescriptor<AnimationData>("WeaponGun.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> weaponGrenade =
            new AssetDescriptor<AnimationData>("WeaponGrenade.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> weaponBazooka =
            new AssetDescriptor<AnimationData>("WeaponBazooka.xml", AnimationData.class);

    public static final AssetDescriptor[] PlayScreenAssets = new AssetDescriptor[] {
            map, arrow, wormBreath, wormWalk, wormFly, iconGun, iconGrenade, iconBazooka, projectileGun, projectileGrenade, projectileBazooka,
            weaponGun, weaponGrenade, weaponBazooka
    };
}
