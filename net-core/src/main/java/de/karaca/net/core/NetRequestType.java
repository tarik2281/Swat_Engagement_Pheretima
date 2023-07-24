package de.karaca.net.core;

import lombok.Getter;

@Getter
public class NetRequestType<T, R> {

    private final Class<T> requestType;
    private final Class<R> responseType;

    private final NetMessageType<T> requestMessageType;
    private final NetMessageType<R> responseMessageType;

    private NetRequestType(Class<T> requestType, Class<R> responseType) {
        this.requestType = requestType;
        this.responseType = responseType;

        this.requestMessageType = NetMessageType.create(requestType);
        this.responseMessageType = NetMessageType.create(responseType);
    }

    private NetRequestType(String requestName, Class<T> requestType, Class<R> responseType) {
        this.requestType = requestType;
        this.responseType = responseType;

        this.requestMessageType = NetMessageType.create(requestName + "_REQUEST", requestType, NetProtocol.TCP);
        this.responseMessageType = NetMessageType.create(requestName + "_RESPONSE", responseType, NetProtocol.TCP);
    }

    public static <T, R> NetRequestType<T, R> create(Class<T> requestType, Class<R> responseType) {
        return new NetRequestType<>(requestType, responseType);
    }

    public static <T, R> NetRequestType<T, R> create(String requestName, Class<T> requestType, Class<R> responseType) {
        return new NetRequestType<>(requestName, requestType, responseType);
    }
}
