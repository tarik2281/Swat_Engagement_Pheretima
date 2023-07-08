package de.paluno.game.interfaces;

public class RegisterTcpClient {
    private final String sessionId;

    public RegisterTcpClient() {
        this.sessionId = null;
    }

    public RegisterTcpClient(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
