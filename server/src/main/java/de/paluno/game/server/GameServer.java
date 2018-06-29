package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.paluno.game.interfaces.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameServer {

    private Server server;
    private HashMap<Class, DataHandler> objectHandlers;
    private ArrayList<Lobby> lobbies;

    private Lobby getLobbyForConnection(Connection connection) {
        for (Lobby lobby : lobbies) {
            if (lobby.isInLobby(connection))
                return lobby;
        }

        return null;
    }

    private Listener serverListener = new Listener() {
        @Override
        public void connected(Connection connection) {
            Lobby lobby = null;

            if (!lobbies.isEmpty()) {
                lobby = lobbies.get(lobbies.size() - 1);
                if (lobby.isClosed()) {
                    lobby = new Lobby();
                    lobbies.add(lobby);
                }
            }
            else {
                lobby = new Lobby();
                lobbies.add(lobby);
            }

            lobby.addPlayerConnection(connection);
        }

        @Override
        public void disconnected(Connection connection) {
            //Lobby lobby = getLobbyForConnection(connection);
            //connections.remove(connection);
            //clientStates.removeIf(clientState -> clientState.clientId == connection.getID());
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

    private DataHandler<GameSetupData> gameSetupDataHandler = (connection, data) -> getLobbyForConnection(connection).onReceiveGameSetupData(connection, data);

    private DataHandler<WorldData> worldDataHandler = (connection, data) -> getLobbyForConnection(connection).onReceiveWorldData(connection, data);

    private DataHandler<GameEvent> eventHandler = (connection, data) -> getLobbyForConnection(connection).onReceiveGameEvent(connection, data);

    private DataHandler<MessageData> messageDataDataHandler = (connection, data) -> {
        switch (data.getType()) {
            case ClientReady:
                Lobby lobby = getLobbyForConnection(connection);
                if (lobby != null) {
                    lobby.setClientReady(connection);
                }
                break;
        }
    };

    public GameServer() {
        lobbies = new ArrayList<>();
        objectHandlers = new HashMap<>();
    }

    public void initialize() {
        server = new Server();
        server.start();

        KryoInterface.registerClasses(server.getKryo());

        objectHandlers.put(WorldData.class, worldDataHandler);
        objectHandlers.put(GameSetupData.class, gameSetupDataHandler);
        objectHandlers.put(MessageData.class, messageDataDataHandler);

        registerDataHandler(eventHandler, ExplosionEvent.class, ShootEvent.class,
                WormEvent.class, WormDamageEvent.class);

        server.addListener(serverListener);

        try {
            server.bind(5000, 5001);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerDataHandler(DataHandler handler, Class... classes) {
        for (Class clazz : classes) {
            objectHandlers.put(clazz, handler);
        }
    }

    public static void main(String[] args) {
        GameServer server = new GameServer();

        server.initialize();
    }
}
