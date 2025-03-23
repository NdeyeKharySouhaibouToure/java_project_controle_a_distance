import java.io.*;
import java.net.*;

public class ClientControleDistant {
    private Socket socket;
    private PrintWriter sortie;
    private BufferedReader entree;
    private final String adresseServeur;
    private final int portServeur;

    public ClientControleDistant(String adresseServeur, int portServeur) {
        this.adresseServeur = adresseServeur;
        this.portServeur = portServeur;
    }

    public boolean connecter() {
        try {
            socket = new Socket(adresseServeur, portServeur);
            sortie = new PrintWriter(socket.getOutputStream(), true);
            entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            System.err.println("Échec de la connexion au serveur : " + e.getMessage());
            return false;
        }
    }

    public String envoyerCommande(String commande) {
        if (socket == null || socket.isClosed()) {
            return "Non connecté au serveur";
        }
        try {
            sortie.println(commande);
            StringBuilder reponse = new StringBuilder();
            long debutTemps = System.currentTimeMillis();
            long delai = 10000; // Délai de 10 secondes
            while (System.currentTimeMillis() - debutTemps < delai) {
                if (entree.ready()) {
                    String ligne;
                    while ((ligne = entree.readLine()) != null && entree.ready()) {
                        reponse.append(ligne).append("\n");
                    }
                    if (!entree.ready() && reponse.length() > 0) {
                        break;
                    }
                }
                Thread.sleep(100);
            }
            return reponse.toString();
        } catch (IOException | InterruptedException e) {
            return "Erreur lors de l'envoi : " + e.getMessage();
        }
    }

    public void deconnecter() {
        try {
            if (sortie != null) {
                sortie.println("exit");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion : " + e.getMessage());
        }
    }
}