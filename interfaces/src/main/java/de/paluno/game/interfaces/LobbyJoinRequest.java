package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class LobbyJoinRequest {

    @NetPayload
    public static class Result {
        public boolean success;
        public int lobbyId;
    }

    public int lobbyId;
}
