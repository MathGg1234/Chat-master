package utils;

import javax.crypto.SecretKey;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MessageSender {

    public static void sendSilentMessage(String message, String pseudo, String colorHex) {
        try {
            Socket socket = new Socket("172.16.64.193", 12345);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String keyString = "1234567890ABCDEF";
            SecretKey key = EncryptionUtils.loadKeyFromBytes(keyString.getBytes(StandardCharsets.UTF_8));
            String formattedPseudo = pseudo + "@@#" + colorHex;
            String encryptedUsername = EncryptionUtils.encrypt(formattedPseudo, key);
            String encryptedText = EncryptionUtils.encrypt(message, key);

            writer.println(encryptedUsername + "::" + encryptedText);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
