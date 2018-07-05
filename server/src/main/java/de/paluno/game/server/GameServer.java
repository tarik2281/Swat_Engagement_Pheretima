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
    //private HashMap<Class, DataHandler> objectHandlers;


    private int nextLobbyId;
    private ArrayList<Lobby> lobbies;
    private HashMap<Integer, Lobby2> lobbyMap;
    private HashMap<Integer, User> loggedInUsers;

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

            if (object instanceof UserLoginRequest) {
                UserLoginRequest request = (UserLoginRequest)object;
                loggedInUsers.computeIfAbsent(connection.getID(), (k) -> new User(connection, request.getName(), request.getWormNames()));

                connection.sendTCP(new UserLoginRequest.Result(true));
            }
            else if (object instanceof LobbyCreateRequest) {
                LobbyCreateRequest request = (LobbyCreateRequest)object;

                Lobby2 lobby = new Lobby2(getNextLobbyId(), request.getName(), request.getMapNumber(), request.getNumWorms());
                lobbyMap.put(lobby.getId(), lobby);
            }

            /*DataHandler handler = objectHandlers.get(object.getClass());
            if (handler != null) {
                handler.handle(connection, object);
            }*/
        }
    };

    /*private DataHandler<GameSetupData> gameSetupDataHandler = (connection, data) -> getLobbyForConnection(connection).onReceiveGameSetupData(connection, data);

    private DataHandler<WorldData> worldDataHandler = (connection, data) -> getLobbyForConnection(connection).onReceiveWorldData(connection, data);

    private DataHandler<GameEvent> eventHandler = (connection, data) -> getLobbyForConnection(connection).onReceiveGameEvent(connection, data);

    private DataHandler<MessageData> messageDataDataHandler = (connection, data) -> {
        Lobby lobby = getLobbyForConnection(connection);

        switch (data.getType()) {
            case ClientReady:
                lobby = getLobbyForConnection(connection);
                if (lobby != null) {
                    lobby.setClientReady(connection);
                }
                break;
            case ChatMessage:
                if (lobby != null) {
                    lobby.broadcastMessage(connection, data);
                }
                break;
        }
    };*/

    public GameServer() {
        nextLobbyId = 0;
        lobbies = new ArrayList<>();
        loggedInUsers = new HashMap<>();
        //objectHandlers = new HashMap<>();
    }

    private int getNextLobbyId() {
        return nextLobbyId++;
    }

    public void initialize() {
        server = new Server();
        server.start();

        KryoInterface.registerClasses(server.getKryo());

        /*objectHandlers.put(WorldData.class, worldDataHandler);
        objectHandlers.put(GameSetupData.class, gameSetupDataHandler);
        objectHandlers.put(MessageData.class, messageDataDataHandler);
        objectHandlers.put(ChatMessage.class, messageDataDataHandler);

        registerDataHandler(eventHandler, ExplosionEvent.class, ShootEvent.class,
                WormEvent.class, WormDamageEvent.class);*/

        server.addListener(serverListener);

        try {
            server.bind(Constants.TCP_PORT, Constants.UDP_PORT);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private void registerDataHandler(DataHandler handler, Class... classes) {
        for (Class clazz : classes) {
            objectHandlers.put(clazz, handler);
        }
    }*/

    public static void main(String[] args) {
        GameServer server = new GameServer();

        server.initialize();
    }
}
