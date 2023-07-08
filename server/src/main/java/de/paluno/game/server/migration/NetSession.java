package de.paluno.game.server.migration;

import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NetSession {

    private UUID sessionId;
    private NetSocket tcpSocket;
    private SocketAddress udpAddress;
    private NettyServer server;

    public void sendTCP(Object object) {
        tcpSocket.write(server.getKryoNetSerializer().writeObject(object));
    }

    public void sendUDP(Object object) {
        server.sendUdp(object, udpAddress);
    }
}
