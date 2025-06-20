package client;

import utils.EncryptionUtils;
import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;
import com.formdev.flatlaf.FlatDarkLaf;
import utils.GuiUtils;
import utils.IconeCache;



public class AncienSwingChatClient extends JFrame {

    private JTextPane chatPane;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter out;
    private SecretKey sharedKey;
    private JPanel backgroundPanel;
    private Color customUsernameColor = null;

    private String username;
    private final String clientIp = getEthernetExterneIp();

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

    public AncienSwingChatClient() {


        this.username = promptUsername();
        initializeLookAndFeel();
        setupUI();
        loadKey();
        connectToServer();

    }


    public void Client() {
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
        backgroundPanel = new JPanel(new BorderLayout());
        setContentPane(backgroundPanel);

        GuiUtils.setWindowIcon(this, "resources/chat_icon.png");

        setTitle("Le Chat des PD(I) - " + username);
        setSize(350, 400);

        // Calcul position bas droite
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - getWidth();
        int y = screenSize.height - getHeight();
        setLocation(x+8, y-31); // positionne en bas à droite

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // ne ferme pas la fenêtre tout de suite

        chatPane = new JTextPane();
        chatPane.setOpaque(false);
        chatPane.setEditable(false);
        chatPane.setFont(new Font("Lucida Console", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(chatPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        inputField.setFont(new Font("Lucida Console", Font.PLAIN, 16));
        inputField.setOpaque(false);

        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(false);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        backgroundPanel.add(inputPanel, BorderLayout.SOUTH);
        setAlwaysOnTop(true);
        setVisible(true);
        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());

        // Ajout au System Tray
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
                            SwingUtilities.invokeLater(() -> appendMessage(sender, decrypted));
                        }
                    }
                } catch (Exception e) {
                    appendSystemMessage("Connection lost or failed to read.");
                }
            }).start();

        } catch (IOException e) {
            appendSystemMessage("Unable to connect to server.");
        }
    }

    private void appendSystemMessage(String message) {
        try {
            StyledDocument doc = chatPane.getStyledDocument();
            doc.insertString(doc.getLength(), message + "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void appendMessage(String sender, String content) {
        StyledDocument doc = chatPane.getStyledDocument();
        try {
            String time = "[" + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "] ";
            doc.insertString(doc.getLength(), time, null); // Optionnellement, ajoute un style ici

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
                Color customColor = null;
                if (sender.contains("@@#")) {
                    int index = sender.indexOf("@@#");
                    try {
                        customColor = Color.decode("#" + sender.substring(index + 3));
                    } catch (Exception ignored) {}
                    sender = sender.substring(0, index);
                }

                SimpleAttributeSet attr = new SimpleAttributeSet();
                StyleConstants.setForeground(attr, (customColor != null) ? customColor : getColorForUsername(sender));
                doc.insertString(doc.getLength(), sender, attr);
            }

            doc.insertString(doc.getLength(), ": " + content + "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    public void sendCustomMessage(String message) {
        if (out == null) {
            connectToServer();
            // On attend une courte durée pour être sûr que la connexion s'établit
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }

        if (out != null) {
            try {

                String userWithColor = "System";
                String encryptedUsername = EncryptionUtils.encrypt(userWithColor, sharedKey);
                String encryptedText = EncryptionUtils.encrypt(message, sharedKey);
                out.println(encryptedUsername + "::" + encryptedText);
            } catch (Exception e) {
                showError("Erreur d'envoi automatique");
            }
        } else {
            showError("Connexion au serveur impossible pour l'envoi automatique");
        }
    }




    private void sendMessage() {

        String text = inputField.getText().trim();

        if (!text.isEmpty() && out != null) {
            try {
                if (text.startsWith("/")) {
                    String[] parts = text.substring(1).split("\s+", 2);
                    String command = parts[0].toLowerCase();
                    String arguments = parts.length > 1 ? parts[1] : "";

                    switch (command) {
                        case "ip":
                            String ip = getEthernetExterneIp();
                            appendSystemMessage("Ton IP est : " + ip);
                            break;
                        case "hack":
                            sendSilentMessageStressTest();
                            break;

                        case "rename":
                            username = promptUsername();
                            break;
                        case "colorname":
                            Color newColor = parseColor(arguments);
                            if (newColor != null) {
                                customUsernameColor = newColor;
                                appendSystemMessage(" Couleur modifiée");
                            } else {
                                appendSystemMessage(" Couleur invalide");
                            }
                            break;
                        case "clear":
                            chatPane.setText("");
                            break;
                        case "help":
                            appendSystemMessage("Commandes disponibles : /rename, /clear, /help, /colorname + color");
                            break;
                        default:
                            appendSystemMessage("Commande inconnue : " + command);
                            break;
                    }
                } else {
                    String colorCode = customUsernameColor != null
                            ? "@@#" + Integer.toHexString(customUsernameColor.getRGB()).substring(2)
                            : "";
                    String userWithColor = username + colorCode;
                    String encryptedUsername = EncryptionUtils.encrypt(userWithColor, sharedKey);
                    String encryptedText = EncryptionUtils.encrypt(text, sharedKey);
                    out.println(encryptedUsername + "::" + encryptedText);
                }

                inputField.setText("");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur d'envoi");
            }
        }
    }

    private Color parseColor(String input) {
        try {
            if (input.startsWith("#")) return Color.decode(input);
            if (input.matches("rgb\\(\\d{1,3},\\d{1,3},\\d{1,3}\\)")) {
                String[] parts = input.substring(4, input.length() - 1).split(",");
                return new Color(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim()));
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private Color getColorForUsername(String name) {
        int hash = name.hashCode();
        float hue = (hash & 0xFFFFFFF) % 360 / 360f;
        return Color.getHSBColor(hue, 0.7f, 0.8f);
    }


    public static void sendSilentMessage(String message) {
        try {
            Socket socket = new Socket("172.16.64.193", 12345);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String keyString = "1234567890ABCDEF";
            SecretKey key = EncryptionUtils.loadKeyFromBytes(keyString.getBytes(StandardCharsets.UTF_8));

            String encryptedUsername = EncryptionUtils.encrypt("Bot@@#00FF00", key);
            String encryptedText = EncryptionUtils.encrypt(message, key);

            writer.println(encryptedUsername + "::" + encryptedText);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        SwingUtilities.invokeLater(AncienSwingChatClient::new);


    }
}
