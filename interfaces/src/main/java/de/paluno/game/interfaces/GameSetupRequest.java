package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class GameSetupRequest {

    public static class Player {
        private int clientId;
        private UserName userName;

        public Player() {

        }

        public Player(int clientId, UserName userName) {
            this.clientId = clientId;
            this.userName = userName;
        }

        public int getClientId() {
            return clientId;
        }

        public UserName getUserName() {
            return userName;
        }
    }

    private Player[] players;
    private int mapNumber;
    private int numWorms;

    public GameSetupRequest() {

    }

    public GameSetupRequest(Player[] players, int mapNumber, int numWorms) {
        this.players = players;
        this.mapNumber = mapNumber;
        this.numWorms = numWorms;
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public int getNumWorms() {
        return numWorms;
    }
}
