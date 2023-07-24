package de.paluno.game.server.net;

import de.karaca.net.core.*;
import de.karaca.net.core.vertx.NetSystemVertx;
import de.paluno.game.interfaces.*;
import de.paluno.game.server.Lobby;
import de.paluno.game.server.User;
import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class GameServerVerticle extends AbstractVerticle {
    private final HashMap<Integer, Lobby> lobbyMap = new HashMap<>();
    private final HashMap<UUID, User> loggedInUsers = new HashMap<>();

    private int nextLobbyId = 0;

    private NetSessionServer netSessionServer;

    private final NetMessageRouter messageRouter = new NetMessageRouter()
        .route(UserLoginRequest.class, (netSession, message) -> {
            loginUser(netSession, message.getPayload());
            netSessionServer.send(netSession, NetMessage.from(new UserLoginRequest.Result(true)));
        })
        .request(LobbyCreateRequest.TYPE, (netSession, message) ->
            findUserById(netSession.getSessionId())
                .map(user -> {
                    Lobby lobby = createLobby(message.getName(), message.getMapNumber(), message.getNumWorms(), user);

                    LobbyCreateRequest.Result result = new LobbyCreateRequest.Result();
                    result.lobbyId = lobby.getId();

                    return result;
                })
        )
        .route(LobbyCreateRequest.class, (netSession, message) -> {
            User user = getUserById(netSession.getSessionId());
            if (user != null) {
                Lobby lobby = createLobby(message.getPayload().getName(), message.getPayload().getMapNumber(), message.getPayload().getNumWorms(), user);

                LobbyCreateRequest.Result result = new LobbyCreateRequest.Result();
                result.lobbyId = lobby.getId();
                netSessionServer.send(netSession, NetMessage.from(result));
            }
        })
        .request(LobbyJoinRequest.TYPE, (netSession, message) ->
            findUserById(netSession.getSessionId())
                .flatMap(user ->
                    findLobbyById(message.lobbyId)
                        .map(lobby -> lobby.joinUser(user))
                        .map(joined ->
                            new LobbyJoinRequest.Result().setSuccess(joined)
                        )
                )
        )
        .route(LobbyJoinRequest.class, (netSession, message) -> {
            boolean joined = false;
            User user = getUserById(netSession.getSessionId());
            if (user != null) {
                Lobby lobby = getLobbyById(message.getPayload().lobbyId);
                if (lobby != null) {
                    joined = lobby.joinUser(user);
                }

                LobbyJoinRequest.Result result = new LobbyJoinRequest.Result();
                result.success = joined;
                if (joined)
                    result.lobbyId = lobby.getId();
                else
                    result.lobbyId = Lobby.ID_NONE;

                netSessionServer.send(netSession, NetMessage.from(result));
            }
        })
        .request(LobbyListRequest.TYPE, (netSession, message) ->
            Optional.of(new LobbyListRequest.Result()
                .setLobbies(lobbyMap.values()
                    .stream()
                    .filter(Lobby::isOpen)
                    .map(l -> new LobbyData()
                        .setId(l.getId())
                        .setName(l.getName()))
                    .toList()
                )
            )
        )
//        .route(LobbyListRequest.class, (netSession, message) -> {
//            var lobbies = lobbyMap.values()
//                .stream()
//                .filter(Lobby::isOpen)
//                .map(l -> new LobbyData()
//                    .setId(l.getId())
//                    .setName(l.getName()))
//                .toList();
//
//            LobbyListRequest.Result result = new LobbyListRequest.Result();
//            result.lobbies = lobbies.toArray(new LobbyData[0]);
//
//            netSessionServer.send(netSession, NetMessage.from(result));
//        })
        .route(LobbyDataRequest.class, (netSession, message) -> {
            Lobby lobby = getLobbyById(message.getPayload().lobbyId);
            LobbyData data = null;
            if (lobby != null) {
                data = new LobbyData()
                    .setId(lobby.getId())
                    .setName(lobby.getName())
                    .setMapNumber(lobby.getMapNumber())
                    .setNumWorms(lobby.getNumWorms())
                    .setCreatingUserId(lobby.getCreatingUser().getId());
            }

            LobbyDataRequest.Result result = new LobbyDataRequest.Result();
            result.lobbyData = data;
            if (lobby != null)
                result.users = lobby.getUsers();

            netSessionServer.send(netSession, NetMessage.from(result));
        })
        .route(StartMatchRequest.class, (netSession, message) -> {
            User user = getUserById(netSession.getSessionId());
            if (user != null) {
                Lobby lobby = getLobbyById(user.getCurrentLobbyId());
                if (lobby != null)
                    lobby.startMatch();
            }
        })
        .route(ChatMessage.class, (netSession, message) -> {
            User user = getUserById(netSession.getSessionId());
            if (user != null) {
                Lobby lobby = getLobbyById(user.getCurrentLobbyId());
                if (lobby != null) {
                    ChatMessage chatMessage = message.getPayload();
                    chatMessage.setUserName(user.getName());
                    lobby.broadcastChatMessage(chatMessage);
                }
            }
        })
        .route(GameSetupData.class, (netSession, message) -> {
            User user = getUserById(netSession.getSessionId());
            if (user != null) {
                Lobby lobby = getLobbyById(user.getCurrentLobbyId());
                if (lobby != null) {
                    lobby.setupMatch(message.getPayload());
                }
            }
        })
        .route(Message.class, (netSession, message) -> {
            User user = getUserById(netSession.getSessionId());
            if (user != null) {
                Lobby lobby = getLobbyById(user.getCurrentLobbyId());
                if (lobby != null && message.getPayload().getType() == Message.Type.ClientReady) {
                    lobby.userReady(user);
                }
            }
        })
        .fallback((netSession, message) -> {
            if (message.getPayload() instanceof GameData gameData) {
                User user = getUserById(netSession.getSessionId());
                if (user != null) {
                    Lobby lobby = getLobbyById(user.getCurrentLobbyId());
                    if (lobby != null) {
                        lobby.handleGameData(user, gameData);
                    }
                }
            }
        });

    @Override
    public void start() throws Exception {
        var netSystem = NetSystemVertx.from(vertx);

        netSessionServer = NetSessionServer.create(netSystem)
            .onDisconnect(netSession -> logoutUser(netSession.getSessionId()))
            .onReceive(messageRouter)
            .start();
    }

    private int getNextLobbyId() {
        return nextLobbyId++;
    }

    private User getUserById(UUID id) {
        return loggedInUsers.get(id);
    }

    private Optional<User> findUserById(UUID id) {
        return Optional.ofNullable(getUserById(id));
    }

    private Lobby getLobbyById(int id) {
        return lobbyMap.get(id);
    }

    private Optional<Lobby> findLobbyById(int id) {
        return Optional.ofNullable(getLobbyById(id));
    }

    private User loginUser(NetSession netSession, UserLoginRequest request) {
        User user = new User(netSession, netSessionServer, request.getName(), request.getWormNames(), request.isUdpEnabled());
        loggedInUsers.put(netSession.getSessionId(), user);

        log.info("User logged in (id: {}, name: {}, worms: {}, udpEnabled: {})", user.getId(), user.getName(), Arrays.toString(user.getWormNames()), request.isUdpEnabled());
        return user;
    }

    private void logoutUser(UUID sessionId) {
        User user = loggedInUsers.remove(sessionId);

        if (user != null) {
            findLobbyById(user.getCurrentLobbyId())
                .ifPresent(lobby -> lobby.leaveUser(user));

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
