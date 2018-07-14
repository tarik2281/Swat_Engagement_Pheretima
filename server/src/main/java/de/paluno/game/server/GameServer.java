package de.paluno.game.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.paluno.game.interfaces.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameServer {

    private Server server;
    private int nextLobbyId;

    private HashMap<Integer, Lobby> lobbyMap;
    private HashMap<Integer, User> loggedInUsers;

    private Listener serverListener = new Listener() {
        @Override
        public void connected(Connection connection) {

        }

        @Override
        public void disconnected(Connection connection) {
            logoutUser(connection.getID());
        }

        @Override
        public void received(Connection connection, Object object) {
            super.received(connection, object);

            if (object instanceof UserLoginRequest) {
                UserLoginRequest request = (UserLoginRequest)object;
                loginUser(connection, request);

                connection.sendTCP(new UserLoginRequest.Result(true));
            }
            else if (object instanceof LobbyCreateRequest) {
                LobbyCreateRequest request = (LobbyCreateRequest)object;

                User user = getUserById(connection.getID());
                if (user != null) {
                    Lobby lobby = createLobby(request.getName(), request.getMapNumber(), request.getNumWorms(), user);

                    LobbyCreateRequest.Result result = new LobbyCreateRequest.Result();
                    result.lobbyId = lobby.getId();
                    connection.sendTCP(result);
                }
            }
            else if (object instanceof LobbyJoinRequest) {
                LobbyJoinRequest request = (LobbyJoinRequest)object;

                boolean joined = false;
                Lobby lobby = lobbyMap.get(request.lobbyId);
                if (lobby != null) {
                    User user = getUserById(connection.getID());
                    if (user != null)
                        joined = lobby.joinUser(user);
                }

                LobbyJoinRequest.Result result = new LobbyJoinRequest.Result();
                result.success = joined;
                if (joined)
                    result.lobbyId = lobby.getId();
                else
                    result.lobbyId = Lobby.ID_NONE;
                connection.sendTCP(result);
            }
            else if (object instanceof LobbyLeaveRequest) {
                User user = getUserById(connection.getID());
                if (user != null) {
                    Lobby lobby = lobbyMap.get(user.getCurrentLobbyId());
                    if (lobby != null)
                        lobby.leaveUser(user);

                    LobbyLeaveRequest.Result result = new LobbyLeaveRequest.Result();
                    result.success = true;
                    connection.sendTCP(result);
                }
            }
            else if (object instanceof LobbyListRequest) {
                ArrayList<LobbyData> lobbies = new ArrayList<>();

                for (Lobby lobby : lobbyMap.values()) {
                    if (lobby.isOpen()) {
                        LobbyData data = new LobbyData();
                        data.id = lobby.getId();
                        data.name = lobby.getName();
                        lobbies.add(data);
                    }
                }

                LobbyListRequest.Result result = new LobbyListRequest.Result();
                result.lobbies = lobbies.toArray(new LobbyData[0]);
                connection.sendTCP(result);
            }
            else if (object instanceof LobbyDataRequest) {
                LobbyDataRequest request = (LobbyDataRequest)object;

                LobbyData lobbyData = null;
                Lobby lobby = getLobbyById(request.lobbyId);
                if (lobby != null) {
                    lobbyData = new LobbyData();
                    lobbyData.id = lobby.getId();
                    lobbyData.name = lobby.getName();
                    lobbyData.mapNumber = lobby.getMapNumber();
                    lobbyData.numWorms = lobby.getNumWorms();
                    lobbyData.creatingUserId = lobby.getCreatingUser().getId();
                }

                LobbyDataRequest.Result result = new LobbyDataRequest.Result();
                result.lobbyData = lobbyData;
                if (lobby != null)
                    result.users = lobby.getUsers();
                connection.sendTCP(result);
            }
            else if (object instanceof StartMatchRequest) {
                StartMatchRequest request = (StartMatchRequest)object;
                User user = getUserById(connection.getID());
                if (user != null) {
                    Lobby lobby = getLobbyById(user.getCurrentLobbyId());
                    if (lobby != null && lobby.getCreatingUser().getId() == user.getId()) {
                        lobby.startMatch();
                    }
                }
            }
            else if (object instanceof ChatMessage) {
                ChatMessage message = (ChatMessage)object;
                User user = getUserById(connection.getID());
                if (user != null) {
                    Lobby lobby = getLobbyById(user.getCurrentLobbyId());

                    if (lobby != null) {
                        message.setUserName(user.getName());

                        lobby.broadcastChatMessage(message);
                    }
                }
            }
            else if (object instanceof GameSetupData) {
                User user = getUserById(connection.getID());
                if (user != null) {
                    Lobby lobby = getLobbyById(user.getCurrentLobbyId());
                    if (lobby != null) {
                        lobby.setupMatch((GameSetupData)object);
                    }
                }
            }
            else if (object instanceof GameData) {
                User user = getUserById(connection.getID());
                if (user != null) {
                    Lobby lobby = getLobbyById(user.getCurrentLobbyId());
                    if (lobby != null) {
                        lobby.handleGameData(user, (GameData)object);
                    }
                }
            }
            else if (object instanceof Message) {
                User user = getUserById(connection.getID());

                Lobby lobby = null;
                if (user != null)
                    lobby = getLobbyById(user.getCurrentLobbyId());

                switch (((Message) object).getType()) {
                    case ClientReady:
                        if (lobby != null)
                            lobby.userReady(user);
                        break;
                }
            }
        }
    };

    public GameServer() {
        nextLobbyId = 0;
        lobbyMap = new HashMap<>();
        loggedInUsers = new HashMap<>();
    }

    private int getNextLobbyId() {
        return nextLobbyId++;
    }

    private User getUserById(int id) {
        return loggedInUsers.get(id);
    }

    private Lobby getLobbyById(int id) {
        return lobbyMap.get(id);
    }

    private User loginUser(Connection connection, UserLoginRequest request) {
        User user = new User(connection, request.getName(), request.getWormNames(), request.isUdpEnabled());
        loggedInUsers.put(connection.getID(), user);

        System.out.printf("User logged in (id: %d, name: %s, worms: %s, udpEnabled: %b)\n", user.getId(), user.getName(), Arrays.toString(user.getWormNames()), request.isUdpEnabled());
        return user;
    }

    private void logoutUser(int id) {
        User user = loggedInUsers.remove(id);

        if (user != null) {
            Lobby lobby = getLobbyById(user.getCurrentLobbyId());
            if (lobby != null)
                lobby.leaveUser(user);

            System.out.printf("User logged out (id: %d, name: %s)\n", user.getId(), user.getName());
        }
    }

    private Lobby createLobby(String name, int mapNumber, int numWorms, User creatingUser) {
        Lobby lobby = new Lobby(getNextLobbyId(), name, mapNumber, numWorms, creatingUser);
        lobby.setDestroyListener(() -> {
            lobbyMap.remove(lobby.getId());
            System.out.printf("Destroyed lobby (id: %d, name: %s)\n", lobby.getId(), lobby.getName());
        });

        lobbyMap.put(lobby.getId(), lobby);

        System.out.printf("Created lobby (id: %d, name: %s, map: %d, worms: %d, creatingUser: %d)\n", lobby.getId(), name, mapNumber, numWorms, creatingUser.getId());

        return lobby;
    }

    public void initialize(int tcpPort, int udpPort) {
        server = new Server();
        server.start();

        KryoInterface.registerClasses(server.getKryo());

        server.addListener(serverListener);

        try {
            server.bind(tcpPort, udpPort);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GameServer server = new GameServer();

        int tcpPort = Constants.TCP_PORT;
        int udpPort = Constants.UDP_PORT;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-tcp_port")) {
                if (++i >= args.length) {
                    System.out.println(USAGE_MESSAGE);
                    System.exit(-1);
                }
                else {
                    try {
                        tcpPort = Integer.parseInt(args[i]);
                    }
                    catch (NumberFormatException e) {
                        System.out.println(USAGE_MESSAGE);
                        System.exit(-1);
                    }
                }
            }
            else if (args[i].equals("-udp_port")) {
                if (++i >= args.length) {
                    System.out.println(USAGE_MESSAGE);
                    System.exit(-1);
                }
                else {
                    try {
                        udpPort = Integer.parseInt(args[i]);
                    }
                    catch (NumberFormatException e) {
                        System.out.println(USAGE_MESSAGE);
                        System.exit(-1);
                    }
                }
            }
            else {
                System.out.println(USAGE_MESSAGE);
                System.exit(-1);
            }
        }

        System.out.println("Starting server on TCP port: " + tcpPort + " and UDP port: " + udpPort);
        server.initialize(tcpPort, udpPort);
    }

    private static final String USAGE_MESSAGE = "Usage: server.jar [-tcp_port PORT_NUMBER] [-udp_port PORT_NUMBER]";
}
