package utils;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ServerConnector {

    public static void connect(JTextPane chatArea, JTextField inputField, String username, SecretKey sharedKey,
                               Predicate<String> ipChecker,
                               TriConsumer<LocalDateTime, String, String> appendMessage,
                               Consumer<PrintWriter> outRefSetter) {
        try {
            Socket socket = new Socket("172.16.64.193", 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            outRefSetter.accept(out);

            new Thread(() -> {
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        String[] parts = line.split("::", 2);
                        if (parts.length == 2) {
                            String sender = EncryptionUtils.decrypt(parts[0], sharedKey);
                            String decrypted = EncryptionUtils.decrypt(parts[1], sharedKey);
                            SwingUtilities.invokeLater(() -> appendMessage.accept(LocalDateTime.now(), sender, decrypted));
                            chatArea.setCaretPosition(chatArea.getDocument().getLength());
                        }
                    }
                } catch (Exception e) {
                    appendMessage.accept(LocalDateTime.now(), "System", "Connection lost or failed to read.");
                    SwingUtilities.invokeLater(() -> new client.New());
                }
            }).start();

            MessageSender.sendSilentMessage(username + " connected to server.", "System", "00FF00");

        } catch (Exception e) {
            appendMessage.accept(LocalDateTime.now(), "System", "Unable to connect to server.");
        }
    }

    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
}