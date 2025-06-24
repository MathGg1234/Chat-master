# Chat-master

Une application de **chat en temps réel** construite en Java (Swing) avec un système de chiffrement des messages, une interface moderne, des commandes personnalisées et un système client-serveur.

---

##  Fonctionnalités

-  Interface Swing moderne avec thème sombre (`FlatLaf`)
-  Envoi et réception de messages en temps réel
-  Chiffrement des messages avec une clé symétrique (`AES`)
-  Couleurs personnalisées pour les pseudos (`@#HEX`)
-  Commandes personnalisées :
  - `/rename` : changer de pseudo
  - `/clear` : effacer le chat
  - `/ip` : afficher l’adresse IP locale
  - `/colorname #RRGGBB` : personnaliser la couleur du pseudo
  - `/hide` & `/show` : fenêtre toujours au-dessus
  - `/help` : afficher l’aide
-  Effets visuels spéciaux (ex: pseudo arc-en-ciel pour certains utilisateurs)
-  Compatible avec un système de kick via commande spéciale

---

##  Démarrage rapide

###  Prérequis

- Java 17 ou plus
- IntelliJ, Eclipse ou BlueJ (compatible tous IDE)
- Maven ou compilation manuelle via `javac`

###  Cloner le projet

```bash
git clone https://github.com/MathGg1234/Chat-master.git
cd Chat-master
