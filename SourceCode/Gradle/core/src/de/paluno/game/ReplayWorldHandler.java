package de.paluno.game;

import de.paluno.game.screens.PlayScreen;

public class ReplayWorldHandler extends WorldHandler {


    public ReplayWorldHandler(PlayScreen screen, int mapNumber) {
        super(screen, mapNumber);
    }

    @Override
    protected void onInitializePlayers() {

    }

    @Override
    protected boolean shouldAcceptInput() {
        return false;
    }

    @Override
    protected boolean shouldWorldStep() {
        return false;
    }

    @Override
    protected void requestNextTurn() {

    }
}
