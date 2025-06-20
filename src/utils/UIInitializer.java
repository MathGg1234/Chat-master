package utils;

import javax.swing.*;
import java.awt.*;

public class UIInitializer {
    public static void setupFrame(JFrame frame, String username) {
        GuiUtils.setWindowIcon(frame, "resources/chat.png");
        frame.setTitle("Le Chat des PD(I) - " + username);
        frame.setSize(350, 400);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - frame.getWidth();
        int y = screenSize.height - frame.getHeight();
        frame.setLocation(x + 8, y - 31);
        frame.setLayout(new BorderLayout());
    }

    public static JTextPane createChatArea() {
        JTextPane chatArea = new JTextPane();
        chatArea.setOpaque(false);
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Lucida Console", Font.PLAIN, 18));
        return chatArea;
    }

    public static void setupInputComponents(JTextField inputField, JButton sendButton, Runnable onSend) {
        inputField.setFont(new Font("Lucida Console", Font.PLAIN, 16));
        sendButton.addActionListener(e -> onSend.run());
        inputField.addActionListener(e -> onSend.run());
    }

    public static JPanel createInputPanel(JTextField inputField, JButton sendButton) {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        return inputPanel;
    }

    public static void finalizeFrame(JFrame frame, JTextField inputField) {
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());
        if (SystemTray.isSupported()) {
            IconeCache.setupSystemTray(frame, "resources/chat.png");
        }
    }
}