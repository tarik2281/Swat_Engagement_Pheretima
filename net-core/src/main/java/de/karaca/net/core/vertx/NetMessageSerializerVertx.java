package de.karaca.net.core.vertx;

import de.karaca.net.core.NetMessageSerializer;
import de.karaca.net.core.NetPayloadSerializer;
import io.vertx.core.buffer.Buffer;

public class NetMessageSerializerVertx extends NetMessageSerializer {

    public NetMessageSerializerVertx(NetPayloadSerializer payloadSerializer) {
        super(payloadSerializer);
    }

    public Buffer flushToBuffer() {
        return Buffer.buffer(getSerializedBytes());
    }

    public void readFromBuffer(Buffer buffer) {
        buffer.getBytes(input.getBuffer(), input.limit());

        input.setLimit(input.limit() + buffer.length());
    }
}
