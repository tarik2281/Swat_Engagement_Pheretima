package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class LobbyDataRequest {

    @NetPayload
    public static class Result {
        public LobbyData lobbyData;
        public String[] users;
    }

    public int lobbyId;
}
