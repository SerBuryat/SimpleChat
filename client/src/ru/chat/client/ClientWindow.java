package ru.chat.client;

import ru.chat.network.TCPConnection;
import ru.chat.network.TCPConnectionObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionObserver { // old version of client App (using Swing)
    private static final String IP_ADDRESS = "192.168.0.102";
    private static final int PORT = 8189;

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField("Artem");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);

        add(log, BorderLayout.CENTER);
        log.setEditable(false);
        log.setLineWrap(true);

        add(fieldNickName,BorderLayout.NORTH);

        add(fieldInput,BorderLayout.SOUTH);
        fieldInput.addActionListener(this);

        setVisible(true);

        try {
            connection = new TCPConnection(this,IP_ADDRESS,PORT);
        } catch (IOException e) {
            printMSG("Connection error" + e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientWindow());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if(msg.isEmpty()) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickName.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMSG("Connection ready!");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMSG(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMSG("Connection close.");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMSG("Connection error" + e);
    }

    private synchronized void printMSG(String msg) {
        SwingUtilities.invokeLater(() -> {
            log.append(msg + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }
}
