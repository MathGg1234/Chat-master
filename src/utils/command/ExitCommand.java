package utils.command;

public class ExitCommand implements Command {

    @Override
    public boolean matches(String input) {
        return input.equalsIgnoreCase("/exit");
    }

    @Override
    public void execute(String input, CommandContext context) {
        System.exit(0);
    }
}