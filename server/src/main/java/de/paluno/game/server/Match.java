package de.paluno.game.server;

import de.paluno.game.interfaces.*;

import java.util.ArrayList;
import java.util.Random;

public class Match {

    private static final int STATE_PLAYER_TURN = 1;
    private static final int STATE_TURRETS_SHOOT = 2;
    private static final int STATE_RAISE_WATER = 3;
    private static final int STATE_AIRDROP = 4;
    private static final int STATE_GAME_OVER = 5;

    private int currentTick;

    private int currentPlayerIndex;
    private ArrayList<Player> players;
    private int numPlayersAlive;

    private Random windRandomizer;
    private Lobby lobby;

    private int state;
    private int simulatingUserId = -1;

    public Match(Lobby lobby) {
        currentTick = 0;
        currentPlayerIndex = -1;
        numPlayersAlive = 0;
        this.lobby = lobby;

        players = new ArrayList<>();
        windRandomizer = new Random();

        state = 0;
    }

    public Player addPlayer(User user, int number) {
        Player player = new Player(user, number);
        player.setDefeatedListener(() -> numPlayersAlive--);
        players.add(player);
        numPlayersAlive++;
        return player;
    }

    public void userDisconnected(User user) {
        for (Player player : players) {
            if (player.getControllingUser() == user) {
                player.setDisconnected(true);

                for (Worm worm : player.getWorms())
                    worm.setDead(true);

                if (!sendGameOver() && (simulatingUserId == player.getControllingUser().getId() || (state == STATE_PLAYER_TURN && currentPlayerIndex == player.getNumber()))) {
                    lobby.broadcastData(user, new GameEvent(currentTick, GameEvent.Type.END_TURN));
                }

                break;
            }
        }
    }

    public void userReady(User user) {
        for (Player player : players)
            if (player.getControllingUser() == user) {
                System.out.println("Setting user ready (id: " + user.getId() + ")");
                player.setReady(true);
                break;
            }

        if (allClientsReady() && !sendGameOver()) {
            if (isRoundEnded()) {
                Player simulatingPlayer = null;
                for (Player player : players) {
                    if (!player.isDisconnected()) {
                        simulatingPlayer = player;
                        break;
                    }
                }

                state = STATE_TURRETS_SHOOT;
                simulatingUserId = simulatingPlayer.getControllingUser().getId();
                lobby.broadcastData(null, new TurretsShootRequest(simulatingUserId));
            }
            else if (state == STATE_TURRETS_SHOOT) {
                state = STATE_RAISE_WATER;
                simulatingUserId = -1;
                lobby.broadcastData(null, new RaiseWaterEvent());
            }
            else if (state == STATE_RAISE_WATER) {
                Player simulatingPlayer = null;
                for (Player player : players) {
                    if (!player.isDisconnected()) {
                        simulatingPlayer = player;
                        break;
                    }
                }

                state = STATE_AIRDROP;
                simulatingUserId = simulatingPlayer.getControllingUser().getId();
                lobby.broadcastData(null, new SpawnAirdropRequest(simulatingPlayer.getControllingUser().getId()));
            }
            else {
                simulatingUserId = -1;
                startTurn();
            }
        }
    }

    public void handleGameData(User sender, GameData gameData) {
        if (gameData instanceof WorldData) {
            if (currentTick < gameData.getTick())
                currentTick = gameData.getTick();

            lobby.broadcastData(sender, gameData);
        } else if (gameData instanceof GameEvent) {
            switch (((GameEvent) gameData).getType()) {
                case WORM_FELL_DOWN:
                case WORM_DIED: {
                    WormEvent event = (WormEvent) gameData;
                    getWorm(event).setDead(true);

                    break;
                }
                case WORM_TOOK_DAMAGE: {
                    WormDamageEvent event = (WormDamageEvent) gameData;
                    getWorm(event).applyDamage(event.getDamage());
                    break;
                }
                case WORM_INFECTED: {
                    WormEvent event = (WormEvent) gameData;
                    getWorm(event).setInfected(true);
                    break;
                }
            }

            if (state != STATE_RAISE_WATER)
                lobby.broadcastData(sender, gameData);
        }
    }

    private boolean allClientsReady() {
        boolean ready = true;

        for (Player player : players) {
            if (player.getControllingUser().getConnection().isConnected() && !player.isReady()) {
                System.out.println("Not ready: " + player.getControllingUser().getId() + " " + player.getControllingUser().getName());
                ready = false;
                break;
            }
        }

        if (ready)
            players.forEach(player -> player.setReady(false));

        return ready;
    }

    private Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    private Worm getWorm(WormEvent event) {
        return players.get(event.getPlayerNumber()).getWormByNumber(event.getWormNumber());
    }

    private void applyWormInfectionDamage() {
        Player currentPlayer = getCurrentPlayer();
        Worm worm = currentPlayer.getCurrentWorm();

        if (worm.isInfected()) {
            WormDamageEvent damageEvent = worm.takeDamage(Constants.VIRUS_DAMAGE, Constants.DAMAGE_TYPE_VIRUS);

            if (worm.isDead()) {
                lobby.broadcastData(null, new WormEvent(0, GameEvent.Type.WORM_DIED, worm.getPlayerNumber(), worm.getWormNumber()));

                if (numPlayersAlive >= Constants.NUM_MIN_PLAYERS) {
                    if (currentPlayer.isDefeated()) {
                        shiftTurn(false);
                        applyWormInfectionDamage();
                    } else {
                        currentPlayer.shiftTurn();
                    }
                }
            } else {
                lobby.broadcastData(null, damageEvent);
            }
        }
    }

    private boolean sendGameOver() {
        if (state == STATE_GAME_OVER)
            return true;

        if (numPlayersAlive < Constants.NUM_MIN_PLAYERS) {
            state = STATE_GAME_OVER;

            int winningPlayer = -1;
            for (Player player : players) {
                if (!player.isDefeated()) {
                    winningPlayer = player.getNumber();
                    break;
                }
            }

            GameOverEvent gameOverEvent = new GameOverEvent();
            gameOverEvent.winningPlayer = winningPlayer;
            lobby.broadcastData(null, gameOverEvent);
            return true;
        }

        return false;
    }

    private void startTurn() {
        state = STATE_PLAYER_TURN;

        shiftTurn(true);

        applyWormInfectionDamage();

        StartTurnEvent startTurnEvent = new StartTurnEvent();

        Player currentPlayer = getCurrentPlayer();
        startTurnEvent.playerNumber = currentPlayer.getNumber();
        startTurnEvent.wormNumber = currentPlayer.getCurrentWorm().getWormNumber();
        startTurnEvent.wind = windRandomizer.nextInt(Constants.WIND_RANGE + 1) + Constants.WIND_START;

        for (Player player : players) {
            player.getControllingUser().send(startTurnEvent, false);
        }

        System.out.println("Starting turn for player: " + startTurnEvent.playerNumber + ", worm: " + startTurnEvent.wormNumber + ", wormDead: " + currentPlayer.getCurrentWorm().isDead());
    }

    private void shiftTurn(boolean shiftWorms) {
        if (numPlayersAlive <= 0)
            return;

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).isDefeated());

        if (shiftWorms)
            players.get(currentPlayerIndex).shiftTurn();
    }

    private boolean isRoundEnded() {
        boolean roundEnded = true;

        for (Player player : players) {
            if (!player.isDisconnected() && !player.isRoundEnded()) {
                roundEnded = false;
                break;
            }
        }

        if (roundEnded)
            players.forEach(player -> player.setRoundEnded(false));

        return roundEnded;
    }
}
