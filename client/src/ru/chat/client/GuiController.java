package ru.chat.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import ru.chat.network.TCPConnection;
import ru.chat.network.TCPConnectionObserver;

public class GuiController implements TCPConnectionObserver {

    private static final String IP_ADDRESS = "192.168.0.102";
    private static final int PORT = 8189;
    private TCPConnection connection;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button buttonConfirmNickname;

    @FXML
    private TextArea textAreaChatArea;

    @FXML
    private TextField textFieldNickName;

    @FXML
    private Button buttonUserSendMSG;

    @FXML
    private TextArea textAreaUserMSG;

    @FXML
    void initialize() {
        startConnection();

        textAreaUserMSG.setOnKeyPressed(key -> {
            String msg = textAreaUserMSG.getText();
            if (key.getCode() == KeyCode.ENTER) {
                if(!msg.isEmpty()) { // ignore send empty string
                    textAreaUserMSG.setText(null);
                    connection.sendString(textFieldNickName.getText() + ": " + msg);
                    key.consume(); // return cursor back in  textAreaUserMSG
                }
            }

        });

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

    private synchronized void printMSG(String str) {
        Platform.runLater(() -> {
            textAreaChatArea.setText(textAreaChatArea.getText() + "\n" + str); // receive string and create new line
            textAreaChatArea.selectPositionCaret(textAreaChatArea.getLength()); // TextARea AUTOscroll down
            textAreaChatArea.deselect();// remove text highlighting
        });
    }

    private void startConnection() {
        try {
            connection = new TCPConnection(this,IP_ADDRESS,PORT);
        } catch (IOException e) {
            printMSG("Connection error" + e);
        }
    }
}

