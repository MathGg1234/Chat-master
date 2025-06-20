package utils.command;

import utils.MessageDisplay;
import utils.MessageSender;

import java.time.LocalDateTime;

public class ByeCommand implements Command {
    @Override
    public boolean matches(String input) {
        return input.equalsIgnoreCase("/bye");
    }

    @Override
    public void execute(String input, CommandContext context) {
        if (context.clientIp.equals("172.16.64.193")) {
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
                    System.exit(0);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    MessageDisplay.append(context.chatArea, LocalDateTime.now(), "System", "Erreur lors du compte à rebours.", context.clientIp);
                }
            }).start();
        } else {
            MessageDisplay.append(context.chatArea, LocalDateTime.now(), "System", "Tu n'as pas les droits de faire cette commande, chien !", context.clientIp);
        }
        context.inputField.setText("");
    }
}