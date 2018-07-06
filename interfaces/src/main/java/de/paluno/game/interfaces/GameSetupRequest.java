package de.paluno.game.interfaces;

public class GameSetupRequest {

    public static class Player {
        private int clientId;
        private String[] wormNames;

        public Player(int clientId, String[] wormNames) {
            this.clientId = clientId;
            this.wormNames = wormNames;
        }

        public int getClientId() {
            return clientId;
        }

        public String[] getWormNames() {
            return wormNames;
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
