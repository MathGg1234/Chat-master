package utils;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

public class LookAndFeelManager {
    public static void applyDarkTheme() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf dark theme");
        }
    }
}