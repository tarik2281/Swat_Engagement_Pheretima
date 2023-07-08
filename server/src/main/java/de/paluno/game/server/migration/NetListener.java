package de.paluno.game.server.migration;

public interface NetListener {
    void connected(NetSession session);
    void disconnected(NetSession session);
    void received(NetSession session, Object object);
}
