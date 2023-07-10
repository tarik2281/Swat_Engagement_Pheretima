package de.karaca.net.core.vertx;

import de.karaca.net.core.KryoNetPayloadSerializer;
import de.karaca.net.core.NetMessage;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.datagram.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class NetDataHandlerVertx {

    private final NetMessageSerializerVertx serializer = new NetMessageSerializerVertx(new KryoNetPayloadSerializer());

    public void handleDatagramPacket(DatagramPacket packet, Consumer<NetMessage> messageConsumer) {
        serializer.resetInputBuffer();
        serializer.readFromBuffer(packet.data());

        while (serializer.hasData() && serializer.canDeserialize()) {
            NetMessage message = serializer.deserialize();

            messageConsumer.accept(message);
        }

        if (serializer.hasData()) {
            log.warn("Datagram packet contains more data than expected");
        }
    }

    public void handleNetData(NetSessionVertx netSession, Buffer buffer, Consumer<NetMessage> messageConsumer) {
        serializer.resetInputBuffer();

        if (netSession.hasPendingData()) {
            var messageBuffer = netSession.getMessageBuffer();

            serializer.copyFrom(messageBuffer);

            messageBuffer.clear();
        }

        serializer.readFromBuffer(buffer);

        while (serializer.hasData() && serializer.canDeserialize()) {
            NetMessage message = serializer.deserialize();

            messageConsumer.accept(message);
        }

        if (serializer.hasData()) {
            serializer.copyTo(netSession.getMessageBuffer());
        }
    }

    public Buffer toBuffer(NetMessage netMessage) {
        serializer.serialize(netMessage);
        return serializer.flushToBuffer();
    }

    public void send(NetSessionVertx netSession, NetMessage netMessage) {
        var buffer = toBuffer(netMessage);

        switch (netMessage.getType().getProtocol()) {
            case TCP -> netSession.getTcpSocket().write(buffer);
            case UDP -> netSession.getUdpSocket().send(buffer,
                netSession.getRemoteUdpAddress().port(),
                netSession.getRemoteUdpAddress().host());
        }
    }
}
