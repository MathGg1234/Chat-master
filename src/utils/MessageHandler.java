package utils;

import utils.command.*;

import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class MessageHandler {

    private static final List<Command> commands = Arrays.asList(
            new AlwaysOnTopCommand(),
            new ByeCommand(),
            new ClearCommand(),
            new ExitCommand(),
            new HelpCommand(),
            new IpCommand(),
            new RenameCommand(),
            new RsCommand()




            // Ajouter ici les autres commandes (RenameCommand, ByeCommand, etc.)
    );

    public static void handleSend(JTextComponent inputField, PrintWriter out, SecretKey sharedKey,
                                  String username, Color customColor, String clientIp,
                                  JTextPane chatArea, JFrame parentFrame) {

        String text = inputField.getText().trim();
        if (!text.isEmpty() && out != null) {
            CommandContext context = new CommandContext(inputField, chatArea, parentFrame, out, sharedKey, username, customColor, clientIp);

            for (Command command : commands) {
                if (command.matches(text)) {
                    command.execute(text, context);
                    return;
                }
            }

            try {
                String colorCode = (customColor != null)
                        ? "@@#" + Integer.toHexString(customColor.getRGB()).substring(2)
                        : "";
                String userWithColor = username + colorCode;

                String encryptedUsername = EncryptionUtils.encrypt(userWithColor, sharedKey);
                String encryptedText = EncryptionUtils.encrypt(text, sharedKey);

                out.println(encryptedUsername + "::" + encryptedText);
                inputField.setText("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parentFrame, "Erreur d'envoi", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}