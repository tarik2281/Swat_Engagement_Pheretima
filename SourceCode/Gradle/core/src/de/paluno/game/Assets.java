package de.paluno.game;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class Assets {
    private static final TextureLoader.TextureParameter waterParameters;

    static {
        waterParameters = new TextureLoader.TextureParameter();
        waterParameters.wrapU = Texture.TextureWrap.Repeat;
        waterParameters.wrapV = Texture.TextureWrap.ClampToEdge;
    }

    public static final AssetDescriptor<Texture> waterTexture =
            new AssetDescriptor<Texture>("WaterTexture.png", Texture.class, waterParameters);

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
    public static final AssetDescriptor<Texture> menuButton =
            new AssetDescriptor<Texture>("MenuButton.png", Texture.class);
    public static final AssetDescriptor<Texture> worms1Button =
            new AssetDescriptor<Texture>("Worms1.png", Texture.class);
    public static final AssetDescriptor<Texture> worms2Button =
            new AssetDescriptor<Texture>("Worms2.png", Texture.class);
    public static final AssetDescriptor<Texture> worms3Button =
            new AssetDescriptor<Texture>("Worms3.png", Texture.class);
    public static final AssetDescriptor<Texture> worms4Button =
            new AssetDescriptor<Texture>("Worms4.png", Texture.class);
    public static final AssetDescriptor<Texture> worms5Button =
            new AssetDescriptor<Texture>("Worms5.png", Texture.class, new TextureLoader.TextureParameter());

    public static final AssetDescriptor<Map> map1 =
            new AssetDescriptor<Map>("Map1SEP.tmx", Map.class);
    public static final AssetDescriptor<Map> map2 =
            new AssetDescriptor<Map>("Map2SEP.tmx", Map.class);
    public static final AssetDescriptor<Map> map3 =
            new AssetDescriptor<Map>("Map3SEP.tmx", Map.class);
    public static final AssetDescriptor<Map> map4 =
            new AssetDescriptor<Map>("Map4SEP.tmx", Map.class);

    public static AssetDescriptor<Map> getMapByIndex(int index) {
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
    public static final AssetDescriptor<Texture> airstrikeCrosshair =
            new AssetDescriptor<Texture>("AirstrikeCrosshair.png", Texture.class);

    public static final AssetDescriptor<Texture> gameOverScreen =
            new AssetDescriptor<Texture>("GameOverScreen.png", Texture.class);

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
    public static final AssetDescriptor<Texture> iconAirstrike =
            new AssetDescriptor<Texture>("IconAirstrike.png", Texture.class);

    public static final AssetDescriptor<Texture> projectileGun =
            new AssetDescriptor<Texture>("ProjectileGun.png", Texture.class);
    public static final AssetDescriptor<Texture> projectileGrenade =
            new AssetDescriptor<Texture>("ProjectileGrenade.png", Texture.class);
    public static final AssetDescriptor<Texture> projectileBazooka =
            new AssetDescriptor<Texture>("ProjectileBazooka.png", Texture.class);
    public static final AssetDescriptor<Texture> projectileSpecial =
            new AssetDescriptor<Texture>("ProjectileSpecial.png", Texture.class);
    public static final AssetDescriptor<Texture> projectileAirstrike =
            new AssetDescriptor<Texture>("ProjectileAirstrike.png", Texture.class);
    public static final AssetDescriptor<Texture> projectileMine =
            new AssetDescriptor<Texture>("ProjectileMine.png", Texture.class);
    public static final AssetDescriptor<Texture> projectileTurret =
            new AssetDescriptor<Texture>("ProjectileTurret.png", Texture.class);

    public static final AssetDescriptor<Texture> crate =
            new AssetDescriptor<Texture>("crate.png", Texture.class);
    public static final AssetDescriptor<Texture> chute =
            new AssetDescriptor<Texture>("chute.png", Texture.class);

    public static final AssetDescriptor<Texture> weaponTurret =
            new AssetDescriptor<Texture>("WeaponTurret.png", Texture.class);
    public static final AssetDescriptor<AnimationData> weaponGun =
            new AssetDescriptor<AnimationData>("WeaponGun.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> weaponGrenade =
            new AssetDescriptor<AnimationData>("WeaponGrenade.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> weaponBazooka =
            new AssetDescriptor<AnimationData>("WeaponBazooka.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> weaponSpecial =
            new AssetDescriptor<AnimationData>("WeaponSpecial.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> weaponAirstrike =
            new AssetDescriptor<AnimationData>("WeaponAirstrike.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> weaponMine =
            new AssetDescriptor<AnimationData>("WeaponMine.xml", AnimationData.class);
    public static final AssetDescriptor<AnimationData> weapon =
            new AssetDescriptor<AnimationData>("WeaponMine.xml", AnimationData.class);

    public static final AssetDescriptor<Music> menuSound =
    		new AssetDescriptor<Music>("MenuScreen_ThemeSong.mp3", Music.class);
    public static final AssetDescriptor<Music> tickSound =
    		new AssetDescriptor<Music>("MenuScreen_ClickSound.mp3", Music.class);
    public static final AssetDescriptor<Sound> gameOverSound =
    		new AssetDescriptor<Sound>("GameOverScreen_CrowdCheer.mp3", Sound.class);
    public static final AssetDescriptor<Sound> grenadeExplosionSound =
    		new AssetDescriptor<Sound>("GrenadeExplosion2.mp3", Sound.class);
    public static final AssetDescriptor<Sound> bazookaShotSound =
    		new AssetDescriptor<Sound>("ShotGunFire.wav", Sound.class);
    public static final AssetDescriptor<Sound> gunShotSound =
    		new AssetDescriptor<Sound>("ShotGunFire3.mp3", Sound.class);
    public static final AssetDescriptor<Sound> gunRelease =
    		new AssetDescriptor<Sound>("GunRelease.mp3", Sound.class);
    public static final AssetDescriptor<Sound> airstrikeSound =
    		new AssetDescriptor<Sound>("Airstrike.WAV", Sound.class);
    public static final AssetDescriptor<Sound> airstrikeExplosion =
    		new AssetDescriptor<Sound>("AirstrikeExplosion.WAV", Sound.class);
    public static final AssetDescriptor<Sound> airballSound =
    		new AssetDescriptor<Sound>("NoHitNoExplosion.WAV", Sound.class);
    public static final AssetDescriptor<Sound> raiseLimitSound =
    		new AssetDescriptor<Sound>("UnderWaterLoop.WAV", Sound.class);
    public static final AssetDescriptor<Sound> map1Sound =
    		new AssetDescriptor<Sound>("Map1_ArabianNight.mp3", Sound.class);
    public static final AssetDescriptor<Sound> map2Sound =
    		new AssetDescriptor<Sound>("Map2_BuildingNight.mp3", Sound.class);
    public static final AssetDescriptor<Sound> map3Sound =
    		new AssetDescriptor<Sound>("Map3_CamelotDay.mp3", Sound.class);
    public static final AssetDescriptor<Sound> map4Sound =
    		new AssetDescriptor<Sound>("Map4_PrehistoricDay.mp3", Sound.class);
    public static final AssetDescriptor<Sound> walkLoop =
    		new AssetDescriptor<Sound>("WalkLoop.wav", Sound.class);
    public static final AssetDescriptor<Sound> noAmmo =
    		new AssetDescriptor<Sound>("NoAmmoWarning.wav", Sound.class);
    public static final AssetDescriptor<Sound> fallDown =
    		new AssetDescriptor<Sound>("FallDown.wav", Sound.class);
    public static final AssetDescriptor<Sound> roundStart =
    		new AssetDescriptor<Sound>("StartRound.wav", Sound.class);
    public static final AssetDescriptor<Sound> landSound =
    		new AssetDescriptor<Sound>("WormLanding.wav", Sound.class);
    public static final AssetDescriptor<Sound> virusSound =
    		new AssetDescriptor<Sound>("Cough2.wav", Sound.class);
    public static final AssetDescriptor<Sound> throwSound =
    		new AssetDescriptor<Sound>("Throw.mp3", Sound.class);
    public static final AssetDescriptor<Sound> windMedium =
    		new AssetDescriptor<Sound>("WindMedium.mp3", Sound.class);
    public static final AssetDescriptor<Sound> windHeavy =
    		new AssetDescriptor<Sound>("WindHeavy.mp3", Sound.class);
    public static final AssetDescriptor<Sound> targetSound =
    		new AssetDescriptor<Sound>("TargetAquired.mp3", Sound.class);
    public static final AssetDescriptor<Sound> grenadeContact =
    		new AssetDescriptor<Sound>("GrenadeImpact2.mp3", Sound.class);
    public static final AssetDescriptor<Sound> onGroundSound =
    		new AssetDescriptor<Sound>("Thud1.mp3", Sound.class);
    public static final AssetDescriptor<Sound> destroySound =
    		new AssetDescriptor<Sound>("DestroySound.mp3", Sound.class);
    public static final AssetDescriptor<Sound> airstrikeUse =
    		new AssetDescriptor<Sound>("PickUpUtility.mp3", Sound.class);
    public static final AssetDescriptor<Sound> headshotSound =
    		new AssetDescriptor<Sound>("HeadshotSound.mp3", Sound.class);
    public static final AssetDescriptor<Sound> airDropFall = 
    		new AssetDescriptor<Sound>("AirDropFall.wav", Sound.class);
    public static final AssetDescriptor<Sound> lootEquip = 
    		new AssetDescriptor<Sound>("LootEquip.mp3", Sound.class);



    public static void loadAssets(AssetManager assetManager, AssetDescriptor[] assets) {
        for (AssetDescriptor asset : assets)
            assetManager.load(asset);
    }

    public static final AssetDescriptor[] MenuScreenAssets = {
            menuBackground, map1Thumbnail, map2Thumbnail, map3Thumbnail, map4Thumbnail, playButton, worms1Button,
            worms2Button, worms3Button, worms4Button, worms5Button
    };

    public static final AssetDescriptor[] PlayScreenAssets = {
            airstrikeCrosshair, arrow, wormBreath, wormWalk, wormFly, iconGun, iconGrenade, iconBazooka, projectileGun, projectileGrenade, projectileBazooka,
            weaponGun, weaponGrenade, weaponBazooka, iconSpecial, projectileSpecial, weaponSpecial, iconAirstrike, projectileAirstrike, weaponAirstrike, weaponUI, windGreen, windOrange, windRed,
            weaponMine, projectileMine,weaponTurret ,projectileTurret, crate, chute, waterTexture
    };

    public static final AssetDescriptor[] GameOverScreenAssets = {
    		gameOverScreen, menuButton
    };

    public static final AssetDescriptor[] Music = {
    		menuSound, tickSound, gameOverSound, grenadeExplosionSound, gunShotSound, gunRelease, airstrikeSound, airstrikeExplosion,
    		airballSound, bazookaShotSound, raiseLimitSound, map1Sound, map2Sound, map3Sound, map4Sound, walkLoop, noAmmo, fallDown,
    		roundStart, landSound, virusSound, throwSound, windMedium, windHeavy, targetSound, grenadeContact, onGroundSound, destroySound,
    		airstrikeUse, headshotSound, airDropFall, lootEquip
    };
}
