package utils.command;

import utils.IconeCache;

import javax.swing.*;

public class RsCommand implements Command {
    @Override
    public boolean matches(String input) {
        return input.equalsIgnoreCase("/rs");
    }


    @Override
    public void execute(String input, CommandContext context) {
        context.frame.dispose();
        SwingUtilities.invokeLater(() -> new client.New());
        context.inputField.setText("");
        IconeCache.removeSystemTray();
    }
}