package de.karaca.net.core;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.function.Consumer;

@Slf4j
public class NetMessageRouter implements Consumer<NetMessage> {
    protected final HashMap<Integer, Consumer<Object>> routes = new HashMap<>();

    private Consumer<NetMessage> nextHandler;

    public <T> NetMessageRouter route(NetMessageType<T> messageType, Consumer<T> handler) {
        var previousHandler = routes.put(messageType.getNameHash(), (Consumer<Object>) handler);

        if (previousHandler != null) {
            log.warn("Overriding handler for message type {}", messageType.getName());
        }

        return this;
    }

    public NetMessageRouter chain(Consumer<NetMessage> handler) {
        nextHandler = handler;
        return this;
    }

    @Override
    public void accept(NetMessage netMessage) {
        Consumer<Object> handler = routes.get(netMessage.getType().getNameHash());
        if (handler != null) {
            handler.accept(netMessage.getPayload());
        } else if (nextHandler != null) {
            nextHandler.accept(netMessage);
        } else {
            log.warn("No handler for message type {}", netMessage.getType());
        }
    }
}
