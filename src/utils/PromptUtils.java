package utils;

import javax.swing.*;

public class PromptUtils {
    public static String promptUsername(JFrame parent) {
        String name = JOptionPane.showInputDialog(parent, "Enter your username:", "Username", JOptionPane.PLAIN_MESSAGE);
        return (name == null || name.trim().isEmpty()) ? "Anonymous" : name.trim();
    }
}