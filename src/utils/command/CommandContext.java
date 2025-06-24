package utils.command;

import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;

public class CommandContext {
    public final JTextComponent inputField;
    public final JTextPane chatArea;
    public final JFrame frame;
    public final PrintWriter out;
    public final SecretKey sharedKey;
    public final AtomicReference<String> usernameRef;
    public final Color customColor;
    public final String clientIp;

    public CommandContext(JTextComponent inputField, JTextPane chatArea, JFrame frame,
                          PrintWriter out, SecretKey sharedKey,
                          AtomicReference<String> usernameRef,
                          Color customColor, String clientIp) {
        this.inputField = inputField;
        this.chatArea = chatArea;
        this.frame = frame;
        this.out = out;
        this.sharedKey = sharedKey;
        this.usernameRef = usernameRef;
        this.customColor = customColor;
        this.clientIp = clientIp;
    }
}
