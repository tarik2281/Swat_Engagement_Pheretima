package de.paluno.game;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class Assets {
    public static final AssetDescriptor<TiledMap> map =
            new AssetDescriptor<TiledMap>("map.tmx", TiledMap.class);

    public static final AssetDescriptor<Texture> ground =
            new AssetDescriptor<Texture>("Stone-0233.jpg", Texture.class);
    public static final AssetDescriptor<Texture> arrow =
            new AssetDescriptor<Texture>("Arrow.png", Texture.class);
    public static final AssetDescriptor<Texture> projectile =
            new AssetDescriptor<Texture>("Projectile.png", Texture.class);

    public static final AssetDescriptor<AnimationData> wormBreath =
            new AssetDescriptor<AnimationData>("wbrth1.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> wormWalk =
            new AssetDescriptor<AnimationData>("wwalk.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> wormEquipGun =
            new AssetDescriptor<AnimationData>("whgnlnk.xml", AnimationData.class);
    //WindIndicator
    public static final AssetDescriptor<Texture> windGreen =
            new AssetDescriptor<Texture>("Green", Texture.class);

    public static final AssetDescriptor<Texture> windOrange =
            new AssetDescriptor<Texture>("Orange", Texture.class);

    public static final AssetDescriptor<Texture> windRed =
            new AssetDescriptor<Texture>("Red", Texture.class);
}
