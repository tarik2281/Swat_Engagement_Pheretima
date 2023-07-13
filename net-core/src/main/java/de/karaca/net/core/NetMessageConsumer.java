package de.karaca.net.core;

public interface NetMessageConsumer<T> {
    void accept(NetSession netSession, NetMessage<T> message);
}
