package de.paluno.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import de.paluno.game.interfaces.*;
import de.paluno.game.interfaces.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

    private ArrayList<DataHandler> dataHandlers;
    //private HashMap<Class, DataHandler> dataHandlers;

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
                if (disconnectionListener != null)
                    disconnectionListener.onDisconnected(NetworkClient.this);
            });
        }

        @Override
        public void received(Connection connection, Object object) {
            //System.out.println("Data received: " + object.toString());

            if (!(object instanceof FrameworkMessage.KeepAlive))
                Gdx.app.postRunnable(() -> {
                    for (DataHandler dataHandler : dataHandlers)
                        dataHandler.handleData(NetworkClient.this, object);
                });
            /*DataHandler handler = dataHandlers.get(object.getClass());
            if (handler != null) {
                handler.handleData(NetworkClient.this, object);
            }
            else if (!(object instanceof FrameworkMessage.KeepAlive)){
                System.err.println("No DataHandler registered for " + object.getClass().getName());
            }*/
        }
    };

    public NetworkClient(String remoteAddress) {
        this.remoteAddress = remoteAddress;

        dataHandlers = new ArrayList<>();
    }

    public void setConnectionListener(ConnectionListener listener) {
        this.connectionListener = listener;
    }

    public void setDisconnectionListener(DisconnectionListener listener) {
        this.disconnectionListener = listener;
    }

    public void requestLogin(String userName, String[] wormNames) {
        if (client.isConnected()) {
            client.sendTCP(new UserLoginRequest(userName, wormNames));
        }
    }

    public void connect() {
        if (client == null) {
            client = new Client();

            client.start();

            KryoInterface.registerClasses(client.getKryo());

            client.addListener(networkListener);

            new Thread(() -> {
                try {
                    // TODO: hardcoded TCP port
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
        client.stop();
    }

    public int getClientId() {
        return client.getID();
    }

    public void sendObject(Object object) {
        client.sendTCP(object);
    }

    public void sendObjectUDP(Object object) {
        client.sendUDP(object);
    }

    public void registerDataHandler(DataHandler handler) {
        dataHandlers.add(handler);
    }

    public void unregisterDataHandler(DataHandler handler) {
        dataHandlers.remove(handler);
    }

    /*public <T> void registerDataHandler(Class<T> tClass, DataHandler handler) {
        dataHandlers.put(tClass, handler);
    }

    public <T> void unregisterDataHandler(Class<T> tClass, DataHandler handler) {
        dataHandlers.remove(tClass);
    }*/
}
