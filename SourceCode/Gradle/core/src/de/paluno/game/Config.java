package de.paluno.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;

public class Config {

    public static String serverAddress;
    public static int serverTcpPort;
    public static int serverUdpPort;
    public static boolean udpEnabled;
    public static boolean fullscreen;

    public static void loadConfig(FileHandle fileHandle) {
        XmlReader reader = new XmlReader();
        XmlReader.Element root = reader.parse(fileHandle);

        serverAddress = root.get("ServerAddress");
        serverTcpPort = root.getInt("ServerTCPPort");
        serverUdpPort = root.getInt("ServerUDPPort");
        udpEnabled = root.getBoolean("UDPEnabled");
        fullscreen = root.getBoolean("Fullscreen");
    }
}
