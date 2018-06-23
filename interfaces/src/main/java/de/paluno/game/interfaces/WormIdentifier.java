package de.paluno.game.interfaces;

public class WormIdentifier {
    private int playerId;
    private int wormId;

    public WormIdentifier() {

    }

    public WormIdentifier(int playerId, int wormId) {
        this.playerId = playerId;
        this.wormId = wormId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getWormId() {
        return wormId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setWormId(int wormId) {
        this.wormId = wormId;
    }

    public boolean equals(int playerId, int wormId) {
        return this.playerId == playerId && this.wormId == wormId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WormIdentifier) {
            WormIdentifier wormIdentifier = (WormIdentifier)obj;
            return playerId == wormIdentifier.playerId && wormId == wormIdentifier.wormId;
        }

        return false;
    }
}
