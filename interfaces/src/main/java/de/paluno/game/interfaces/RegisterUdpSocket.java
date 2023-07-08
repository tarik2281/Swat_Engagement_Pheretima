package de.paluno.game.interfaces;

public class RegisterUdpSocket {
    private String sessionId;

    public RegisterUdpSocket() {
    }

    public RegisterUdpSocket(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
