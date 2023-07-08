package de.karaca.sep.client;

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
}
