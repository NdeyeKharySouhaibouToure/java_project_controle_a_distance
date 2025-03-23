# Projet Contrôle à Distance

## Description
Ce projet est une application Java qui permet de contrôler un ordinateur à distance.
L’application inclut une interface graphique développée avec Swing, à la fois pour le serveur et le client, et offre les fonctionnalités suivantes :
- Gestion de plusieurs clients simultanément grâce à un pool de threads.
- Exécution de commandes système.
- Interface serveur avec un tableau des clients connectés et un journal des actions.
- Interface client avec un historique des commandes pour une réutilisation facile.
- Déconnexion propre des clients et arrêt sécurisé du serveur.

## Prérequis
Pour exécuter ce projet, assurez-vous d’avoir :
- Java 8 ou une version supérieure installée (JDK requis).
- Un système d’exploitation compatible (Windows, Linux, ou macOS).
- Aucun autre logiciel ou dépendance externe n’est nécessaire.

## Compilation
1. Ouvre un terminal (ou une invite de commande) dans le dossier du projet ().
2. Compile tous les fichiers Java avec la commande suivante :
   javac *.java
   Assurez-vous qu’aucune erreur de compilation n’apparaît.

## Exécution
### 1. Lancer le serveur
- Exécute la commande suivante dans le terminal :
  java InterfaceServeurControleDistant
- Une interface graphique s’ouvre. Entre un port (ex. ) dans le champ prévu, puis clique sur Démarrer le serveur.
- Le journal affiche Serveur démarré sur le port 8080 si tout fonctionne correctement.

### 2. Lancer le client
- Ouvre un autre terminal dans le même dossier.
- Exécute la commande suivante :
  java InterfaceClientControleDistant
- Une interface graphique s’ouvre. Entre l’adresse du serveur (ex.  si le serveur est sur la même machine) et le port (ex. ), puis clique sur Connecter.
- Un message Connecté au serveur à localhost:8080 s’affiche si la connexion est réussie.

## Utilisation
1. **Démarrer le serveur** : Lance le serveur comme indiqué ci-dessus et choisis un port (ex. ).
2. **Connecter le client** : Lance le client, entre l’adresse et le port du serveur, puis connecte-toi.
3. **Envoyer des commandes** :
 - Dans l’interface client, entre une commande système (ex. ClientControleDistant.class
ClientControleDistant.java
InterfaceClientControleDistant$1.class
InterfaceClientControleDistant.class
InterfaceClientControleDistant.java
InterfaceServeurControleDistant.class
InterfaceServeurControleDistant.java
ServeurControleDistant$GestionnaireClient.class
ServeurControleDistant.class
ServeurControleDistant.java sur Windows, NaaruMacka sur Linux/macOS) dans le champ Commande.
 - Clique sur Envoyer ou appuie sur Entrée.
 - Le résultat s’affiche dans la zone de résultats, et la commande est ajoutée à l’historique.
4. **Déconnexion** :
 - Pour déconnecter un client, entre la commande  et clique sur Envoyer.
 - Le client se déconnecte, et son adresse IP disparaît du tableau des clients sur le serveur.
5. **Arrêter le serveur** :
 - Dans l’interface serveur, clique sur Arrêter le serveur.
 - Le serveur se ferme proprement, et toutes les connexions sont terminées.

## Structure des fichiers
-  : Contient la logique du serveur, y compris la gestion des connexions et l’exécution des commandes via .
-  : Interface graphique du serveur (Swing), affichant un tableau des clients connectés et un journal des actions.
-  : Gère la logique du client, comme l’envoi des commandes et la réception des résultats.
-  : Interface graphique du client (Swing), avec un champ pour les commandes, une zone de résultats, et un historique des commandes.

## Fonctionnalités principales
- **Multi-clients** : Le serveur peut gérer plusieurs clients simultanément grâce à un pool de threads.
- **Interface graphique** : Interfaces Swing pour le serveur et le client, facilitant l’interaction.
- **Historique des commandes** : Le client conserve un historique des commandes pour une réutilisation rapide.
- **Journalisation** : Le serveur affiche un journal des actions (connexions, déconnexions, commandes).
- **Compatibilité multi-plateforme** : Fonctionne sur Windows, Linux, et macOS avec des commandes adaptées.

## Auteurs
- Ndèye Khary Souhaibou TOURE
- Sokhna Maimounatou Kabyr NDIAYE
- Aicha SAGNE


- **Interface graphique** : Nous avons opté pour Swing pour sa simplicité et sa compatibilité, bien que JavaFX ait été envisagé pour une interface plus moderne.

# Controle__distance
