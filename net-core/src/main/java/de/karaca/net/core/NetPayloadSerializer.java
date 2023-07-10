package de.karaca.net.core;

import java.io.InputStream;
import java.io.OutputStream;

public interface NetPayloadSerializer {
    boolean isTypeSupported(Class<?> type);

    void writeTo(Object object, OutputStream outputStream);
    <T> T readFrom(NetMessageType<T> netMessageType, InputStream inputStream);

}
