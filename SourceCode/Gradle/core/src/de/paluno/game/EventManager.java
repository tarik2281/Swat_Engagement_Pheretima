package de.paluno.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class EventManager {
    public enum Type {
        WormInfected, WormDied, ProjectileExploded
    }

    public interface Listener {
        void handleEvent(Type eventType, Object data);
    }

    private class Data {
        private Type type;
        private Object data;
    }

    private ArrayDeque<Data>[] queues;
    private HashMap<Type, ArrayList<Listener>> listenerMap;
    private int activeQueue;

    private EventManager() {
        queues = new ArrayDeque[2];

        for (int i = 0; i < 2; i++)
            queues[i] = new ArrayDeque<>();

        listenerMap = new HashMap<>();

        activeQueue = 0;
    }

    public void addListener(Type eventType, Listener l) {
        listenerMap.computeIfAbsent(eventType, (e) -> new ArrayList<>(1)).add(l);
    }

    public void removeListener(Type eventType, Listener l) {
        ArrayList<Listener> listeners = listenerMap.get(eventType);

        if (listeners != null)
            listeners.remove(l);
    }

    public void processEvents() {
        int processingQueue = activeQueue;
        activeQueue = (activeQueue + 1) % 2;

        Data eventData;
        while ((eventData = queues[processingQueue].pollFirst()) != null) {
            ArrayList<Listener> listeners = listenerMap.get(eventData.type);

            if (listeners != null) {
                for (Listener l : listeners) {
                    l.handleEvent(eventData.type, eventData.data);
                }
            }
        }
    }

    public void queueEvent(Type type, Object data) {
        Data eventData = new Data();
        eventData.type = type;
        eventData.data = data;
        queues[activeQueue].addLast(eventData);
    }

    private static final EventManager singleton = new EventManager();

    public static EventManager getInstance() {
        return singleton;
    }
}
