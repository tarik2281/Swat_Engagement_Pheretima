package de.paluno.game.server;

import de.paluno.game.interfaces.*;

import java.util.ArrayList;
import java.util.Random;

public class Match {

    private static final int STATE_PLAYER_TURN = 1;
    private static final int STATE_TURRETS_SHOOT = 2;
    private static final int STATE_AIRDROP = 3;

    private int currentTick;

    private int currentPlayerIndex;
    private ArrayList<Player> players;
    private int numPlayersAlive;

    private Random windRandomizer;
    private Lobby lobby;

    private int state;

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
                for (Worm worm : player.getWorms())
                    worm.setDead(true);

                if (!sendGameOver() && currentPlayerIndex == player.getNumber()) {
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

                if (player.getNumber() == currentPlayerIndex) {
                    System.out.println("broadcasting end turn event");
                    //lobby.broadcastData(user, new GameEvent(currentTick, GameEvent.Type.END_TURN));
                }
                break;
            }

        if (allClientsReady()) {
            if (isRoundEnded()) {
                Player simulatingPlayer = null;
                for (Player player : players) {
                    if (player.getControllingUser().getConnection().isConnected()) {
                        simulatingPlayer = player;
                        break;
                    }
                }

                state = STATE_TURRETS_SHOOT;
                lobby.broadcastData(null, new TurretsShootRequest(simulatingPlayer.getControllingUser().getId()));
            }
            else if (state == STATE_TURRETS_SHOOT) {
                Player simulatingPlayer = null;
                for (Player player : players) {
                    if (player.getControllingUser().getConnection().isConnected()) {
                        simulatingPlayer = player;
                        break;
                    }
                }

                state = STATE_AIRDROP;
                lobby.broadcastData(null, new SpawnAirdropRequest(simulatingPlayer.getControllingUser().getId()));
            }
            else {
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

    public Worm getWorm(WormEvent event) {
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

    public boolean sendGameOver() {
        if (numPlayersAlive < Constants.NUM_MIN_PLAYERS) {
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

        if (!sendGameOver()) {
            applyWormInfectionDamage();

            StartTurnEvent startTurnEvent = new StartTurnEvent();

            Player currentPlayer = getCurrentPlayer();
            startTurnEvent.playerNumber = currentPlayer.getNumber();
            startTurnEvent.wormNumber = currentPlayer.getCurrentWorm().getWormNumber();
            startTurnEvent.wind = windRandomizer.nextInt(Constants.WIND_RANGE + 1) + Constants.WIND_START;

            for (Player player : players) {
                player.getControllingUser().getConnection().sendTCP(startTurnEvent);
            }

            System.out.println("Starting turn for player: " + startTurnEvent.playerNumber + ", worm: " + startTurnEvent.wormNumber + ", wormDead: " + currentPlayer.getCurrentWorm().isDead());
        }
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
            if (!player.isRoundEnded()) {
                roundEnded = false;
                break;
            }
        }

        if (roundEnded)
            players.forEach(player -> player.setRoundEnded(false));

        return roundEnded;
    }
}
