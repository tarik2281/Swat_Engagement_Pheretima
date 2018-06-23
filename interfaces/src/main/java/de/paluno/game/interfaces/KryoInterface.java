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
    }
}
