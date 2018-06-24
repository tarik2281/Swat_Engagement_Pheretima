package de.paluno.game;

import de.paluno.game.interfaces.WorldData;
import de.paluno.game.screens.PlayScreen;

public class LocalWorldHandler extends WorldHandler {

    private int currentPlayer;

    private int mapNumber;
    private int numWorms;

    public LocalWorldHandler(PlayScreen screen, int mapNumber, int numWorms) {
        super(screen, mapNumber);

        this.mapNumber = mapNumber;
        this.numWorms = numWorms;
    }

    @Override
    public boolean requestNextTurn() {
        setCurrentPlayerTurn(0, 0);

        return true;
    }

    @Override
    public void onInitializePlayers() {
        initializePlayersDefault(numWorms);
    }

    @Override
    protected void onEmitWorldData(WorldData data) {

    }

    @Override
    protected void onUpdate(float delta) {

    }
}
