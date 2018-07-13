package de.paluno.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;

public class Config {

    public static String serverAddress;
    public static boolean fullscreen;

    public static void loadConfig(FileHandle fileHandle) {
        XmlReader reader = new XmlReader();
        XmlReader.Element root = reader.parse(fileHandle);

        serverAddress = root.get("ServerAddress");
        fullscreen = root.getBoolean("Fullscreen");
    }
}