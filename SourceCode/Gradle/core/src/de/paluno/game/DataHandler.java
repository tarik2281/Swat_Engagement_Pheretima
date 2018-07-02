package de.paluno.game;

public interface DataHandler<T> {

    void handleData(NetworkClient client, T data);
}
