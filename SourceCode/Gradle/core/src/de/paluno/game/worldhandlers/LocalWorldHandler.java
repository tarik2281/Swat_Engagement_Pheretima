package de.paluno.game.worldhandlers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import de.paluno.game.Constants;
import de.paluno.game.EventManager;
import de.paluno.game.gameobjects.Player;
import de.paluno.game.gameobjects.Worm;
import de.paluno.game.interfaces.*;
import de.paluno.game.screens.PlayScreen;
import de.paluno.game.screens.WinningPlayer;

public class LocalWorldHandler extends WorldHandler {

    private int numWorms;
    private Array<UserName> names;

    private boolean wormDied;

    public LocalWorldHandler(PlayScreen screen, int mapNumber, int numWorms, Array<UserName> names) {
        super(screen, mapNumber);

        this.numWorms = numWorms;
        this.names = names;
    }

    private void startTurn() {
        if (getNumPlayersAlive() <= 1) {
            int winningPlayerNumber = -1;
            for (Player player : getPlayers()) {
                if (!player.isDefeated()) {
                    winningPlayerNumber = player.getPlayerNumber();
                    break;
                }
            }

            WinningPlayer winningPlayer = WinningPlayer.NONE;
            switch (winningPlayerNumber) {
                case 0:
                    winningPlayer = WinningPlayer.PLAYERONE;
                    break;
                case 1:
                    winningPlayer = WinningPlayer.PLAYERTWO;
                    break;
            }

            EventManager.getInstance().queueEvent(EventManager.Type.GameOver, winningPlayer);
        }
        else {
            Player currentPlayer = getCurrentPlayer();
            Worm worm = currentPlayer.getCurrentWorm();

            if (worm.isInfected()) {
                worm.takeDamage(de.paluno.game.Constants.VIRUS_DAMAGE, Constants.DAMAGE_TYPE_VIRUS);

                if (worm.isDead()) {
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            if (!currentPlayer.isDefeated())
                                currentPlayer.shiftTurn();
                            else
                                shiftTurn(false);

                            startTurn();
                        }
                    }, 0.05f);
                    return;
                }
            }

            setCurrentPlayerTurn(currentPlayer.getPlayerNumber(),
                    currentPlayer.getCurrentWorm().getCharacterNumber());
            getWindHandler().nextWind();
            getReplay().setWind(getWindHandler().getWind());
            wormDied = false;
        }
    }

    private void shiftTurn(boolean shiftWorms) {
        if (getNumPlayersAlive() <= 1)
            return;

        if (shiftWorms && currentPlayer != -1) {
            getCurrentPlayer().shiftTurn();
        }

        do {
            currentPlayer = (currentPlayer + 1) % getPlayers().size();
        } while (getCurrentPlayer().isDefeated());
    }

    @Override
    protected void requestNextTurn() {
        if (wormDied && getReplay() != null) {
            getReplay().addGameData(new GameEvent(getCurrentGameTick(), GameEvent.Type.END_TURN));
            EventManager.getInstance().queueEvent(EventManager.Type.Replay, getReplay());
        }

        wormDied = false;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                shiftTurn(true);

                startTurn();
            }
        }, 0.5f);
    }

    @Override
    protected void onWormDied(Worm.DeathEvent event) {
        wormDied = true;
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
        for (int i = 0; i < names.size; i++) {
            UserName userName = names.get(i);
            Player player = addPlayer(i);
            player.setName(userName.getUserName());

            for (int j = 0; j < numWorms; j++) {
                Worm worm = addWorm(player, j, userName.getWormNames()[j]);
                worm.setPosition(getRandomSpawnPosition());
            }
        }
    }

    @Override
    protected boolean shouldCreateReplay() {
        return true;
    }
}
