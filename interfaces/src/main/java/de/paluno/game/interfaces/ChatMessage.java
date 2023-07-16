package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class ChatMessage extends Message {

    private int player;
    private String userName;
    private String message;

    public ChatMessage() {

    }

    public ChatMessage(String message) {
        super(Type.ChatMessage);

        this.message = message;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
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
