package de.karaca.net.core;

import lombok.Getter;

@Getter
public class NetMessage<T> {
    private final NetMessageType<T> type;
    private final T payload;

    private NetMessage(NetMessageType<T> type, T payload) {
        this.type = type;
        this.payload = payload;
    }

    public <R> boolean isOfType(NetMessageType<R> type) {
        return this.type == type;
    }

    public static <T> Builder<T> builder(NetMessageType<T> type) {
        return new Builder<>(type);
    }

    public static class Builder<T> {
        private final NetMessageType<T> type;
        private T payload;

        private Builder(NetMessageType<T> type) {
            this.type = type;
        }

        public Builder<T> payload(T payload) {
            this.payload = payload;
            return this;
        }

        public NetMessage<T> build() {
            return new NetMessage<>(type, payload);
        }
    }
}
