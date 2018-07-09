package de.paluno.game.interfaces;

public class ChatMessage extends MessageData {

    private int player;
    private String message;

    public ChatMessage() {

    }

    public ChatMessage(String message) {
        super(Type.ChatMessage);

        this.message = message;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }
}
