// Fichier: client/New.java
package client;

import com.formdev.flatlaf.FlatDarkLaf;
import utils.*;

import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

public class New extends JFrame {
    private JTextPane chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter out;
    private SecretKey sharedKey;
    private Color customColor = null;
    private final AtomicReference<String> usernameRef = new AtomicReference<>();
    private final String clientIp = NetworkUtils.getEthernetIp("Hyper-V Virtual Ethernet Adapter #2");

    public New() {
        LookAndFeelManager.applyDarkTheme();
        this.usernameRef.set(PromptUtils.promptUsername(this));
        UIInitializer.setupFrame(this, usernameRef.get());

        chatArea = UIInitializer.createChatArea();
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        sendButton = new JButton("Send");
        UIInitializer.setupInputComponents(inputField, sendButton, this::sendMessage);
        add(UIInitializer.createInputPanel(inputField, sendButton), BorderLayout.SOUTH);

        JPanel featurePanel = FeaturePanelBuilder.buildFeaturePanel(this, usernameRef , chatArea, inputField, () -> customColor, color -> customColor = color);
        add(featurePanel, BorderLayout.NORTH);

        UIInitializer.finalizeFrame(this, inputField);

        loadKey();
        connectToServer();
    }

    private void loadKey() {
        sharedKey = EncryptionUtils.loadKeyFromString("1234567890ABCDEF");
    }

    private void connectToServer() {
        ServerConnector.connect(chatArea, inputField, String.valueOf(usernameRef), sharedKey, ip -> clientIp.equals(ip), this::appendMessage, outRef -> out = outRef);
    }

    private void sendMessage() {
        MessageHandler.handleSend(inputField, out, sharedKey, String.valueOf(usernameRef), customColor, clientIp, chatArea, this);
    }

    private void appendMessage(LocalDateTime timestamp, String sender, String content) {
        MessageDisplay.append(chatArea, timestamp, sender, content, clientIp);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(New::new);
    }
}