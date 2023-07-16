package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class StartTurnEvent {

    public int playerNumber;
    public int wormNumber;
    public int wind;
}
