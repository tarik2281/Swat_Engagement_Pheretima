package de.paluno.game;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import de.karaca.sep.client.NetClientVerticle;
import de.karaca.sep.client.NetListener;
import de.paluno.game.interfaces.*;
import de.paluno.game.interfaces.Constants;
import de.paluno.game.interfaces.GameEvent;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class NetworkClient {
    private static final Logger log = LoggerFactory.getLogger(NetworkClient.class);

    public interface ConnectionListener {
        void onConnectionResult(NetworkClient client, int result);
    }

    public interface DisconnectionListener {
        void onDisconnected(NetworkClient client);
    }

    public static final int RESULT_CONNECTION_FAILED = -1;
    public static final int RESULT_CONNECTION_SUCCESS = 0;

    private String remoteAddress;
    private Client client;
    private NetClientVerticle netClientVerticle;
    private ConnectionListener connectionListener;
    private DisconnectionListener disconnectionListener;
    private boolean userDisconnect = false;

    private ArrayList<DataHandler> addQueue;
    private ArrayList<DataHandler> removeQueue;
    private ArrayList<DataHandler> dataHandlers;

    private NetListener netListener = new NetListener() {
        @Override
        public void connected() {
            Gdx.app.postRunnable(() -> {
                if (connectionListener != null)
                    connectionListener.onConnectionResult(NetworkClient.this, RESULT_CONNECTION_SUCCESS);
            });
        }

        @Override
        public void disconnected() {
            Gdx.app.postRunnable(() -> {
                if (!userDisconnect && disconnectionListener != null)
                    disconnectionListener.onDisconnected(NetworkClient.this);
            });
        }

        @Override
        public void connectionFailed() {
            Gdx.app.postRunnable(() -> {
                if (connectionListener != null)
                    connectionListener.onConnectionResult(NetworkClient.this, RESULT_CONNECTION_FAILED);
            });
        }

        @Override
        public void received(Object object) {
            if (!(object instanceof WorldData)) {
                log.info("Data received: " + object.toString());

                if (object instanceof GameEvent gameEvent) {
                    log.info("GameEvent received: " + gameEvent.getType().name());
                } else if (object instanceof Message message) {
                    log.info("Message received: " + message.getType().name());
                }
            }
            //System.out.println("Data received: " + object.toString());
            if (!(object instanceof FrameworkMessage.KeepAlive))
                Gdx.app.postRunnable(() -> {
                    dataHandlers.addAll(addQueue);
                    dataHandlers.removeAll(removeQueue);

                    addQueue.clear();
                    removeQueue.clear();

                    for (DataHandler dataHandler : dataHandlers)
                        dataHandler.handleData(NetworkClient.this, object);
                });
        }
    };

    public NetworkClient(String remoteAddress) {
        this.remoteAddress = remoteAddress;

        addQueue = new ArrayList<>();
        removeQueue = new ArrayList<>();
        dataHandlers = new ArrayList<>();
    }

    public void setConnectionListener(ConnectionListener listener) {
        this.connectionListener = listener;
    }

    public void setDisconnectionListener(DisconnectionListener listener) {
        this.disconnectionListener = listener;
    }

    public void connect() {
        if (netClientVerticle == null) {
            Vertx vertx = Vertx.vertx();

            netClientVerticle = new NetClientVerticle(netListener);
            vertx.deployVerticle(netClientVerticle);
        }

//        if (client == null) {
//            client = new Client();
//
//            client.start();
//
//            KryoInterface.registerClasses(client.getKryo());
//
//            client.addListener(networkListener);
//
//            new Thread(() -> {
//                try {
//                    client.connect(5000, remoteAddress, Constants.TCP_PORT, Constants.UDP_PORT);
//                } catch (IOException e) {
//                    Gdx.app.postRunnable(() -> {
//                        if (connectionListener != null)
//                            connectionListener.onConnectionResult(NetworkClient.this, RESULT_CONNECTION_FAILED);
//                    });
//
//                    e.printStackTrace();
//                }
//            }).start();
//        }
    }

    public void updateRTT() {
        client.updateReturnTripTime();
    }

    public int getRTT() {
        return client.getReturnTripTime();
    }

    public void disconnect() {
        userDisconnect = true;
//        client.stop();

        netClientVerticle.getVertx().close();
        netClientVerticle = null;
    }

    public int getClientId() {
        return netClientVerticle.getSessionId().hashCode();
    }

    public void send(Object object) {
        send(object, false);
    }

    public void send(Object object, boolean preferUdp) {
        if (netClientVerticle == null)
            return;


        if (!(object instanceof WorldData)) {
            log.info("Sending data: " + object.toString());

            if (object instanceof GameEvent gameEvent) {
                log.info("Sending GameEvent: " + gameEvent.getType().name());
            } else if (object instanceof Message message) {
                log.info("Sending Message: " + message.getType().name());
            }
        }

        if (Config.udpEnabled && preferUdp)
            netClientVerticle.sendUdp(object);
        else
            netClientVerticle.sendTcp(object);
    }

    public void registerDataHandler(DataHandler handler) {
        addQueue.add(handler);
    }

    public void unregisterDataHandler(DataHandler handler) {
        removeQueue.add(handler);
    }
}
