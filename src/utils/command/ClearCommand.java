package utils.command;

public class ClearCommand implements Command {

    @Override
    public boolean matches(String input) {
        return input.equalsIgnoreCase("/clear");
    }

    @Override
    public void execute(String input, CommandContext context) {
        context.chatArea.setText("");
        context.inputField.setText("");
    }
}