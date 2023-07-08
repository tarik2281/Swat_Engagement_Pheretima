package de.paluno.game.interfaces;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KryoNetSerializer {

    private final Kryo kryo = new Kryo();

    private final Input input = new Input(2048);
    private final Output output = new Output(2048);

    public KryoNetSerializer() {
        kryo.setReferences(false);
        kryo.setRegistrationRequired(true);

        KryoInterface.registerClasses(kryo);
    }

    public void readBuffer(Buffer buffer) {
        buffer.getBytes(input.getBuffer());

        input.setPosition(0);
        input.setLimit(buffer.length());
        input.setTotal(0);
    }

    public Object readNextObject() {
        var parsedObject = kryo.readClassAndObject(input);

        if (input.position() != input.limit())
            log.warn("Input was not fully consumed!");

        return parsedObject;
    }

    public boolean hasData() {
        return input.position() < input.limit();
    }

    public Buffer writeObject(Object object) {
        output.clear();
        kryo.writeClassAndObject(output, object);

        return Buffer.buffer(output.toBytes());
    }
}
