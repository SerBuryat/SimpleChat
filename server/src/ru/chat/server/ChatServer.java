package ru.chat.server;

import ru.chat.network.TCPConnection;
import ru.chat.network.TCPConnectionObserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionObserver {


    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server is running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new TCPConnection(serverSocket.accept(), this);
                } catch (IOException e) {
                    System.out.println("TCPConnection error: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client connected: " + tcpConnection);
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection error: " + e);
    }

    private void sendToAllConnections(String value) {
        System.out.println(value);
        for(TCPConnection tcpConnection : connections) {
            tcpConnection.sendString(value);
        }
    }
}
