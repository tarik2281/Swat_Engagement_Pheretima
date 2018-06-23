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
        }

        @Override
        public void received(Connection connection, Object object) {
            super.received(connection, object);

            System.out.println("Data received: " + object.toString());

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
            for (Connection c : connections) {
                if (c.getID() != connection.getID())
                    c.sendTCP(data);
            }
        }
    };

    private DataHandler<MessageData> messageDataDataHandler = new DataHandler<MessageData>() {
        @Override
        public void handle(Connection connection, MessageData data) {
            switch (data.getType()) {
                case RequestGameSetupResult:

                    break;
            }
        }
    };

    public GameServer() {
        //lobbies = new ArrayList<>();
        connections = new ArrayList<>();
        objectHandlers = new HashMap<>();
    }

    public void initialize() {
        server = new Server();
        server.start();

        KryoInterface.registerClasses(server.getKryo());

        objectHandlers.put(WorldData.class, new WorldDataHandler().initialize(this));
        objectHandlers.put(GameSetupData.class, gameSetupDataDataHandler);

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
