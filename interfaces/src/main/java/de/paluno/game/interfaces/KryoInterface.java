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
        kryo.register(Message.class);
        kryo.register(Message.Type.class);
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
        kryo.register(PointerData.class);
        kryo.register(ChatMessage.class);

        kryo.register(UserLoginRequest.class);
        kryo.register(UserLoginRequest.Result.class);

        kryo.register(LobbyData.class);
        kryo.register(LobbyData[].class);
        kryo.register(LobbyCreateRequest.class);
        kryo.register(LobbyCreateRequest.Result.class);
        kryo.register(LobbyJoinRequest.class);
        kryo.register(LobbyJoinRequest.Result.class);
        kryo.register(LobbyDataRequest.class);
        kryo.register(LobbyDataRequest.Result.class);
        kryo.register(LobbyListRequest.class);
        kryo.register(LobbyListRequest.Result.class);
        kryo.register(LobbyLeaveRequest.class);
        kryo.register(LobbyLeaveRequest.Result.class);

        kryo.register(UserMessage.class);

        kryo.register(String[].class);

        kryo.register(StartMatchRequest.class);
        kryo.register(GameSetupRequest.Player.class);
        kryo.register(GameSetupRequest.Player[].class);
        kryo.register(TurretsShootRequest.class);
    }
}
