package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler<T>> activeConnections;

    public ConnectionsImpl() {
        this.activeConnections = new ConcurrentHashMap<>();
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if (activeConnections.containsKey(connectionId)) {
            activeConnections.get(connectionId).send(msg);
            return true; // msg sent successfully
        }
        return false; // msg haven't been sent
    }

    @Override
    public void broadcast(T msg) {
        for (Integer connection : activeConnections.keySet()) {
            activeConnections.get(connection).send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        if (activeConnections.contains(connectionId))
            activeConnections.remove(connectionId);
    }

    public void registerConnection(int connectionId, ConnectionHandler client) {
        if (!activeConnections.containsKey(connectionId)) {
            activeConnections.put(connectionId, client);
        }

    }
}