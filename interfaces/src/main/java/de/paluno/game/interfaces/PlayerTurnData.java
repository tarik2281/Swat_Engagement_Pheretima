package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class PlayerTurnData extends GameData {
    private WormData playingWormData;
    private float shootingAngle;
    private int wind;

    public PlayerTurnData() {
        super();
    }

    public PlayerTurnData(int tick, WormData wormData, float shootingAngle) {
        super(tick);

        this.playingWormData = wormData;
        this.shootingAngle = shootingAngle;
    }

    public WormData getPlayingWormData() {
        return playingWormData;
    }

    public float getShootingAngle() {
        return shootingAngle;
    }
}
