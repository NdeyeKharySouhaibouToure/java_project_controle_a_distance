import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class InterfaceClientControleDistant extends JFrame {
    private ClientControleDistant client;
    private JTextField champAdresseServeur;
    private JTextField champPortServeur;
    private JTextField champCommande;
    private JTextArea zoneResultats;
    private JButton boutonConnecter;
    private JButton boutonDeconnecter;
    private JButton boutonEnvoyer;
    private JComboBox<String> historiqueCommandes;
    private ArrayList<String> listeHistorique;

    public InterfaceClientControleDistant() {
        super("Client - Contrôle à Distance");
        listeHistorique = new ArrayList<>();
        initialiserInterface();
        configurerEvenements();
        configurerFenetre();
    }

    private void initialiserInterface() {
        JPanel panneauConnexion = new JPanel(new GridLayout(1, 5, 5, 5));
        champAdresseServeur = new JTextField("localhost");
        champPortServeur = new JTextField("8080");
        boutonConnecter = new JButton("Connecter");
        boutonDeconnecter = new JButton("Déconnecter");
        boutonDeconnecter.setEnabled(false);

        panneauConnexion.add(new JLabel("Serveur :"));
        panneauConnexion.add(champAdresseServeur);
        panneauConnexion.add(new JLabel("Port :"));
        panneauConnexion.add(champPortServeur);
        panneauConnexion.add(boutonConnecter);
        panneauConnexion.add(boutonDeconnecter);

        JPanel panneauCommande = new JPanel(new BorderLayout(5, 5));
        champCommande = new JTextField();
        boutonEnvoyer = new JButton("Envoyer");
        boutonEnvoyer.setEnabled(false);

        panneauCommande.add(new JLabel("Commande :"), BorderLayout.WEST);
        panneauCommande.add(champCommande, BorderLayout.CENTER);
        panneauCommande.add(boutonEnvoyer, BorderLayout.EAST);

        JPanel panneauHistorique = new JPanel(new BorderLayout(5, 5));
        historiqueCommandes = new JComboBox<>();
        historiqueCommandes.setEnabled(false);

        historiqueCommandes.addActionListener(e -> {
            String commandeSelectionnee = (String) historiqueCommandes.getSelectedItem();
            if (commandeSelectionnee != null && !commandeSelectionnee.isEmpty()) {
                champCommande.setText(commandeSelectionnee);
            }
        });

        panneauHistorique.add(new JLabel("Historique :"), BorderLayout.WEST);
        panneauHistorique.add(historiqueCommandes, BorderLayout.CENTER);

        zoneResultats = new JTextArea();
        zoneResultats.setEditable(false);
        JScrollPane defilementResultats = new JScrollPane(zoneResultats);

        setLayout(new BorderLayout(10, 10));
        JPanel panneauHaut = new JPanel(new BorderLayout(5, 5));
        panneauHaut.add(panneauConnexion, BorderLayout.NORTH);
        panneauHaut.add(panneauCommande, BorderLayout.CENTER);
        panneauHaut.add(panneauHistorique, BorderLayout.SOUTH);

        add(panneauHaut, BorderLayout.NORTH);
        add(defilementResultats, BorderLayout.CENTER);

        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void configurerEvenements() {
        boutonConnecter.addActionListener(e -> {
            String adresse = champAdresseServeur.getText().trim();
            int port;
            try {
                port = Integer.parseInt(champPortServeur.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Numéro de port invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            client = new ClientControleDistant(adresse, port);
            if (client.connecter()) {
                boutonConnecter.setEnabled(false);
                boutonDeconnecter.setEnabled(true);
                boutonEnvoyer.setEnabled(true);
                historiqueCommandes.setEnabled(true);
                champCommande.requestFocus();
                zoneResultats.append("Connecté au serveur à " + adresse + ":" + port + "\n");
            } else {
                zoneResultats.append("Échec de la connexion au serveur à " + adresse + ":" + port + "\n");
            }
        });

        boutonDeconnecter.addActionListener(e -> {
            if (client != null) {
                client.deconnecter();
                zoneResultats.append("Déconnecté du serveur\n");
            }
            boutonConnecter.setEnabled(true);
            boutonDeconnecter.setEnabled(false);
            boutonEnvoyer.setEnabled(false);
            historiqueCommandes.setEnabled(false);
        });

        boutonEnvoyer.addActionListener(e -> envoyerCommande());
        champCommande.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    envoyerCommande();
                }
            }
        });
    }

    private void envoyerCommande() {
        if (client == null) return;
        String commande = champCommande.getText().trim();
        if (commande.isEmpty()) return;
        if (!listeHistorique.contains(commande)) {
            listeHistorique.add(commande);
            historiqueCommandes.addItem(commande);
        }
        zoneResultats.append("> " + commande + "\n");
        new Thread(() -> {
            String reponse = client.envoyerCommande(commande);
            SwingUtilities.invokeLater(() -> {
                zoneResultats.append(reponse + "\n");
                zoneResultats.setCaretPosition(zoneResultats.getDocument().getLength());
            });
        }).start();
        champCommande.setText("");
    }

    private void configurerFenetre() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfaceClientControleDistant::new);
    }
}