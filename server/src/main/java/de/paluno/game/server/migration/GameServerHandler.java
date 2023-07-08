package de.paluno.game.server.migration;

import de.paluno.game.interfaces.*;
import de.paluno.game.server.Lobby;
import de.paluno.game.server.User;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class GameServerHandler implements NetListener {

    private final HashMap<Integer, Lobby> lobbyMap = new HashMap<>();
    private final HashMap<UUID, User> loggedInUsers = new HashMap<>();

    private int nextLobbyId = 0;

    @Override
    public void connected(NetSession netSession) {

    }

    @Override
    public void disconnected(NetSession netSession) {
        logoutUser(netSession.getSessionId());
    }

    @Override
    public void received(NetSession netSession, Object object) {
        if (object instanceof UserLoginRequest request) {
            loginUser(netSession, request);

            netSession.sendTCP(new UserLoginRequest.Result(true));
        } else if (object instanceof LobbyCreateRequest request) {

            User user = getUserById(netSession.getSessionId());
            if (user != null) {
                Lobby lobby = createLobby(request.getName(), request.getMapNumber(), request.getNumWorms(), user);

                LobbyCreateRequest.Result result = new LobbyCreateRequest.Result();
                result.lobbyId = lobby.getId();
                netSession.sendTCP(result);
            }
        } else if (object instanceof LobbyJoinRequest request) {

            boolean joined = false;
            Lobby lobby = lobbyMap.get(request.lobbyId);
            if (lobby != null) {
                User user = getUserById(netSession.getSessionId());
                if (user != null)
                    joined = lobby.joinUser(user);
            }

            LobbyJoinRequest.Result result = new LobbyJoinRequest.Result();
            result.success = joined;
            if (joined)
                result.lobbyId = lobby.getId();
            else
                result.lobbyId = Lobby.ID_NONE;
            netSession.sendTCP(result);
        } else if (object instanceof LobbyLeaveRequest) {
            User user = getUserById(netSession.getSessionId());
            if (user != null) {
                Lobby lobby = lobbyMap.get(user.getCurrentLobbyId());
                if (lobby != null)
                    lobby.leaveUser(user);

                LobbyLeaveRequest.Result result = new LobbyLeaveRequest.Result();
                result.success = true;
                netSession.sendTCP(result);
            }
        } else if (object instanceof LobbyListRequest) {
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
            netSession.sendTCP(result);
        } else if (object instanceof LobbyDataRequest request) {

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
            netSession.sendTCP(result);
        } else if (object instanceof StartMatchRequest request) {
            User user = getUserById(netSession.getSessionId());
            if (user != null) {
                Lobby lobby = getLobbyById(user.getCurrentLobbyId());
                if (lobby != null && lobby.getCreatingUser().getId() == user.getId()) {
                    lobby.startMatch();
                }
            }
        } else if (object instanceof ChatMessage message) {
            User user = getUserById(netSession.getSessionId());
            if (user != null) {
                Lobby lobby = getLobbyById(user.getCurrentLobbyId());

                if (lobby != null) {
                    message.setUserName(user.getName());

                    lobby.broadcastChatMessage(message);
                }
            }
        } else if (object instanceof GameSetupData) {
            User user = getUserById(netSession.getSessionId());
            if (user != null) {
                Lobby lobby = getLobbyById(user.getCurrentLobbyId());
                if (lobby != null) {
                    lobby.setupMatch((GameSetupData) object);
                }
            }
        } else if (object instanceof GameData) {
            User user = getUserById(netSession.getSessionId());
            if (user != null) {
                Lobby lobby = getLobbyById(user.getCurrentLobbyId());
                if (lobby != null) {
                    lobby.handleGameData(user, (GameData) object);
                }
            }
        } else if (object instanceof Message message) {
            User user = getUserById(netSession.getSessionId());
            log.info("Received message '{}' from session id '{}' for user id {}", message.getType().name(), netSession.getSessionId(), user != null ? user.getId() : null);

            Lobby lobby = null;
            if (user != null)
                lobby = getLobbyById(user.getCurrentLobbyId());

            if (Objects.requireNonNull(((Message) object).getType()) == Message.Type.ClientReady) {
                if (lobby != null)
                    lobby.userReady(user);
            }
        }
    }

    private int getNextLobbyId() {
        return nextLobbyId++;
    }

    private User getUserById(UUID id) {
        return loggedInUsers.get(id);
    }

    private Lobby getLobbyById(int id) {
        return lobbyMap.get(id);
    }

    private User loginUser(NetSession netSession, UserLoginRequest request) {
        User user = new User(netSession, request.getName(), request.getWormNames(), request.isUdpEnabled());
        loggedInUsers.put(netSession.getSessionId(), user);

        log.info("User logged in (id: {}, name: {}, worms: {}, udpEnabled: {})", user.getId(), user.getName(), Arrays.toString(user.getWormNames()), request.isUdpEnabled());
        return user;
    }

    private void logoutUser(UUID sessionId) {
        User user = loggedInUsers.remove(sessionId);

        if (user != null) {
            Lobby lobby = getLobbyById(user.getCurrentLobbyId());
            if (lobby != null)
                lobby.leaveUser(user);

            log.info("User logged out (id: {}, name: {})", user.getId(), user.getName());
        }
    }

    private Lobby createLobby(String name, int mapNumber, int numWorms, User creatingUser) {
        Lobby lobby = new Lobby(getNextLobbyId(), name, mapNumber, numWorms, creatingUser);
        lobby.setDestroyListener(() -> {
            lobbyMap.remove(lobby.getId());
            log.info("Destroyed lobby (id: {}, name: {})", lobby.getId(), lobby.getName());
        });

        lobbyMap.put(lobby.getId(), lobby);

        log.info("Created lobby (id: {}, name: {}, map: {}, worms: {}, creatingUser: {})", lobby.getId(), name, mapNumber, numWorms, creatingUser.getId());

        return lobby;
    }
}
