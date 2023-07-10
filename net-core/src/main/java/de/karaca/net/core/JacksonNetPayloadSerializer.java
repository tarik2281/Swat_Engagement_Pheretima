package de.karaca.net.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JacksonNetPayloadSerializer implements NetPayloadSerializer {

    private final ObjectMapper objectMapper;

    public JacksonNetPayloadSerializer() {
        this(new ObjectMapper());
    }

    public JacksonNetPayloadSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean isTypeSupported(Class<?> type) {
        return true;
    }

    @Override
    public void writeTo(Object object, OutputStream outputStream) {
        try {
            objectMapper.writeValue(outputStream, object);
        } catch (Exception e) {
            // TODO: error handling
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object readFrom(NetMessageType messageType, InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, messageType.getType());
        } catch (IOException e) {
            // TODO: error handling
            throw new RuntimeException(e);
        }
    }
}
