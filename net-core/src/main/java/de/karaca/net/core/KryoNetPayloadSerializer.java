package de.karaca.net.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.InputStream;
import java.io.OutputStream;

public class KryoNetPayloadSerializer implements NetPayloadSerializer {

    private final Kryo kryo = new Kryo();

    private Input inputBuffer;
    private Output outputBuffer;

    public KryoNetPayloadSerializer() {
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
    }

    @Override
    public boolean isTypeSupported(Class<?> type) {
        return true;
    }

    @Override
    public void writeTo(Object object, OutputStream outputStream) {
        // TODO: error handling
        if (outputStream instanceof Output output) {
            kryo.writeObject(output, object);
        } else {
            if (outputBuffer == null) {
                outputBuffer = new Output(4096);
            }

            kryo.writeObject(outputBuffer, object);

            outputBuffer.setOutputStream(outputStream);
            outputBuffer.flush();
        }
    }

    @Override
    public <T> T readFrom(NetMessageType<T> netMessageType, InputStream inputStream) {
        // TODO: error handling
        if (inputStream instanceof Input input) {
            return kryo.readObject(input, netMessageType.getType());
        } else {
            if (inputBuffer == null) {
                inputBuffer = new Input(4096);
            }

            inputBuffer.setInputStream(inputStream);

            return kryo.readObject(inputBuffer, netMessageType.getType());
        }
    }
}
