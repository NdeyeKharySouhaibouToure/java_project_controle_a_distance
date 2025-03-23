import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;

public class InterfaceServeurControleDistant extends JFrame {
    private ServeurControleDistant serveur;
    private JTextField champPort;
    private JButton boutonDemarrer;
    private JButton boutonArreter;
    private JTable tableauClients;
    private DefaultTableModel modeleTableauClients;
    private JTextArea zoneLog;

    public InterfaceServeurControleDistant() {
        super("Serveur - Contrôle à Distance");
        initialiserInterface();
        configurerEvenements();
        configurerFenetre();
    }

    private void initialiserInterface() {
        JPanel panneauControle = new JPanel(new GridLayout(1, 4, 5, 5));
        champPort = new JTextField("8080");
        boutonDemarrer = new JButton("Démarrer le serveur");
        boutonArreter = new JButton("Arrêter le serveur");
        boutonArreter.setEnabled(false);

        panneauControle.add(new JLabel("Port :"));
        panneauControle.add(champPort);
        panneauControle.add(boutonDemarrer);
        panneauControle.add(boutonArreter);

        String[] nomsColonnes = {"Adresse IP", "Heure de connexion", "Commandes exécutées"};
        modeleTableauClients = new DefaultTableModel(nomsColonnes, 0);
        tableauClients = new JTable(modeleTableauClients);
        JScrollPane defilementClients = new JScrollPane(tableauClients);

        zoneLog = new JTextArea();
        zoneLog.setEditable(false);
        JScrollPane defilementLog = new JScrollPane(zoneLog);

        JSplitPane panneauDivise = new JSplitPane(JSplitPane.VERTICAL_SPLIT, defilementClients, defilementLog);
        panneauDivise.setResizeWeight(0.3);

        setLayout(new BorderLayout(10, 10));
        add(panneauControle, BorderLayout.NORTH);
        add(panneauDivise, BorderLayout.CENTER);

        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void configurerEvenements() {
        boutonDemarrer.addActionListener(e -> {
            int port;
            try {
                port = Integer.parseInt(champPort.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Numéro de port invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            serveur = new ServeurControleDistant(port, this);
            new Thread(() -> serveur.demarrer()).start();
            boutonDemarrer.setEnabled(false);
            boutonArreter.setEnabled(true);
            champPort.setEnabled(false);
        });

        boutonArreter.addActionListener(e -> {
            if (serveur != null) {
                serveur.arreter();
                ajouterMessageLog("Serveur arrêté");
            }
            boutonDemarrer.setEnabled(true);
            boutonArreter.setEnabled(false);
            champPort.setEnabled(true);
            while (modeleTableauClients.getRowCount() > 0) {
                modeleTableauClients.removeRow(0);
            }
        });
    }

    public void ajouterClient(String adresseIP, String heureConnexion) {
        SwingUtilities.invokeLater(() -> {
            modeleTableauClients.addRow(new Object[]{adresseIP, heureConnexion, 0});
        });
    }

    public void supprimerClient(String adresseIP) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < modeleTableauClients.getRowCount(); i++) {
                if (modeleTableauClients.getValueAt(i, 0).equals(adresseIP)) {
                    modeleTableauClients.removeRow(i);
                    break;
                }
            }
        });
    }

    public void incrementerCompteurCommande(String adresseIP) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < modeleTableauClients.getRowCount(); i++) {
                if (modeleTableauClients.getValueAt(i, 0).equals(adresseIP)) {
                    int compte = (int) modeleTableauClients.getValueAt(i, 2);
                    modeleTableauClients.setValueAt(compte + 1, i, 2);
                    break;
                }
            }
        });
    }

    public void ajouterMessageLog(String message) {
        SwingUtilities.invokeLater(() -> {
            zoneLog.append(message + "\n");
            zoneLog.setCaretPosition(zoneLog.getDocument().getLength());
        });
    }

    private void configurerFenetre() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfaceServeurControleDistant::new);
    }
}