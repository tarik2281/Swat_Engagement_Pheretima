package de.karaca.net.core;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class NetMessageRouter implements NetMessageConsumer<Object> {
    protected final HashMap<Integer, NetMessageConsumer<Object>> routes = new HashMap<>();

    private NetMessageConsumer<Object> nextHandler;

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

    @Override
    public void accept(NetSession netSession, NetMessage<Object> netMessage) {
        var handler = routes.get(netMessage.getType().getNameHash());
        if (handler != null) {
            handler.accept(netSession, netMessage);
        } else if (nextHandler != null) {
            nextHandler.accept(netSession, netMessage);
        } else {
            log.warn("No handler for message type {}", netMessage.getType());
        }
    }
}
