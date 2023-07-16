package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class Message {

    public enum Type {
        /**
         * UserJoined messages resolve to an instance of {@link UserMessage}.
         */
        UserJoined,
        /**
         * UserLeft messages resolve to an instance of {@link UserMessage}.
         */
        UserLeft,
        ClientReady,
        LobbyDestroyed,
        /**
         * ChatMessage messages resolve to an instance of {@link ChatMessage}.
         */
        ChatMessage
    }

    private Type type;

    public Message() {

    }

    protected Message(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public static Message clientReady() {
        return new Message(Type.ClientReady);
    }

    public static Message lobbyDestroyed() {
        return new Message(Type.LobbyDestroyed);
    }
}
