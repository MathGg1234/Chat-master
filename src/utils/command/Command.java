package utils.command;

import javax.swing.*;
import javax.crypto.SecretKey;
import java.awt.*;
import java.io.PrintWriter;

public interface Command {
    boolean matches(String input);
    void execute(String input, CommandContext context);
}