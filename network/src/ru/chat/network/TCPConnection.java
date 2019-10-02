package ru.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionObserver connectionObserver;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionObserver connectionObserver, String ipAddress, int port) throws IOException{
        this(new Socket(ipAddress,port), connectionObserver);
    }

    public TCPConnection(Socket socket, TCPConnectionObserver connectionObserver) throws IOException {
        this.connectionObserver = connectionObserver;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
                StandardCharsets.UTF_8));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectionObserver.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()) {
                        String msg = in.readLine();
                        connectionObserver.onReceiveString(TCPConnection.this,msg);
                    }
                } catch (IOException e) {
                    connectionObserver.onException(TCPConnection.this, e);
                } finally {
                    connectionObserver.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void sendString(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            connectionObserver.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            connectionObserver.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
