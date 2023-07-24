package de.karaca.net.core;

public interface NetRequestHandler<T, R> {
    R apply(NetSession netSession, T request);
}
