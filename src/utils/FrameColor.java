package utils;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class FrameColor extends JFrame {

    public FrameColor(Consumer<Color> colorCallback) {
        setTitle("Choose Your Pseudo Color");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JColorChooser colorChooser = new JColorChooser();
        colorChooser.setPreferredSize(new Dimension(800, 400));
        add(colorChooser, BorderLayout.CENTER);

        JButton applyButton = new JButton("Apply Color");
        applyButton.addActionListener(e -> {
            Color selectedColor = colorChooser.getColor();
            colorCallback.accept(selectedColor);
            dispose();
        });

        add(applyButton, BorderLayout.SOUTH);
        setAlwaysOnTop(true);
        pack(); // ajuste la taille automatiquement
        toFront(); // remonte la fenêtre au premier plan
        setVisible(true);
    }
}
