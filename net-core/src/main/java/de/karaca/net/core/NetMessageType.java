package de.karaca.net.core;

import de.karaca.net.core.util.FNV1a;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

@Getter
@Slf4j
public final class NetMessageType<T> {
    private static final HashMap<Integer, NetMessageType<Object>> messageTypeMap = new HashMap<>();

    private final String name;
    private final int nameHash;
    private final Class<T> type;
    private final NetProtocol protocol;

    private NetMessageType(String name, Class<T> type, NetProtocol protocol) {
        this.name = Objects.requireNonNull(name);
        this.type = type;
        this.protocol = protocol;

        this.nameHash = FNV1a.hash32(name.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NetMessageType<?> other) {
            return nameHash == other.nameHash;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return nameHash;
    }

    public static <T> NetMessageType<T> create(String name, Class<T> type, NetProtocol protocol) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (protocol == null) {
            throw new IllegalArgumentException("protocol must not be null");
        }

        var messageType = new NetMessageType<>(name, type, protocol);

        var oldMessageType = messageTypeMap.put(messageType.getNameHash(), (NetMessageType<Object>) messageType);

        if (oldMessageType != null) {
            if (oldMessageType.equals(messageType)) {
                log.warn("NetMessageType with name '{}' and hash '{}' already exists with same name",
                    messageType.getName(),
                    messageType.getNameHash());
            } else {
                // hash collision
                log.warn("NetMessageType with name '{}' and hash '{}' already exists with different name '{}'",
                    messageType.getName(),
                    messageType.getNameHash(),
                    oldMessageType.getName());
            }
        }

        return messageType;
    }

    public static NetMessageType<Object> getByHash(int nameHash) {
        return messageTypeMap.get(nameHash);
    }
}
