package de.karaca.sep.client;

public interface NetListener {
    void connected();
    void disconnected();
    void connectionFailed();
    void received(Object object);
}
