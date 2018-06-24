package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.paluno.game.interfaces.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class GameServer {

    private Server server;
    public ArrayList<Connection> connections;
    private HashMap<Class, DataHandler> objectHandlers;
    public ArrayList<ClientState> clientStates;
    //private ArrayList<Lobby> lobbies;

    private Listener serverListener = new Listener() {
        @Override
        public void connected(Connection connection) {
            if (connections.size() < 2) {

                MessageData message = new MessageData(MessageData.Type.PlayerJoined);
                for (Connection c : connections) {
                    c.sendTCP(message);
                }

                connections.add(connection);
                clientStates.add(new ClientState(connection.getID()));

                if (connections.size() == 2) {
                    // start game but request setup data from client

                    int[] clientIds = new int[connections.size()];
                    int[] playerIds = new int[connections.size()];

                    for (int i = 0; i < connections.size(); i++) {
                        Connection c = connections.get(i);
                        clientIds[i] = c.getID();
                        playerIds[i] = i;
                    }

                    GameSetupRequest request = new GameSetupRequest(clientIds, playerIds);
                    //MessageData request = new MessageData(MessageData.Type.RequestGameSetup);
                    System.out.println("Sending game setup request");
                    connections.get(0).sendTCP(request);

                    /*MessageData messageData = new MessageData(MessageData.Type.GameStarted);
                    for (Connection c : connections) {
                        c.sendTCP(messageData);
                    }*/
                }
            }
        }

        @Override
        public void disconnected(Connection connection) {
            connections.remove(connection);
            clientStates.removeIf(clientState -> clientState.clientId == connection.getID());
        }

        @Override
        public void received(Connection connection, Object object) {
            super.received(connection, object);

            //System.out.println("Data received: " + object.toString());

            DataHandler handler = objectHandlers.get(object.getClass());
            if (handler != null) {
                handler.handle(connection, object);
            }
        }
    };

    private DataHandler<GameSetupData> gameSetupDataDataHandler = new DataHandler<GameSetupData>() {
        @Override
        public void handle(Connection connection, GameSetupData data) {
            System.out.println("Broadcasting gameSetup");

            numWorms = 1;

            for (Connection c : connections) {
                if (c.getID() != connection.getID())
                    c.sendTCP(data);
            }
        }
    };

    private DataHandler<WorldData> worldDataHandler = new DataHandler<>() {
        @Override
        public void handle(Connection connection, WorldData data) {
            for (Connection c : connections) {
                if (connection.getID() == c.getID()) {
                    c.sendTCP(data);
                }
            }
        }
    };

    private DataHandler<MessageData> messageDataDataHandler = new DataHandler<MessageData>() {
        @Override
        public void handle(Connection connection, MessageData data) {
            switch (data.getType()) {
                case ClientReady:
                    clientStates.forEach(clientState -> { if (clientState.clientId == connection.getID()) clientState.ready = true; });
                    //clientsReady.put(connection.getID(), true);

                    boolean allClientsReady = true;

                    for (ClientState state : clientStates) {
                        if (!state.ready) {
                            allClientsReady = false;
                            break;
                        }
                    }

                    if (allClientsReady) {
                        StartTurnEvent event = new StartTurnEvent();

                        event.playerNumber = currentPlayer;
                        event.wormNumber = playerTurns[currentPlayer];

                        for (Connection c : connections)
                            c.sendTCP(event);

                        shiftTurn();

                        clientStates.forEach(clientState -> clientState.ready = false );
                    }

                    break;
            }
        }
    };

    private int currentPlayer;
    private int[] playerTurns;
    private int numWorms;

    private void shiftTurn() {
        playerTurns[currentPlayer] = (playerTurns[currentPlayer] + 1) % numWorms;
        currentPlayer = (currentPlayer + 1) % 2;
    }

    public GameServer() {
        //lobbies = new ArrayList<>();
        connections = new ArrayList<>();
        objectHandlers = new HashMap<>();
        clientStates = new ArrayList<>();

        playerTurns = new int[2];
    }

    public void initialize() {
        server = new Server();
        server.start();

        KryoInterface.registerClasses(server.getKryo());

        objectHandlers.put(WorldData.class, new WorldDataHandler().initialize(this));
        objectHandlers.put(GameSetupData.class, gameSetupDataDataHandler);
        objectHandlers.put(MessageData.class, messageDataDataHandler);

        server.addListener(serverListener);

        try {
            server.bind(Constants.TCP_PORT);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GameServer server = new GameServer();

        server.initialize();
    }
}
