package utils;

import java.awt.*;

public class ColorUtils {
    public static Color getColorFromUsername(String username) {
        int hash = username.hashCode();
        float hue = (hash & 0xFFFFFFF) % 360 / 360f;
        return Color.getHSBColor(hue, 0.7f, 0.8f);
    }
}
