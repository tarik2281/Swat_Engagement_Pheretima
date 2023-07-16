package de.paluno.game.interfaces;

import de.karaca.net.core.NetMessageType;
import de.karaca.net.core.NetProtocol;

public class NetMessageTypes {
    public static final NetMessageType<WorldData> WORLD_DATA = NetMessageType.create("WORLD_DATA", WorldData.class, NetProtocol.TCP);

    // UserLoginRequest
    public static final NetMessageType<UserLoginRequest> USER_LOGIN_REQUEST = NetMessageType.create("USER_LOGIN_REQUEST", UserLoginRequest.class, NetProtocol.TCP);

    // LobbyJoinRequest
    public static final NetMessageType<LobbyJoinRequest> LOBBY_JOIN_REQUEST = NetMessageType.create("LOBBY_JOIN_REQUEST", LobbyJoinRequest.class, NetProtocol.TCP);

    // LobbyJoinRequest.Result
    public static final NetMessageType<LobbyJoinRequest.Result> LOBBY_JOIN_REQUEST_RESULT = NetMessageType.create("LOBBY_JOIN_REQUEST_RESULT", LobbyJoinRequest.Result.class, NetProtocol.TCP);


    // LobbyListRequest
    public static final NetMessageType<LobbyListRequest> LOBBY_LIST_REQUEST = NetMessageType.create("LOBBY_LIST_REQUEST", LobbyListRequest.class, NetProtocol.TCP);

    // LobbyListRequest.Result
    public static final NetMessageType<LobbyListRequest.Result> LOBBY_LIST_REQUEST_RESULT = NetMessageType.create("LOBBY_LIST_REQUEST_RESULT", LobbyListRequest.Result.class, NetProtocol.TCP);

    // LobbyDataRequest
    public static final NetMessageType<LobbyDataRequest> LOBBY_DATA_REQUEST = NetMessageType.create("LOBBY_DATA_REQUEST", LobbyDataRequest.class, NetProtocol.TCP);

    // LobbyDataRequest.Result
    public static final NetMessageType<LobbyDataRequest.Result> LOBBY_DATA_REQUEST_RESULT = NetMessageType.create("LOBBY_DATA_REQUEST_RESULT", LobbyDataRequest.Result.class, NetProtocol.TCP);

    // StartMatchRequest
    public static final NetMessageType<StartMatchRequest> START_MATCH_REQUEST = NetMessageType.create("START_MATCH_REQUEST", StartMatchRequest.class, NetProtocol.TCP);

    // ChatMessage
    public static final NetMessageType<ChatMessage> CHAT_MESSAGE = NetMessageType.create("CHAT_MESSAGE", ChatMessage.class, NetProtocol.TCP);

    // GameSetupData
    public static final NetMessageType<GameSetupData> GAME_SETUP_DATA = NetMessageType.create("GAME_SETUP_DATA", GameSetupData.class, NetProtocol.TCP);

    // GameData
    public static final NetMessageType<GameData> GAME_DATA = NetMessageType.create("GAME_DATA", GameData.class, NetProtocol.TCP);

    // CratePickupEvent
    public static final NetMessageType<CratePickupEvent> CRATE_PICKUP_EVENT = NetMessageType.create("CRATE_PICKUP_EVENT", CratePickupEvent.class, NetProtocol.TCP);

    // DestroyChuteEvent
    public static final NetMessageType<DestroyChuteEvent> DESTROY_CHUTE_EVENT = NetMessageType.create("DESTROY_CHUTE_EVENT", DestroyChuteEvent.class, NetProtocol.TCP);
}
