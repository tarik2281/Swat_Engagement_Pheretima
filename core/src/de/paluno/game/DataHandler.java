package de.paluno.game;

public interface DataHandler {

    void handleData(NetworkClient client, Object data);
    //void handleData(NetworkClient client, T data);
}
