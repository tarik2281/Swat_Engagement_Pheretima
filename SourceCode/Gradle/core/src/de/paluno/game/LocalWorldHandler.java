package de.paluno.game;

import de.paluno.game.gameobjects.Player;
import de.paluno.game.screens.PlayScreen;
import de.paluno.game.screens.WinningPlayer;

public class LocalWorldHandler extends WorldHandler {

    private int numWorms;

    public LocalWorldHandler(PlayScreen screen, int mapNumber, int numWorms) {
        super(screen, mapNumber);

        this.numWorms = numWorms;
    }

    @Override
    protected void requestNextTurn() {
        int numPlayersAlive = 0;
        WinningPlayer winningPlayer = WinningPlayer.NONE;

        for (Player player : getPlayers()) {
            if (!player.isDefeated()) {
                numPlayersAlive++;
                switch (player.getPlayerNumber()) {
                    case 0:
                        winningPlayer = WinningPlayer.PLAYERONE;
                        break;
                    case 1:
                        winningPlayer = WinningPlayer.PLAYERTWO;
                        break;
                }
            }
        }

        if (numPlayersAlive <= 1) {
            EventManager.getInstance().queueEvent(EventManager.Type.GameOver, winningPlayer);
            return;
        }

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
