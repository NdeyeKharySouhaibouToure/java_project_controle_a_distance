import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServeurControleDistant {
    private ServerSocket socketServeur;
    private boolean enExecution;
    private ExecutorService poolThreads;
    private final int port;
    private InterfaceServeurControleDistant gui;
    private java.util.concurrent.ConcurrentHashMap<String, GestionnaireClient> clients;

    // Constructeur
    public ServeurControleDistant(int port, InterfaceServeurControleDistant gui) {
        this.port = port;
        this.gui = gui;
        this.poolThreads = Executors.newCachedThreadPool();
        this.clients = new java.util.concurrent.ConcurrentHashMap<>();
    }

    // Démarrer le serveur
    public void demarrer() {
        try {
            socketServeur = new ServerSocket(port);
            enExecution = true;
            gui.ajouterMessageLog("Serveur démarré sur le port " + port);

            while (enExecution) {
                try {
                    Socket socketClient = socketServeur.accept();
                    String adresseIP = socketClient.getInetAddress().getHostAddress();
                    String heureConnexion = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

                    gui.ajouterMessageLog("Nouveau client connecté : " + adresseIP);
                    gui.ajouterClient(adresseIP, heureConnexion);

                    GestionnaireClient gestionnaire = new GestionnaireClient(socketClient, this);
                    clients.put(adresseIP, gestionnaire);
                    poolThreads.execute(gestionnaire);
                } catch (IOException e) {
                    if (enExecution) {
                        gui.ajouterMessageLog("Erreur lors de l'acceptation d'une connexion : " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            gui.ajouterMessageLog("Impossible de démarrer le serveur : " + e.getMessage());
        }
    }

    // Arrêter le serveur
    public void arreter() {
        enExecution = false;
        for (GestionnaireClient gestionnaire : clients.values()) {
            gestionnaire.deconnecter();
        }
        poolThreads.shutdown();
        try {
            if (socketServeur != null && !socketServeur.isClosed()) {
                socketServeur.close();
            }
        } catch (IOException e) {
            gui.ajouterMessageLog("Erreur lors de la fermeture du serveur : " + e.getMessage());
        }
    }

    // Supprimer un client
    public void supprimerClient(String adresseIP) {
        clients.remove(adresseIP);
        gui.supprimerClient(adresseIP);
    }

    // Classe interne pour gérer les clients
    private class GestionnaireClient implements Runnable {
        private Socket socketClient;
        private BufferedReader entree;
        private PrintWriter sortie;
        private ServeurControleDistant serveur;
        private String adresseIP;

        public GestionnaireClient(Socket socket, ServeurControleDistant serveur) {
            this.socketClient = socket;
            this.serveur = serveur;
            this.adresseIP = socket.getInetAddress().getHostAddress();
        }

        @Override
        public void run() {
            try {
                entree = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                sortie = new PrintWriter(socketClient.getOutputStream(), true);

                String commande;
                while ((commande = entree.readLine()) != null && enExecution) {
                    if (commande.equalsIgnoreCase("exit")) {
                        break;
                    }
                    gui.ajouterMessageLog("Commande reçue de " + adresseIP + " : " + commande);
                    gui.incrementerCompteurCommande(adresseIP);
                    String resultat = executerCommande(commande);
                    sortie.println(resultat);
                }
            } catch (IOException e) {
                gui.ajouterMessageLog("Erreur avec le client " + adresseIP + " : " + e.getMessage());
            } finally {
                deconnecter();
            }
        }

        public void deconnecter() {
            try {
                if (socketClient != null && !socketClient.isClosed()) {
                    socketClient.close();
                    gui.ajouterMessageLog("Client déconnecté : " + adresseIP);
                    serveur.supprimerClient(adresseIP);
                }
            } catch (IOException e) {
                gui.ajouterMessageLog("Erreur lors de la fermeture du socket client : " + e.getMessage());
            }
        }

        private String executerCommande(String commande) {
            StringBuilder sortieCommande = new StringBuilder();
            try {
                Process processus;
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    // Utilisation de ProcessBuilder pour remplacer Runtime.exec
                    ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", commande);
                    pb.redirectErrorStream(true); // Combiner les erreurs avec la sortie standard
                    processus = pb.start();
                } else {
                    processus = new ProcessBuilder("/bin/sh", "-c", commande).start();
                }

                // Lire la sortie standard et les erreurs
                BufferedReader entreeStd = new BufferedReader(new InputStreamReader(processus.getInputStream()));
                String ligne;
                while ((ligne = entreeStd.readLine()) != null) {
                    sortieCommande.append(ligne).append("\n");
                }

                // Attendre la fin du processus
                int codeSortie = processus.waitFor();
                if (codeSortie != 0) {
                    sortieCommande.append("ERREUR: Code de sortie non nul : ").append(codeSortie).append("\n");
                }
            } catch (IOException | InterruptedException e) {
                sortieCommande.append("Erreur lors de l'exécution : ").append(e.getMessage()).append("\n");
            }
            return sortieCommande.toString();
        }
    }
}