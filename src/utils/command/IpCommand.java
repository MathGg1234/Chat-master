package utils.command;

import java.time.LocalDateTime;
import utils.MessageDisplay;

public class IpCommand implements Command {

    @Override
    public boolean matches(String input) {
        return input.equalsIgnoreCase("/ip");
    }

    @Override
    public void execute(String input, CommandContext context) {
        MessageDisplay.append(context.chatArea, LocalDateTime.now(), "System", "Ton IP est : " + context.clientIp, context.clientIp);
        context.inputField.setText("");
    }
}