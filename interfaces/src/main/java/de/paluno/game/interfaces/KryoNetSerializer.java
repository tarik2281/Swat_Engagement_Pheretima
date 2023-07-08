package de.paluno.game.interfaces;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.vertx.core.buffer.Buffer;

public class KryoNetSerializer {

    private final Kryo kryo = new Kryo();

    private final Input input = new Input(2048);
    private final Output output = new Output(2048);

    public KryoNetSerializer() {
        kryo.setReferences(false);
        kryo.setRegistrationRequired(true);

        KryoInterface.registerClasses(kryo);
    }

    public Object readObject(Buffer buffer) {
        buffer.getBytes(input.getBuffer());

        input.setPosition(0);
        input.setLimit(buffer.length());
        input.setTotal(0);

        return kryo.readClassAndObject(input);
    }

    public Buffer writeObject(Object object) {
        output.clear();
        kryo.writeClassAndObject(output, object);

        return Buffer.buffer(output.toBytes());
    }
}
