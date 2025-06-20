package utils.command;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

public class RenameCommand implements Command {
    @Override
    public boolean matches(String input) {
        return input.equalsIgnoreCase("/rename");
    }

    @Override
    public void execute(String input, CommandContext context) {
        String newName = JOptionPane.showInputDialog(context.frame, "Nouveau pseudo :", context.usernameRef.get());
        if (newName != null && !newName.trim().isEmpty()) {
            context.usernameRef.set(newName.trim());
        } else {
            context.usernameRef.set("Anonymous");
        }
        context.inputField.setText("");
    }
}
