package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class UserMessage extends Message {

    private int userId;
    private String name;

    public UserMessage() {

    }

    protected UserMessage(Type type, int userId, String name) {
        super(type);

        this.userId = userId;
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public static UserMessage joined(int userId, String name) {
        return new UserMessage(Type.UserJoined, userId, name);
    }

    public static UserMessage left(int userId, String name) {
        return new UserMessage(Type.UserLeft, userId, name);
    }
}
