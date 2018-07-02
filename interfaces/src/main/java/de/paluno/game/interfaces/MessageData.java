package de.paluno.game.interfaces;

public class MessageData {

    public enum Type {
        PlayerJoined,
        GameStarted,
        RequestGameSetup,
        RequestGameSetupResult,
        ClientReady,
        ChatMessage
    }

    private Type type;

    public MessageData() {

    }

    public MessageData(Type type) {
        this.type = type;
    }


    public Type getType() {
        return type;
    }
}
