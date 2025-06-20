package utils;

import client.New;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FeaturePanelBuilder {

    public static JPanel buildFeaturePanel(New parent, AtomicReference<String> usernameRef, JTextPane chatArea, JTextField inputField,
                                           Supplier<Color> colorGetter, Consumer<Color> colorSetter) {

        JPanel featurePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Bouton Changement de couleur
        JButton colorButton = new JButton("🎨 Change Color");
        colorButton.addActionListener(e -> new FrameColor(color -> {
            colorSetter.accept(color);
            MessageDisplay.append(chatArea, LocalDateTime.now(), "System", "Pseudo color updated.", "local");
        }));
        featurePanel.add(colorButton);

        // Bouton Thémis
        JButton themisButton = new JButton("⚠ Thémis");
        themisButton.addActionListener(e -> {
            JFrame themisFrame = new JFrame("Demande Thémis");
            themisFrame.setSize(400, 250);
            themisFrame.setLocationRelativeTo(null);
            themisFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            themisFrame.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

            JLabel labelThemised = new JLabel(" Personnel à Thémis :");
            JTextField themisedField = new JTextField();

            JLabel labelUser = new JLabel("Qui demande :");
            JTextField userField = new JTextField(usernameRef.get());
            userField.setEditable(false);

            JLabel labelCause = new JLabel("Cause :");
            JTextField causeField = new JTextField();

            formPanel.add(labelThemised);
            formPanel.add(themisedField);
            formPanel.add(labelUser);
            formPanel.add(userField);
            formPanel.add(labelCause);
            formPanel.add(causeField);

            themisFrame.add(formPanel, BorderLayout.CENTER);

            JButton sendButton = new JButton("Envoyer");
            sendButton.addActionListener(ev -> {
                String cause = causeField.getText().trim();
                String themised = themisedField.getText().trim();
                if (!cause.isEmpty()) {
                    MessageSender.sendSilentMessage(
                            "\n\n" + cause + "\n\n---------------------",
                            "\n\n\n\n\n\n\n\n\n\n----Annonce Thémis----\n\nL'utilisateur : " + usernameRef.get() +
                                    "\n\nRéclame un Thémis à l'égard de : \n\n⚠ " + themised + " ⚠\n\nCause ",
                            "00FF00"
                    );
                    themisFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(themisFrame, "Veuillez indiquer une cause.", "Erreur", JOptionPane.WARNING_MESSAGE);
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(sendButton);
            themisFrame.add(buttonPanel, BorderLayout.SOUTH);
            themisFrame.setVisible(true);
        });
        featurePanel.add(themisButton);

        // Bouton PAUSE
        JButton pauseButton = new JButton("⚠ PAUSE");
        pauseButton.addActionListener(e -> {
            pauseButton.setEnabled(false);
            new Thread(() -> {
                for (int i = 0; i < 20; i++) {
                    MessageSender.sendSilentMessage("(" + usernameRef.get() + ")", "C'EST LA PAUSE", "FF0000");
                }
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                SwingUtilities.invokeLater(() -> pauseButton.setEnabled(true));
            }).start();
        });
        featurePanel.add(pauseButton);

        return featurePanel;
    }
}
