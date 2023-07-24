package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;
import de.karaca.net.core.NetRequestType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@NetPayload
public class LobbyListRequest {

    public static final NetRequestType<Void, LobbyListRequest.Result> TYPE = NetRequestType.create("LobbyList", Void.class, LobbyListRequest.Result.class);

    @NetPayload
    @Getter
    @Setter
    public static class Result {

        public List<LobbyData> lobbies;
    }


}
