package de.karaca.net.core;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

@Slf4j
public class NetMessageSerializer {

    private static final int HEADER_LENGTH = 8;

    private final NetPayloadSerializer payloadSerializer;

    protected final Input input = new Input(8192);
    protected final Output output = new Output(8192);

    public NetMessageSerializer(NetPayloadSerializer payloadSerializer) {
        this.payloadSerializer = payloadSerializer;
    }

    public void resetInputBuffer() {
        input.setPosition(0);
        input.setLimit(0);
        input.setTotal(0);
    }

    public void copyFrom(ByteBuffer buffer) {
        buffer.flip();
        buffer.get(input.getBuffer(), input.limit(), buffer.remaining());

        input.setLimit(input.limit() + buffer.limit());

        buffer.clear();
    }

    public void copyTo(ByteBuffer buffer) {
        buffer.put(input.getBuffer(), input.position(), input.limit() - input.position());
    }

    public boolean hasData() {
        return input.position() != input.limit();
    }

    public boolean canDeserialize() {
        int lastPosition = input.position();
        int payloadLength = input.readInt();
        input.setPosition(lastPosition);
        return input.limit() - HEADER_LENGTH - lastPosition >= payloadLength;
    }

    public NetMessage<Object> deserialize() {
        int lastPosition = input.position();
        int payloadLength = input.readInt();

        if (input.limit() - HEADER_LENGTH - lastPosition < payloadLength) {
            throw new IllegalStateException("Not enough data to deserialize");
        }

        int messageTypeHash = input.readInt();
        NetMessageType<Object> messageType = NetMessageType.getByHash(messageTypeHash);

        if (messageType == null) {
            throw new IllegalStateException("Unknown message type hash: " + messageTypeHash);
        }

        Object payload = null;

        if (Objects.equals(messageType.getType(), Void.class)) {
            if (payloadLength != 0) {
                throw new IllegalStateException("Payload length must be 0 for void messages");
            }
        }
        else {
            payload = payloadSerializer.readFrom(messageType, input);
        }

        if (input.position() != input.limit()) {
            log.warn("Input not fully consumed");
        } else {
            resetInputBuffer();
        }

        return NetMessage.builder(messageType)
            .payload(payload)
            .build();
    }

    public <T> void serialize(NetMessage<T> netMessage) {
        output.reset();

        output.setPosition(4);
        output.writeInt(netMessage.getType().getNameHash());

        int payloadStart = output.position();

        if (netMessage.getType().getType() != Void.class) {
            if (netMessage.getPayload() == null) {
                throw new IllegalArgumentException("Payload is null");
            }

            payloadSerializer.writeTo(netMessage.getPayload(), output);
        }

        int endPosition = output.position();

        int payloadLength = endPosition - payloadStart;

        output.setPosition(0);
        output.writeInt(payloadLength);
        output.setPosition(endPosition);
    }

    public void flushToStream(OutputStream outputStream) {
        output.setOutputStream(outputStream);
        output.flush();
    }

    public byte[] getOutputBuffer() {
        return output.getBuffer();
    }

    public byte[] getSerializedBytes() {
        // TODO: validation
        return output.toBytes();
    }

    public NetPayloadSerializer getPayloadSerializer() {
        return payloadSerializer;
    }
}
