package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class LobbyListRequest {

    @NetPayload
    public static class Result {

        public LobbyData[] lobbies;
    }


}
