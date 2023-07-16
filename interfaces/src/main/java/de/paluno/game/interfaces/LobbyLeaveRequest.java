package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class LobbyLeaveRequest {

    @NetPayload
    public static class Result {
        public boolean success;
    }
}
