package de.paluno.game.interfaces;

public class WormEvent extends GameEvent {

    private WormIdentifier identifier;

    public WormEvent() {
        super();
    }

    public WormEvent(int tick, Type type, WormIdentifier identifier) {
        super(tick, type);

        this.identifier = identifier;
    }

    public WormIdentifier getIdentifier() {
        return identifier;
    }
}
