package utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class GuiUtils {

    public static void setWindowIcon(JFrame frame, String resourcePath) {
        try {
            URL iconURL = frame.getClass().getClassLoader().getResource(resourcePath);
            if (iconURL != null) {
                Image icon = ImageIO.read(iconURL);
                frame.setIconImage(icon);
            } else {
                System.err.println("Icon not found: " + resourcePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
