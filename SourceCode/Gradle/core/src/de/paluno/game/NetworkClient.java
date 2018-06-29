package de.paluno.game;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import de.paluno.game.gameobjects.Player;
import de.paluno.game.interfaces.*;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.HashMap;
import java.util.TreeMap;

public class NetworkClient {

    private String remoteAddress;
    private Client client;

    private HashMap<Class, DataHandler> dataHandlers;

    private Listener networkListener = new Listener() {
        @Override
        public void connected(Connection connection) {
            super.connected(connection);
        }

        @Override
        public void disconnected(Connection connection) {
            super.disconnected(connection);
        }

        @Override
        public void received(Connection connection, Object object) {
            //System.out.println("Data received: " + object.toString());

            DataHandler handler = dataHandlers.get(object.getClass());
            if (handler != null) {
                handler.handleData(NetworkClient.this, object);
            }
            else if (!(object instanceof FrameworkMessage.KeepAlive)){
                System.err.println("No DataHandler registered for " + object.getClass().getName());
            }
        }
    };

    public NetworkClient(String remoteAddress) {
        this.remoteAddress = remoteAddress;

        dataHandlers = new HashMap<>();
    }

    public void connect() {
        if (client == null) {
            client = new Client();

            client.start();

            KryoInterface.registerClasses(client.getKryo());

            client.addListener(networkListener);

            try {
                // TODO: hardcoded TCP port
                client.connect(5000, remoteAddress, 5000, 5001);
            } catch (IOException e) {
                // TODO: client connection error handling
                e.printStackTrace();
            }
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

    public <T> void registerDataHandler(Class<T> tClass, DataHandler handler) {
        dataHandlers.put(tClass, handler);
    }

    public <T> void unregisterDataHandler(Class<T> tClass, DataHandler handler) {
        dataHandlers.remove(tClass);
    }
}
