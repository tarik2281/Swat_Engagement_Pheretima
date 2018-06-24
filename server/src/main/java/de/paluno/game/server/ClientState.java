package de.paluno.game.server;

public class ClientState {

    public int clientId;
    public boolean ready;

    public ClientState(int clientId) {
        this.clientId = clientId;
        ready = false;
    }
}
