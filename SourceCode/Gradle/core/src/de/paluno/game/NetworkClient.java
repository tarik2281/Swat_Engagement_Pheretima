package de.paluno.game;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import de.paluno.game.interfaces.*;
import de.paluno.game.interfaces.Constants;

import java.io.IOException;
import java.util.ArrayList;

public class NetworkClient {

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
    private ConnectionListener connectionListener;
    private DisconnectionListener disconnectionListener;
    private boolean userDisconnect = false;

    private ArrayList<DataHandler> addQueue;
    private ArrayList<DataHandler> removeQueue;
    private ArrayList<DataHandler> dataHandlers;

    private Listener networkListener = new Listener() {
        @Override
        public void connected(Connection connection) {
            Gdx.app.postRunnable(() -> {
                if (connectionListener != null)
                    connectionListener.onConnectionResult(NetworkClient.this, RESULT_CONNECTION_SUCCESS);
            });
        }

        @Override
        public void disconnected(Connection connection) {
            Gdx.app.postRunnable(() -> {
                if (!userDisconnect && disconnectionListener != null)
                    disconnectionListener.onDisconnected(NetworkClient.this);
            });
        }

        @Override
        public void received(Connection connection, Object object) {
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
        if (client == null) {
            client = new Client();

            client.start();

            KryoInterface.registerClasses(client.getKryo());

            client.addListener(networkListener);

            new Thread(() -> {
                try {
                    client.connect(5000, remoteAddress, Constants.TCP_PORT, Constants.UDP_PORT);
                } catch (IOException e) {
                    Gdx.app.postRunnable(() -> {
                        if (connectionListener != null)
                            connectionListener.onConnectionResult(NetworkClient.this, RESULT_CONNECTION_FAILED);
                    });

                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void updateRTT() {
        client.updateReturnTripTime();
    }

    public int getRTT() {
        return client.getReturnTripTime();
    }

    public void disconnect() {
        userDisconnect = true;
        client.stop();
    }

    public int getClientId() {
        return client.getID();
    }

    public void send(Object object) {
        client.sendTCP(object);
    }

    public void sendUDP(Object object) {
        client.sendUDP(object);
    }

    public void registerDataHandler(DataHandler handler) {
        addQueue.add(handler);
    }

    public void unregisterDataHandler(DataHandler handler) {
        removeQueue.add(handler);
    }
}
