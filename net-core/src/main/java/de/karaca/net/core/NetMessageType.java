package de.karaca.net.core;

import de.karaca.net.core.util.FNV1a;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

@Getter
@Slf4j
public final class NetMessageType<T> {
    private static final HashMap<Integer, NetMessageType<?>> messageTypeByHash = new HashMap<>();
    private static final HashMap<Class<?>, NetMessageType<?>> messageTypeByClass = new HashMap<>();

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

    @SuppressWarnings("unchecked")
    public static <T> NetMessageType<T> getByClass(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }

        var messageType = messageTypeByClass.get(type);

        if (messageType == null) {
            throw new IllegalArgumentException("No NetMessageType registered by class for " + type.getName());
        }

        return (NetMessageType<T>) messageType;
    }

    @SuppressWarnings("unchecked")
    public static NetMessageType<Object> getByHash(int nameHash) {
        return (NetMessageType<Object>) messageTypeByHash.get(nameHash);
    }

    public static void scan(Class<?> basePackage) {
        Reflections reflections = new Reflections(ConfigurationBuilder.build(basePackage));
        reflections.getTypesAnnotatedWith(NetPayload.class).forEach(NetMessageType::create);
    }

    public static void scan(String basePackage) {
        Reflections reflections = new Reflections(ConfigurationBuilder.build(basePackage));
        reflections.getTypesAnnotatedWith(NetPayload.class).forEach(NetMessageType::create);
    }

    public static <T> NetMessageType<T> create(Class<T> type) {
        NetPayload payload = type.getAnnotation(NetPayload.class);
        NetProtocol protocol = NetProtocol.TCP;

        if (payload != null) {
            protocol = payload.protocol();
        } else {
            log.warn("No @NetPayload annotation found for type {}", type.getName());
        }

        return create(type, protocol);
    }

    public static <T> NetMessageType<T> create(Class<T> type, NetProtocol protocol) {
        var existingMessageType = messageTypeByClass.get(type);

        if (existingMessageType != null) {
            if (existingMessageType.getProtocol() == protocol) {
                return (NetMessageType<T>) existingMessageType;
            } else {
                throw new IllegalArgumentException("NetMessageType already exists for type " + type.getName() + " with different protocol");
            }
        }

        var messageType = create(type.getName(), type, protocol);

        messageTypeByClass.put(type, messageType);

        return messageType;
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

        log.debug("Created NetMessageType with name '{}' and hash '{}' for type '{}' using protocol '{}'",
            messageType.getName(),
            messageType.getNameHash(),
            messageType.getType().getName(),
            messageType.getProtocol());

        addMessageType(messageType);

        return messageType;
    }

    private static void addMessageType(NetMessageType<?> messageType) {
        var oldMessageType = messageTypeByHash.put(messageType.getNameHash(), messageType);

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
    }
}
