package utils;

import javax.swing.text.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Arrays;

public class MessageDisplay {

    public static void append(JTextPane chatArea, LocalDateTime timestamp, String sender, String content, String clientIp) {
        StyledDocument doc = chatArea.getStyledDocument();
        try {
            String time = String.format("[%02d:%02d] ", timestamp.getHour(), timestamp.getMinute());
            doc.insertString(doc.getLength(), time, null);

            boolean rainbow = sender.equals("Cordon") || clientIp.equals("172.16.64.182") || sender.contains("(I'm Gay)") || sender.equals("Anonymous");

            if (rainbow) {
                UsernameStyler.applyRainbowStyle(doc, sender);
            } else {
                Color pseudoColor = null;
                if (sender.contains("@@#")) {
                    int index = sender.indexOf("@@#");
                    try {
                        pseudoColor = Color.decode("#" + sender.substring(index + 3));
                    } catch (Exception ignored) {}
                    sender = sender.substring(0, index);
                }
                Color finalColor = (pseudoColor != null) ? pseudoColor : ColorUtils.getColorFromUsername(sender);
                UsernameStyler.applyColoredName(doc, sender, finalColor);
            }

            String[] rainbowWords = { "Cordon", "Anonymous" };
            boolean containsRainbow = Arrays.stream(rainbowWords).anyMatch(content::contains);

            doc.insertString(doc.getLength(), ": ", null);

            if (containsRainbow) {
                UsernameStyler.insertTextWithRainbowWords(doc, content, rainbowWords);
            } else {
                doc.insertString(doc.getLength(), content + "\n", null);
            }

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}