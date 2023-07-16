package de.karaca.net.core;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class NetMessageRouter implements NetMessageConsumer<Object> {
    protected final HashMap<Integer, NetMessageConsumer<Object>> routes = new HashMap<>();

    private NetMessageConsumer<Object> nextHandler;
    private NetMessageConsumer<Object> fallbackHandler;

    public <T> NetMessageRouter route(Class<T> type, NetMessageConsumer<T> handler) {
        return route(NetMessageType.getByClass(type), handler);
    }

    public <T> NetMessageRouter route(NetMessageType<T> messageType, NetMessageConsumer<T> handler) {
        var previousHandler = routes.put(messageType.getNameHash(), (NetMessageConsumer<Object>) handler);

        if (previousHandler != null) {
            log.warn("Overriding handler for message type {}", messageType.getName());
        }

        return this;
    }

    public NetMessageRouter chain(NetMessageConsumer<Object> handler) {
        nextHandler = handler;
        return this;
    }

    public NetMessageRouter fallback(NetMessageConsumer<Object> handler) {
        fallbackHandler = handler;
        return this;
    }

    @Override
    public void accept(NetSession netSession, NetMessage<Object> netMessage) {
        var handler = routes.get(netMessage.getType().getNameHash());
        if (handler != null) {
            handler.accept(netSession, netMessage);
        } else if (fallbackHandler != null) {
            fallbackHandler.accept(netSession, netMessage);
        }

        if (nextHandler != null) {
            nextHandler.accept(netSession, netMessage);
        }

        if (handler == null && fallbackHandler == null && nextHandler == null) {
            log.warn("No handler for message type {}", netMessage.getType());
        }
    }
}
