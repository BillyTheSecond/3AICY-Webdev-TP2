package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import serveur.ClientCallback;
import serveur.Produit;


public class ClientRmi extends JFrame{

    private JTextField champPsuedo;
    private JTextArea zoneInfoProduit;
    private JTextField champEnchere;
    private Produit produit;
    private ClientCallbackImpl callback;

    public ClientRmi() {
        setTitle("Enchères");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel pseudo
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Pseudo:"));
        champPsuedo = new JTextField(10);
        topPanel.add(champPsuedo);
        JButton btnConnexion = new JButton("Se connecter");
        topPanel.add(btnConnexion);
        add(topPanel, BorderLayout.NORTH);

        // Action connexion
        btnConnexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!champPsuedo.getText().contentEquals("")) {
                connexionServeur();
                updateInfoProduit();
                }
            }   
        });

        // Zone info produit
        zoneInfoProduit = new JTextArea();
        zoneInfoProduit.setEditable(false);
        add(new JScrollPane(zoneInfoProduit), BorderLayout.CENTER);

        // Zone image
        

        // Zone boutton enchere
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Votre enchère"));
        champEnchere = new JTextField(10);
        bottomPanel.add(champEnchere);
        JButton btnEnchere = new JButton("Enchérir");
        bottomPanel.add(btnEnchere);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action encherir
        btnEnchere.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encherir();
            }   
        });

    }

    private void connexionServeur() {
        try {
            produit = (Produit) Naming.lookup("rmi://localhost/ProduitEnchere");
            String pseudo = champPsuedo.getText();
            callback = new ClientCallbackImpl(pseudo, this);
            produit.enregistrerClient(callback);
            updateInfoProduit();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion: " + e.getMessage());
        }
    }

    private void updateInfoProduit() {
        try {
            StringBuilder info = new StringBuilder();
            info.append("Produit: ").append(produit.getNom()).append("\n");
            info.append("Prix actuel: ").append(produit.getPrix()).append("€\n");
            info.append("Dernier enchérisseur: ").append(produit.getNomAcheteur()).append("\n");
            info.append("-----------------------------\n");
            zoneInfoProduit.append(info.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
  
    private void encherir() {
        try {
            String pseudo = champPsuedo.getText();
            int montant = Integer.parseInt(champEnchere.getText());
            produit.encherir(pseudo, montant);
            updateInfoProduit();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enchère: " + e.getMessage());
        }
    }

    // Implémentation du callback pour recevoir les notifications
    static class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {
        private String pseudo;
        private ClientRmi parent;

        protected ClientCallbackImpl(String pseudo, ClientRmi parent) throws RemoteException {
            super();
            this.pseudo = pseudo;
            this.parent = parent;
        }

        @Override
        public void nouvelleEnchere(String pseudoEncherisseur, int nouveauPrix) throws RemoteException {
            SwingUtilities.invokeLater(() -> {
                parent.zoneInfoProduit.append("Nouvelle enchère de " + pseudoEncherisseur + ": " + nouveauPrix + "€\n");
            });
        }

        @Override
        public void finEnchere(String gagnant, String nomVendeur, String numVendeur) throws RemoteException {
            SwingUtilities.invokeLater(() -> {
                parent.zoneInfoProduit.append("FIN DES ENCHÈRES ! Gagnant: " + gagnant + "\n");
                if (gagnant.equals(pseudo)) {
                    parent.zoneInfoProduit.append("\n" + //
                                                "Félicitations ! Vous avez remporté l'enchère !\n" + 
                                                "Coordonnées du vendeur:\n"+
                                                "  Nom: " + nomVendeur+
                                                "\n  Téléphone: " + numVendeur);
                }
            });
        }
    }

        // @Override
        // public void finEnchere(String gagnant, String nomVendeur, String numVendeur) throws RemoteException {
        //     enchereTerminee = true;
        //     System.out.println("\n========================================");
        //     System.out.println("FIN DES ENCHÈRES !");
        //     System.out.println("Gagnant: " + gagnant);
            
        //     if (gagnant.equals(pseudo)) {
        //         System.out.println("\nFélicitations ! Vous avez remporté l'enchère !");
        //         System.out.println("Coordonnées du vendeur:");
        //         System.out.println("  Nom: " + nomVendeur);
        //         System.out.println("  Téléphone: " + numVendeur);
        //     }
        //     System.out.println("========================================");
        // }
        
        // public boolean isEnchereTerminee() {
        //     return enchereTerminee;
        // }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientRmi frame = new ClientRmi();
            frame.setVisible(true);
        });
    }
}

