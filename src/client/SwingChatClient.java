package client;

import com.formdev.flatlaf.FlatDarkLaf;
import utils.EncryptionUtils;
import utils.FrameColor;
import utils.GuiUtils;
import utils.IconeCache;

import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;

public class SwingChatClient extends JFrame {

    private JTextPane chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter out;
    private SecretKey sharedKey;
    private Color customColor = null;
    private String username;
    private final String clientIp = getEthernetExterneIp();

    public SwingChatClient() {
        initializeLookAndFeel();
        this.username = promptUsername();
        setupUI();
        loadKey();
        connectToServer();
    }

    private String promptUsername() {
        String name = JOptionPane.showInputDialog(this, "Enter your username:", "Username", JOptionPane.PLAIN_MESSAGE);
        return (name == null || name.trim().isEmpty()) ? "Anonymous" : name.trim();
    }

    private void initializeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf dark theme");
        }
    }

    private void loadKey() {
        try {
            String keyString = "1234567890ABCDEF";
            byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
            sharedKey = EncryptionUtils.loadKeyFromBytes(keyBytes);
        } catch (Exception e) {
            System.err.println("Failed to load shared key");
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        GuiUtils.setWindowIcon(this, "resources/chat.png");

        setTitle("Le Chat des PD(I) - " + username);
        setSize(350, 400);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - getWidth();
        int y = screenSize.height - getHeight();
        setLocation(x + 8, y - 31);

        chatArea = new JTextPane();
        chatArea.setOpaque(false);
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Lucida Console", Font.PLAIN, 18));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        inputField.setFont(new Font("Lucida Console", Font.PLAIN, 16));
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        JPanel featurePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton colorButton = new JButton("\uD83C\uDFA8 Change Color");
        colorButton.addActionListener(e -> new FrameColor(color -> {
            customColor = color;
            appendMessage(LocalDateTime.now(), "System", "Pseudo color updated.");
        }));
        featurePanel.add(colorButton);
        add(featurePanel, BorderLayout.NORTH);

        setAlwaysOnTop(true);
        setVisible(true);
        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());

        if (SystemTray.isSupported()) {
            IconeCache.setupSystemTray(this, "resources/chat.png");
        }
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("172.16.64.193", 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        String[] parts = line.split("::", 2);
                        if (parts.length == 2) {
                            String sender = EncryptionUtils.decrypt(parts[0], sharedKey);
                            String decrypted = EncryptionUtils.decrypt(parts[1], sharedKey);
                            SwingUtilities.invokeLater(() -> appendMessage(LocalDateTime.now(), sender, decrypted));
                            chatArea.setCaretPosition(chatArea.getDocument().getLength());
                        }
                    }
                } catch (Exception e) {
                    appendMessage(LocalDateTime.now(), "System", "Connection lost or failed to read.");
                }
            }).start();

        } catch (IOException e) {
            appendMessage(LocalDateTime.now(), "System", "Unable to connect to server.");
        }
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty() && out != null) {
            try {
                if (text.equalsIgnoreCase("/rename")) {
                    username = promptUsername();
                } else if (text.equalsIgnoreCase("/colorname")) {
                    new FrameColor(color -> {
                        customColor = color;
                        appendMessage(LocalDateTime.now(), "System", "Pseudo color updated.");
                    });
                    inputField.setText("");
                    return;
                } else if (text.equalsIgnoreCase("/clear")) {
                    chatArea.setText("");
                    return;
                } else if (text.equalsIgnoreCase("/ip")) {
                    appendMessage(LocalDateTime.now(), "System", "Ton IP est : " + clientIp);
                    return;
                } else if (text.equalsIgnoreCase("/hack")) {
                    sendSilentMessageStressTest();
                    return;
                }

                String colorCode = (customColor != null) ? "@@#" + Integer.toHexString(customColor.getRGB()).substring(2) : "";
                String userWithColor = username + colorCode;
                String encryptedUsername = EncryptionUtils.encrypt(userWithColor, sharedKey);
                String encryptedText = EncryptionUtils.encrypt(text, sharedKey);
                out.println(encryptedUsername + "::" + encryptedText);
                inputField.setText("");

            } catch (Exception e) {
                showError("Erreur d'envoi");
            }
        }
    }

    private void appendMessage(LocalDateTime timestamp, String sender, String content) {
        StyledDocument doc = chatArea.getStyledDocument();
        try {
            String time = String.format("[%02d:%02d] ", timestamp.getHour(), timestamp.getMinute());
            doc.insertString(doc.getLength(), time, null);

            boolean rainbow = sender.equals("Cordon") || clientIp.equals("172.16.64.182") || sender.contains("(I'm Gay)");

            if (rainbow) {
                Color[] rainbowColors = {
                        Color.RED, new Color(255, 165, 0), Color.YELLOW,
                        Color.GREEN, Color.BLUE, new Color(75, 0, 130), new Color(238, 130, 238)
                };
                for (int i = 0; i < sender.length(); i++) {
                    SimpleAttributeSet attr = new SimpleAttributeSet();
                    StyleConstants.setForeground(attr, rainbowColors[i % rainbowColors.length]);
                    doc.insertString(doc.getLength(), String.valueOf(sender.charAt(i)), attr);
                }
            } else {
                Color pseudoColor = null;
                if (sender.contains("@@#")) {
                    int index = sender.indexOf("@@#");
                    try {
                        pseudoColor = Color.decode("#" + sender.substring(index + 3));
                    } catch (Exception ignored) {}
                    sender = sender.substring(0, index);
                }

                SimpleAttributeSet attr = new SimpleAttributeSet();
                StyleConstants.setForeground(attr, (pseudoColor != null) ? pseudoColor : getColorForUsername(sender));
                doc.insertString(doc.getLength(), sender, attr);
            }

            doc.insertString(doc.getLength(), ": " + content + "\n", null);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private Color getColorForUsername(String username) {
        if (username.equals(this.username) && customColor != null) return customColor;
        int hash = username.hashCode();
        float hue = (hash & 0xFFFFFFF) % 360 / 360f;
        return Color.getHSBColor(hue, 0.7f, 0.8f);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private static String getEthernetExterneIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.getDisplayName().equalsIgnoreCase("Hyper-V Virtual Ethernet Adapter #2")) {
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown";
    }


    public static void sendSilentMessageStressTest() {
        try {
            Socket socket = new Socket("172.16.64.193", 12345);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String keyString = "1234567890ABCDEF";
            SecretKey key = EncryptionUtils.loadKeyFromBytes(keyString.getBytes(StandardCharsets.UTF_8));

            String complexMessage = "START###" + "A".repeat(10000) + "\uD83D\uDE80\u0000\uFFFF\uDBFF\uDFFF" +
                    "特殊字符éèêâäç&<>\"\\n\\t\\b\\r" + "END###";

            String encryptedUsername = EncryptionUtils.encrypt("BotStress@@#FF0000", key);
            String encryptedText = EncryptionUtils.encrypt(complexMessage, key);

            writer.println(encryptedUsername + "::" + encryptedText);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingChatClient::new);
    }
}
