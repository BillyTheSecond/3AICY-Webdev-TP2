package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
    private JLabel zoneImage;
    private JTextField champEnchere;
    private Produit produit;
    private ClientCallbackImpl callback;

    // Ajout des labels fixes
    private JLabel labelPrixActuel;
    private JLabel labelDernierEncherisseur;

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

        // Panel info produit (prix et dernier enchérisseur)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        JPanel infoLabelsPanel = new JPanel();
        infoLabelsPanel.setLayout(new BoxLayout(infoLabelsPanel, BoxLayout.Y_AXIS));
        labelPrixActuel = new JLabel("Prix actuel: ");
        labelDernierEncherisseur = new JLabel("Dernier enchérisseur: ");
        infoLabelsPanel.add(labelPrixActuel);
        infoLabelsPanel.add(labelDernierEncherisseur);
        infoPanel.add(infoLabelsPanel, BorderLayout.NORTH);

        // Zone info produit (historique/messages)
        zoneInfoProduit = new JTextArea();
        zoneInfoProduit.setEditable(false);
        infoPanel.add(new JScrollPane(zoneInfoProduit), BorderLayout.CENTER);

        add(infoPanel, BorderLayout.CENTER);

        // Zone image
        zoneImage = new JLabel();
        add(zoneImage, BorderLayout.EAST);

        // Zone boutton enchere
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Votre enchère"));
        champEnchere = new JTextField(10);
        bottomPanel.add(champEnchere);
        JButton btnEnchere = new JButton("Enchérir");
        bottomPanel.add(btnEnchere);
        add(bottomPanel, BorderLayout.SOUTH);

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

            try {
                zoneImage.setIcon(recupererImage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion: " + e.getMessage());
        }
    }

    // Met à jour les labels fixes et efface la zone info
    private void updateInfoProduit() {
        try {
            labelPrixActuel.setText("Prix actuel: " + produit.getPrix() + "€");
            labelDernierEncherisseur.setText("Dernier enchérisseur: " + produit.getNomAcheteur());
            // Optionnel: effacer la zone info ou non
            // zoneInfoProduit.setText("");
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

    // Source ce cette fonction: https://www.baeldung.com/java-resize-image
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resizedImage;
    }

    private ImageIcon recupererImage() throws RemoteException, IOException{
        byte[] imageBytes = produit.getImage();
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
        BufferedImage resizedImg = resizeImage(img, 200, 200);
        ImageIcon icon = new ImageIcon(resizedImg);
        return icon;
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
                // Met à jour les labels fixes
                parent.labelPrixActuel.setText("Prix actuel: " + nouveauPrix + "€");
                parent.labelDernierEncherisseur.setText("Dernier enchérisseur: " + pseudoEncherisseur);
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

