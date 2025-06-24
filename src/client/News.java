package client;

import com.formdev.flatlaf.FlatDarkLaf;
import utils.*;

import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class News extends JFrame {

    private JTextPane chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter out;
    private SecretKey sharedKey;
    private Color customColor = null;
    private String username;
    private final String clientIp = NetworkUtils.getEthernetIp("Hyper-V Virtual Ethernet Adapter #2");

    public News() {
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

// Bouton changement de couleur
        JButton colorButton = new JButton("🎨 Change Color");
        colorButton.addActionListener(e -> new FrameColor(color -> {
            customColor = color;
            appendMessage(LocalDateTime.now(), "System", "Pseudo color updated.");
        }));
        featurePanel.add(colorButton);

// Bouton Thémis
        JButton themisButton = new JButton("⚠ Thémis");
        themisButton.addActionListener(e -> {
            // Création d'une nouvelle fenêtre
            JFrame themisFrame = new JFrame("Demande Thémis");
            themisFrame.setSize(400, 250);
            themisFrame.setLocationRelativeTo(null);
            themisFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            themisFrame.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

            JLabel labelThemised = new JLabel(" Personnel a Thémis :");
            JTextField themisedField = new JTextField();


            JLabel labelUser = new JLabel("Qui demande :");
            JTextField userField = new JTextField(username); // prérempli
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

            // Bouton Envoyer
            JButton sendButton = new JButton("Envoyer");
            sendButton.addActionListener(ev -> {
                String cause = causeField.getText().trim();
                String themised = themisedField.getText().trim();
                if (!cause.isEmpty()) {
                    for (int i = 0; i < 1; i++) {
                        MessageSender.sendSilentMessage(
                                "\n\n"+ cause + "\n\n---------------------",
                                "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n----Annonce Thémis----\n\nL'utilisateur : " + username +
                                        "\n\nRéclame un Thémis a l'égard de : \n\n⚠ " +
                                        themised + " ⚠\n\nCause ",
                                "00FF00"
                        );
                    }
                    themisFrame.dispose(); // ferme la fenêtre après envoi
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


        add(featurePanel, BorderLayout.NORTH);


        // Bouton PAUSE
        JButton pauseButton = new JButton("⚠ PAUSE");
        pauseButton.addActionListener(e -> {
            // Désactiver le bouton temporairement
            pauseButton.setEnabled(false);

            // Lancer la demande
            new Thread(() -> {
                for (int i = 0; i < 20; i++) {
                    MessageSender.sendSilentMessage(
                            "(" + username + ")",
                            "C'EST LA PAUSE",
                            "FF0000"
                    );
                }

                // Attente avant de réactiver (15 secondes ici)
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                // Réactiver le bouton (dans l'EDT/Swing Thread)
                SwingUtilities.invokeLater(() -> pauseButton.setEnabled(true));
            }).start();
        });
        featurePanel.add(pauseButton);





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
                    //System.exit(0);
                    SwingUtilities.invokeLater(New::new);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException c) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();

        } catch (IOException e) {
            appendMessage(LocalDateTime.now(), "System", "Unable to connect to server.");
        }
        MessageSender.sendSilentMessage(username + " connected to server.","System","00FF00");
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty() && out != null) {
            try {

                //Ensemble des commandes actuellement disponible :

                if (text.equalsIgnoreCase("/rename")) {
                    username = promptUsername();
                    inputField.setText("");
                } else if (text.equalsIgnoreCase("/exit")) {
                    System.exit(0);
                    inputField.setText("");
                    return;
                } else if (text.equalsIgnoreCase("/clear")) {
                    chatArea.setText("");
                    inputField.setText("");
                    return;
                } else if (text.equalsIgnoreCase("/rs")) {

                    dispose();
                    SwingUtilities.invokeLater(() -> new News());

                } else if (text.equalsIgnoreCase("/ip")) {
                    appendMessage(LocalDateTime.now(), "System", "Ton IP est : " + clientIp);
                    inputField.setText("");
                    return;
                } else if (text.equalsIgnoreCase("/bye")) {
                    if (clientIp.equals("172.16.64.193")) {
                        new Thread(() -> {
                            try {
                                MessageSender.sendSilentMessage("Le serveur s'éteint dans 3 secondes", "System", "00FF00");
                                Thread.sleep(1000);
                                MessageSender.sendSilentMessage("3", "System", "00FF00");
                                Thread.sleep(1000);
                                MessageSender.sendSilentMessage("2", "System", "00FF00");
                                Thread.sleep(1000);
                                MessageSender.sendSilentMessage("1", "System", "00FF00");
                                Thread.sleep(500);
                                // Ajoute ici System.exit(0); si tu veux éteindre
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                appendMessage(LocalDateTime.now(), "System", "Erreur lors du compte à rebours.");
                            }
                        }).start(); // pour ne pas bloquer l'interface ou le flux principal
                    } else {
                        appendMessage(LocalDateTime.now(), "System", "Tu n'as pas les droits de faire cette commande, chien !");
                    }


                    inputField.setText("");
                    System.exit(0);
                    return;
                } else if (text.equalsIgnoreCase("/hide")) {
                    setAlwaysOnTop(false);
                    inputField.setText("");
                    return;
                } else if (text.equalsIgnoreCase("/show")) {
                    setAlwaysOnTop(true);
                    inputField.setText("");
                    return;
                } else if (text.toLowerCase().startsWith("/help")) {
                    String[] parts = text.split("\s+", 2);
                    Map<String, String> helpMap = new LinkedHashMap<>();
                    helpMap.put("/rename", "Changer de pseudo");
                    helpMap.put("/exit", "Fermer l'application");
                    helpMap.put("/clear", "Effacer l'historique du chat");
                    helpMap.put("/ip", "Afficher votre IP du réseau COMSIC");
                    helpMap.put("/hide", "Ne force plus la fenêtre du chat a être au premier plan");
                    helpMap.put("/show", "Réafficher la fenêtre du chat au premier plan");
                    helpMap.put("/rs", "Redémarre le client");
                    helpMap.put("/help", "Afficher toutes les commandes");
                    helpMap.put("/help + /<commande>", "Afficher la description d'une commande précise ");


                    if (parts.length == 1) {

                        StringBuilder msg = new StringBuilder("\nCommandes disponibles :\n");

                        helpMap.forEach((k, v) -> msg.append(k).append("\n"));

                        appendMessage(LocalDateTime.now(), "System", msg.toString());

                    } else {
                        String desc = helpMap.get(parts[1].toLowerCase());
                        if (desc != null) {
                            appendMessage(LocalDateTime.now(), "System", parts[1] + " : " + desc);
                        } else {
                            appendMessage(LocalDateTime.now(), "System", "Commande inconnue : " + parts[1]);
                        }
                    }
                    inputField.setText("");
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

            boolean rainbow = sender.equals("Cordon") || clientIp.equals("172.16.64.182") || sender.contains("(I'm Gay)") || sender.equals("Anonymous");

            if (rainbow) {
                UsernameStyler.applyRainbowStyle(doc, sender);
            } else {
                Color pseudoColor = null;
                if (sender.contains("@@#")) {
                    int index = sender.indexOf("@@#");
                    try {
                        pseudoColor = Color.decode("#" + sender.substring(index + 3));
                    } catch (Exception ignored) {}
                    sender = sender.substring(0, index);
                }

                Color finalColor = (pseudoColor != null) ? pseudoColor : ColorUtils.getColorFromUsername(sender);
                UsernameStyler.applyColoredName(doc, sender, finalColor);
            }

            String[] rainbowWords = { "Cordon", "Anonymous" };
            boolean containsRainbow = Arrays.stream(rainbowWords).anyMatch(content::contains);

            doc.insertString(doc.getLength(), ": ", null);

            if (containsRainbow) {
                UsernameStyler.insertTextWithRainbowWords(doc, content, rainbowWords);
            } else {
                doc.insertString(doc.getLength(), content + "\n", null);
            }


        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void sendSilentMessageStressTest() {
        MessageSender.sendSilentMessage(
                "START###" + "A".repeat(10000) + "\uD83D\uDE80\u0000\uFFFF\uDBFF\uDFFF" +
                        "特殊字符éèêâäç&<>\"\\n\\t\\b\\r" + "END###",
                "BotStress",
                "FF0000"
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(News::new);

        //sendSilentMessageStressTest();
    }
}
