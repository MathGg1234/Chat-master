package utils;

import javax.swing.text.*;
import java.awt.*;

public class UsernameStyler {

    public static void applyRainbowStyle(StyledDocument doc, String sender) {
        Color[] rainbowColors = {
                Color.RED, new Color(255, 165, 0), Color.YELLOW,
                Color.GREEN, Color.BLUE, new Color(75, 0, 130), new Color(238, 130, 238)
        };
        try {
            for (int i = 0; i < sender.length(); i++) {
                SimpleAttributeSet attr = new SimpleAttributeSet();
                StyleConstants.setForeground(attr, rainbowColors[i % rainbowColors.length]);
                doc.insertString(doc.getLength(), String.valueOf(sender.charAt(i)), attr);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void applyColoredName(StyledDocument doc, String sender, Color color) {
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, color);
        try {
            doc.insertString(doc.getLength(), sender, attr);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void insertTextWithRainbowWords(StyledDocument doc, String text, String[] rainbowWords) {
        Color[] rainbowColors = {
                Color.RED, new Color(255, 165, 0), Color.YELLOW,
                Color.GREEN, Color.BLUE, new Color(75, 0, 130), new Color(238, 130, 238)
        };

        try {
            int index = 0;
            while (index < text.length()) {
                boolean matched = false;

                for (String word : rainbowWords) {
                    if (text.regionMatches(true, index, word, 0, word.length())) {
                        // Appliquer style rainbow au mot
                        for (int i = 0; i < word.length(); i++) {
                            SimpleAttributeSet attr = new SimpleAttributeSet();
                            StyleConstants.setForeground(attr, rainbowColors[i % rainbowColors.length]);
                            doc.insertString(doc.getLength(), String.valueOf(word.charAt(i)), attr);
                        }
                        index += word.length();
                        matched = true;
                        break;
                    }
                }

                if (!matched) {
                    // Texte normal
                    doc.insertString(doc.getLength(), String.valueOf(text.charAt(index)), null);
                    index++;
                }
            }

            doc.insertString(doc.getLength(), "\n", null);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }




}
