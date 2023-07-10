package de.karaca.net.core;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NetConnectionConfig {
    private final String host;
    private final int tcpPort;
    private final int udpPort;
}
