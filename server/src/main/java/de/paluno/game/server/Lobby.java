package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;
import de.paluno.game.interfaces.*;

import java.util.ArrayList;

public class Lobby {

    private int currentPlayerIndex;
    private ArrayList<Player> players;

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
            if (player.connection.getID() == connection.getID())
                return true;
        }

        return false;
    }

    public void addPlayerConnection(Connection connection) {
        if (isClosed())
            return;

        MessageData data = new MessageData(MessageData.Type.PlayerJoined);
        players.forEach(player -> player.connection.sendTCP(data));

        Player player = new Player();
        player.connection = connection;
        player.ready = false;
        player.currentWormIndex = 0;
        players.add(player);

        if (players.size() == 2) {
            startGame();
            closed = true;
        }
    }

    public void startGame() {
        int[] clientIds = new int[players.size()];
        int[] playerNumbers = new int[players.size()];

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            clientIds[i] = player.connection.getID();
            playerNumbers[i] = i;
        }

        GameSetupRequest setupRequest = new GameSetupRequest(clientIds, playerNumbers);
        players.get(0).connection.sendTCP(setupRequest);
    }

    public void setClientReady(Connection connection) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.connection.getID() == connection.getID()) {
                player.ready = true;

                if (i == currentPlayerIndex)
                    broadcast(connection, new EndTurnEvent());

                break;
            }
        }

        if (allClientsReady()) {
            startTurn();
        }
    }

    private void startTurn() {
        StartTurnEvent event = new StartTurnEvent();

        shiftTurn();

        event.playerNumber = currentPlayerIndex;
        event.wormNumber = players.get(currentPlayerIndex).worms.get(players.get(currentPlayerIndex).currentWormIndex).wormNumber;

        for (Player player : players) {
            player.connection.sendTCP(event);
            player.ready = false;
        }
    }

    private boolean allClientsReady() {
        boolean ready = true;

        for (Player player : players) {
            if (!player.ready) {
                ready = false;
                break;
            }
        }

        return ready;
    }

    public void onReceiveGameEvent(Connection source, GameEvent event) {
        broadcast(source, event);
    }

    public void onReceiveWorldData(Connection source, WorldData data) {
        broadcast(source, data);
    }

    public void onReceiveGameSetupData(Connection source, GameSetupData data) {
        for (PlayerData playerData : data.getPlayerData()) {
            Player player = players.get(playerData.getPlayerNumber());
            for (WormData wormData : playerData.getWorms()) {
                Worm worm = new Worm();
                worm.wormNumber = wormData.wormNumber;
                player.worms.add(worm);
            }
        }

        broadcast(source, data);
    }

    private void broadcast(Connection source, Object data) {
        for (Player player : players) {
            if (source == null || player.connection.getID() != source.getID())
                player.connection.sendTCP(data);
        }
    }

    public void shiftTurn() {

        if (currentPlayerIndex != -1) {
            Player player = players.get(currentPlayerIndex);
            player.currentWormIndex = (player.currentWormIndex + 1) % player.worms.size();
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
}
