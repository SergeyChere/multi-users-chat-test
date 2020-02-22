package com.chat.server;

import com.chat.metwork.TCPConnection;
import com.chat.metwork.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {
    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new TCPConnection(serverSocket.accept(), this);
                } catch (IOException exc) {
                    System.out.println("TCPConnection: "+exc);
                }
            }
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client connected: "+tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected: "+tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception exc) {
        System.out.println("TCPConnection exception: "+exc);
    }

    private void sendToAllConnections(String value) {
        System.out.println(value);
        final int size = connections.size();
        for (int i=0; i<size; i++) connections.get(i).sendString(value);
    }
}
