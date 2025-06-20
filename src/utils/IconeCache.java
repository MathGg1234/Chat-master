package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class IconeCache {

    private static TrayIcon trayIcon;

    public static void setupSystemTray(Frame frame, String resourcePath) {
        removeSystemTray(); // Assure qu'aucun ancien TrayIcon n'existe

        if (!SystemTray.isSupported()) {
            System.err.println("System tray non supporté");
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();
            URL iconURL = frame.getClass().getClassLoader().getResource(resourcePath);

            if (iconURL == null) {
                System.err.println("Icône non trouvée : " + resourcePath);
                return;
            }

            Image image = ImageIO.read(iconURL);

            PopupMenu popup = new PopupMenu();

            MenuItem openItem = new MenuItem("Réouvrir le chat");
            openItem.addActionListener(e -> {
                frame.setVisible(true);
                frame.setExtendedState(Frame.NORMAL);
                frame.toFront();
            });

            MenuItem exitItem = new MenuItem("Quitter");
            exitItem.addActionListener(e -> {
                tray.remove(trayIcon);
                System.exit(0);
            });

            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);

            trayIcon = new TrayIcon(image, "Chat Client", popup);
            trayIcon.setImageAutoSize(true);

            trayIcon.addActionListener(e -> {
                frame.setVisible(true);
                frame.setExtendedState(Frame.NORMAL);
                frame.toFront();
            });

            tray.add(trayIcon);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    frame.setVisible(false);
                }
            });

        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }

    public static void removeSystemTray() {
        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
            trayIcon = null;
        }
    }
}