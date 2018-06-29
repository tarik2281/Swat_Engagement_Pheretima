package de.paluno.game;

import de.paluno.game.gameobjects.Player;
import de.paluno.game.screens.PlayScreen;

public class LocalWorldHandler extends WorldHandler {

    private int numWorms;

    public LocalWorldHandler(PlayScreen screen, int mapNumber, int numWorms) {
        super(screen, mapNumber);

        this.numWorms = numWorms;
    }

    @Override
    protected void requestNextTurn() {
        int playerNumber = getCurrentPlayerIndex();
        if (playerNumber >= 0) {
            getPlayers().get(playerNumber).shiftTurn();
            playerNumber = (playerNumber + 1) % getPlayers().size();
        }
        else
            playerNumber = 0;

        Player player = getPlayers().get(playerNumber);

        setCurrentPlayerTurn(playerNumber, player.getCurrentWorm().getCharacterNumber());
        getWindHandler().nextWind();
    }

    @Override
    public boolean shouldAcceptInput() {
        return true;
    }

    @Override
    protected boolean shouldWorldStep() {
        return true;
    }

    @Override
    public void onInitializePlayers() {
        initializePlayersDefault(numWorms);
    }
}
