package de.paluno.game.interfaces;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PingMessage {
    private long timestamp;
    private String sessionId;
}
