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

    private static final int STATE_PLAYER_TURN = 1;
    private static final int STATE_TURRETS_SHOOTING = 2;
    private static final int STATE_RAISE_WATER = 3;
    private static final int STATE_AIRDROP = 4;

    private int numWorms;
    private Array<UserName> names;

    private boolean wormDied;
    private int state;

    private Timer.Task turnTimer = new Timer.Task() {
        @Override
        public void run() {
            if (isRoundEnded()) {
                if (shootTurrets())
                    state = STATE_TURRETS_SHOOTING;
                else {
                    raiseWaterLevel();
                    state = STATE_RAISE_WATER;
                }
            }
            else if (state == STATE_TURRETS_SHOOTING) {
                raiseWaterLevel();
                state = STATE_RAISE_WATER;
            }
            else if (state == STATE_RAISE_WATER) {
                randomAirdrop();
                state = STATE_AIRDROP;
            }
            else {
                state = STATE_PLAYER_TURN;
                shiftTurn(true);

                startTurn();
            }
        }
    };

    private EventManager.Listener eventListener = new EventManager.Listener() {
        @Override
        public void handleEvent(EventManager.Type eventType, Object data) {
            switch (eventType) {
                case ReplayEnded:
                    Timer.schedule(turnTimer, 0.5f);
                    break;
            }
        }
    };

    public LocalWorldHandler(PlayScreen screen, int mapNumber, int numWorms, Array<UserName> names) {
        super(screen, mapNumber);

        this.numWorms = numWorms;
        this.names = names;
    }

    private void startTurn() {
        if (getNumPlayersAlive() <= 1) {
            String winningPlayer = null;
            for (Player player : getPlayers()) {
                if (!player.isDefeated()) {
                    winningPlayer = player.getName();
                    break;
                }
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

        do {
            currentPlayer = (currentPlayer + 1) % getPlayers().size();
        } while (getCurrentPlayer().isDefeated());

        if (shiftWorms) {
            getCurrentPlayer().shiftTurn();
        }
    }

    @Override
    protected void requestNextTurn() {
        if (wormDied && getReplay() != null) {
            getReplay().setCameraPosition(getWorld().getCamera().getWorldPosition());
            getReplay().addGameData(new GameEvent(getCurrentGameTick(), GameEvent.Type.END_TURN));
            EventManager.getInstance().queueEvent(EventManager.Type.Replay, getReplay());
        }
        else {
            Timer.schedule(turnTimer, 0.5f);
        }

        wormDied = false;
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
        EventManager.getInstance().addListener(eventListener, EventManager.Type.ReplayEnded);

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
    public void dispose() {
        super.dispose();

        EventManager.getInstance().removeListener(eventListener, EventManager.Type.ReplayEnded);
    }

    @Override
    protected boolean shouldCreateReplay() {
        return true;
    }

    private boolean isRoundEnded() {
        boolean roundEnded = true;

        for (Player player : getPlayers()) {
            if (!player.isRoundEnded()) {
                roundEnded = false;
                break;
            }
        }

        if (roundEnded)
            getPlayers().forEach(player -> player.setIsRoundEnded(false));

        return roundEnded;
    }
}
