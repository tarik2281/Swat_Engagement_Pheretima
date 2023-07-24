package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;
import de.karaca.net.core.NetRequestType;

@NetPayload
public class LobbyCreateRequest {

    public static final NetRequestType<LobbyCreateRequest, LobbyCreateRequest.Result> TYPE = NetRequestType.create(LobbyCreateRequest.class, LobbyCreateRequest.Result.class);

    @NetPayload
    public static class Result {
        public int lobbyId;
    }

    private String name;
    private int mapNumber;
    private int numWorms;

    public LobbyCreateRequest() {

    }

    public LobbyCreateRequest(String name, int mapNumber, int numWorms) {
        this.name = name;
        this.mapNumber = mapNumber;
        this.numWorms = numWorms;
    }

    public String getName() {
        return name;
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public int getNumWorms() {
        return numWorms;
    }
}
