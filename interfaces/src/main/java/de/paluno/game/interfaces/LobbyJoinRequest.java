package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;
import de.karaca.net.core.NetRequestType;
import lombok.Getter;
import lombok.Setter;

@NetPayload
public class LobbyJoinRequest {

    public static final NetRequestType<LobbyJoinRequest, LobbyJoinRequest.Result> TYPE = NetRequestType.create(LobbyJoinRequest.class, LobbyJoinRequest.Result.class);

    @NetPayload
    @Getter
    @Setter
    public static class Result {
        public boolean success;
        public int lobbyId;
    }

    public int lobbyId;
}
