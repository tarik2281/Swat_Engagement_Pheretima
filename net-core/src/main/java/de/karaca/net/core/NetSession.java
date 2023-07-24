package de.karaca.net.core;

import java.util.UUID;
import java.util.function.Consumer;

public interface NetSession {
    UUID getSessionId();
    <T> void send(NetMessage<T> netMessage);

    <T, R> void request(NetRequestType<T, R> requestType, T payload, Consumer<R> callback);
    <R> void request(NetRequestType<Void, R> requestType, Consumer<R> callback);
}
