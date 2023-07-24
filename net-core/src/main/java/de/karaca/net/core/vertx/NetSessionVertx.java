package de.karaca.net.core.vertx;

import de.karaca.net.core.NetMessage;
import de.karaca.net.core.NetSession;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.UUID;

@Getter
@Setter
public class NetSessionVertx implements NetSession {

    private ByteBuffer messageBuffer;
    private UUID sessionId;

    private NetSocket tcpSocket;
    private DatagramSocket udpSocket;
    private SocketAddress remoteUdpAddress;

    public NetSessionVertx() {
    }

    public NetSessionVertx(UUID sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public UUID getSessionId() {
        return sessionId;
    }

    public boolean hasPendingData() {
        return messageBuffer != null && messageBuffer.hasRemaining();
    }

    public ByteBuffer getMessageBuffer() {
        if (messageBuffer == null)
            messageBuffer = ByteBuffer.allocate(4096);

        return messageBuffer;
    }

    @Override
    public <T> void send(NetMessage<T> netMessage) {
        if (tcpSocket != null) {
            tcpSocket.write(netMessage.getBuffer());
        } else if (udpSocket != null) {
            udpSocket.send(netMessage.getBuffer(), remoteUdpAddress);
        }
    }
}
