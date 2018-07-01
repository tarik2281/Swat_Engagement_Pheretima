package de.paluno.game.interfaces;

import com.esotericsoftware.kryo.Kryo;

public class KryoInterface {

    public static void registerClasses(Kryo kryo) {
        kryo.register(WorldData.class);
        kryo.register(GameSetupRequest.class);
        kryo.register(GameSetupData.class);
        kryo.register(PlayerData.class);
        kryo.register(WormData.class);
        kryo.register(PhysicsData.class);
        kryo.register(MessageData.class);
        kryo.register(MessageData.Type.class);
        kryo.register(int[].class);
        kryo.register(PlayerData[].class);
        kryo.register(WormData[].class);
        kryo.register(StartTurnEvent.class);
        kryo.register(ProjectileData.class);
        kryo.register(GameEvent.Type.class);
        kryo.register(ExplosionEvent.class);
        kryo.register(ProjectileData[].class);
        kryo.register(EndTurnEvent.class);
        kryo.register(ShootEvent.class);
        kryo.register(GameEvent.class);
        kryo.register(WormEvent.class);
        kryo.register(WormDamageEvent.class);
        kryo.register(GameOverEvent.class);
        kryo.register(ShotDirectionData.class);
    }
}
