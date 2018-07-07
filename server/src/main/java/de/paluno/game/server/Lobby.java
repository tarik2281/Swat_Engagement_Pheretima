/*package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;
import de.paluno.game.interfaces.*;

import java.util.ArrayList;
import java.util.Random;

public class Lobby {

    private String name;
    private int currentPlayerIndex;
    private ArrayList<Player> players;
    private int numPlayersAlive;

    private Random windRandomizer = new Random();

    private boolean closed;

    public Lobby() {
        currentPlayerIndex = -1;
        players = new ArrayList<>();
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isInLobby(Connection connection) {
        for (Player player : players) {
            if (player.getConnection().getID() == connection.getID())
                return true;
        }

        return false;
    }

    public void addPlayerConnection(Connection connection) {
        if (isClosed())
            return;

        Player player = new Player(null, players.size());
        player.setDefeatedListener(() -> numPlayersAlive--);
        players.add(player);

        if (players.size() == 2) {
            startGame();
            closed = true;
        }
    }

    public void startGame() {
        int[] clientIds = new int[players.size()];
        int[] playerNumbers = new int[players.size()];

        numPlayersAlive = 0;
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            clientIds[i] = player.getConnection().getID();
            playerNumbers[i] = player.getNumber();
            numPlayersAlive++;
        }

        GameSetupRequest setupRequest = new GameSetupRequest();
        players.get(0).getConnection().sendTCP(setupRequest);
    }

    public void setClientReady(Connection connection) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.getConnection().getID() == connection.getID()) {
                player.setReady(true);

                if (i == currentPlayerIndex)
                    broadcast(connection, new GameEvent(0, GameEvent.Type.END_TURN));

                break;
            }
        }

        if (allClientsReady()) {
            startTurn();
        }
    }

    private void applyWormInfectionDamage() {
        Player currentPlayer = getCurrentPlayer();
        Worm worm = currentPlayer.getCurrentWorm();

        if (worm.isInfected()) {
            WormDamageEvent damageEvent = worm.takeDamage(Constants.VIRUS_DAMAGE, Constants.DAMAGE_TYPE_VIRUS);

            if (worm.isDead()) {
                broadcast(null, new WormEvent(0, GameEvent.Type.WORM_DIED, worm.getPlayerNumber(), worm.getWormNumber()));

                if (numPlayersAlive >= Constants.NUM_MIN_PLAYERS) {
                    if (currentPlayer.isDefeated()) {
                        shiftTurn(false);
                        applyWormInfectionDamage();
                    }
                    else {
                        currentPlayer.shiftTurn();
                    }
                }
            }
            else {
                broadcast(null, damageEvent);
            }
        }
    }

    private void startTurn() {
        shiftTurn(true);

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
            broadcast(null, gameOverEvent);
        }
        else {
            applyWormInfectionDamage();

            StartTurnEvent startTurnEvent = new StartTurnEvent();

            Player currentPlayer = getCurrentPlayer();
            startTurnEvent.playerNumber = currentPlayer.getNumber();
            startTurnEvent.wormNumber = currentPlayer.getCurrentWorm().getWormNumber();
            startTurnEvent.wind = windRandomizer.nextInt(Constants.WIND_RANGE + 1) + Constants.WIND_START;

            for (Player player : players) {
                player.getConnection().sendTCP(startTurnEvent);
                player.setReady(false);
            }
        }
    }

    private boolean allClientsReady() {
        boolean ready = true;

        for (Player player : players) {
            if (!player.isReady()) {
                ready = false;
                break;
            }
        }

        return ready;
    }

    public void onReceiveGameEvent(Connection source, GameEvent event) {
        switch (event.getType()) {
            case WORM_DIED: {
                WormEvent wormEvent = (WormEvent)event;
                getWorm(wormEvent).setDead(true);
                break;
            }
            case WORM_TOOK_DAMAGE: {
                WormDamageEvent wormEvent = (WormDamageEvent)event;
                getWorm(wormEvent).applyDamage(wormEvent.getDamage());
                break;
            }
            case WORM_INFECTED: {
                WormEvent wormEvent = (WormEvent)event;
                getWorm(wormEvent).setInfected(true);
                break;
            }
        }

        broadcast(source, event);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Player getPlayerByNumber(int number) {
        return players.get(number);
    }

    public Worm getWorm(WormEvent event) {
        return getPlayerByNumber(event.getPlayerNumber()).getWormByNumber(event.getWormNumber());
    }

    public void onReceiveWorldData(Connection source, WorldData data) {
        if (data.isUsingTCP())
            broadcast(source, data);
        else
            broadcastUDP(source, data);
    }

    public void onReceiveGameSetupData(Connection source, GameSetupData data) {
        for (PlayerData playerData : data.getPlayerData()) {
            Player player = players.get(playerData.getPlayerNumber());
            player.setupFromData(playerData);
        }

        broadcast(source, data);
    }

    private void broadcast(Connection source, Object data) {
        for (Player player : players) {
            if (source == null || player.getConnection().getID() != source.getID())
                player.getConnection().sendTCP(data);
        }
    }

    private void broadcastUDP(Connection source, Object data) {
        for (Player player : players) {
            if (source == null || player.getConnection().getID() != source.getID())
                player.getConnection().sendUDP(data);
        }
    }

    public void shiftTurn(boolean shiftWorms) {
        if (numPlayersAlive <= 0)
            return;

        if (shiftWorms && currentPlayerIndex != -1) {
            players.get(currentPlayerIndex).shiftTurn();
        }

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (getPlayerByNumber(currentPlayerIndex).isDefeated());
    }

    public void broadcastMessage(Connection source, Message data) {
        switch (data.getType()) {
            case ChatMessage: {
                ChatMessage message = (ChatMessage)data;

                for (Player player : players) {
                    if (player.getConnection().getID() == source.getID()) {
                        message.setPlayer(player.getNumber());
                        break;
                    }
                }

                broadcast(null, data);

                break;
            }
        }
    }
}
*/