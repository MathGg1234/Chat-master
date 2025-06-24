package utils.command;

import utils.IconeCache;
import utils.MessageDisplay;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class RsCommand implements Command {
    @Override
    public boolean matches(String input) {
        return input.toLowerCase().startsWith("/rs");
    }


    @Override
    public void execute(String input, CommandContext context) {
        context.frame.dispose();
        SwingUtilities.invokeLater(() -> new client.New());
        context.inputField.setText("");
        IconeCache.removeSystemTray();
    }
}


//    public void execute(String input, CommandContext context) {
//
//        Map<String> helpMap = new LinkedHashMap<>();
//        helpMap.put("/name", "Changer de pseudo");
//        helpMap.put("/exit", "Fermer l'application");
//        helpMap.put("/clear", "Effacer l'historique du chat");
//        helpMap.put("/ip", "Afficher votre IP du réseau COMSIC");
//        helpMap.put("/hide", "Ne plus forcer la fenêtre à être au premier plan");
//        helpMap.put("/show", "Réactiver la fenêtre au premier plan");
//        helpMap.put("/rs", "Redémarre le client");
//        helpMap.put("/bye", "Éteint le serveur (si autorisé)");
//        helpMap.put("/help", "Afficher toutes les commandes");
//        String[] parts = input.split("\s+", 2);
//
//        if (parts.length == 1) {
//
//        } else {
//            String desc = helpMap.get(parts[1].toLowerCase());
//            if (desc != null) {
//
//            } else {
//
//            }
//        }
//        context.inputField.setText("");
//    }
