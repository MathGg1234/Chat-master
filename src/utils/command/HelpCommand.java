package utils.command;

import utils.MessageDisplay;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class HelpCommand implements Command {
    @Override
    public boolean matches(String input) {
        return input.toLowerCase().startsWith("/help");
    }

    @Override
    public void execute(String input, CommandContext context) {

        String[] parts = input.split("\s+", 2);
        Map<String, String> helpMap = new LinkedHashMap<>();
        helpMap.put("/rename", "Changer de pseudo");
        helpMap.put("/exit", "Fermer l'application");
        helpMap.put("/clear", "Effacer l'historique du chat");
        helpMap.put("/ip", "Afficher votre IP du réseau COMSIC");
        helpMap.put("/hide", "Ne plus forcer la fenêtre à être au premier plan");
        helpMap.put("/show", "Réactiver la fenêtre au premier plan");
        helpMap.put("/rs", "Redémarre le client");
        helpMap.put("/bye", "Éteint le serveur (si autorisé)");
        helpMap.put("/help", "Afficher toutes les commandes");

        if (parts.length == 1) {
            StringBuilder msg = new StringBuilder("\nCommandes disponibles : \n");
            helpMap.forEach((k, v) -> msg.append(k).append(" \n"));
            MessageDisplay.append(context.chatArea, LocalDateTime.now(), "System", msg.toString(), context.clientIp);
        } else {
            String desc = helpMap.get(parts[1].toLowerCase());
            if (desc != null) {
                MessageDisplay.append(context.chatArea, LocalDateTime.now(), "System", parts[1] + " : " + desc, context.clientIp);
            } else {
                MessageDisplay.append(context.chatArea, LocalDateTime.now(), "System", "Commande inconnue : " + parts[1], context.clientIp);
            }
        }
        context.inputField.setText("");
    }
}