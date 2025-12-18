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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import serveur.ClientCallback;
import serveur.Produit;


public class ClientRmi extends JFrame{

    private JTextField champPsuedo;
    private JLabel zoneImage;
    private JTextField champEnchere;
    private Produit produit;
    private ClientCallbackImpl callback;

    // état courant
    private JLabel labelNomObjet;
    private JLabel labelPrixActuel;
    private JLabel labelDernierEncherisseur;

    // Panel fin enchère
    private JPanel panelFinEnchere;
    private JLabel labelGagnant;
    private JLabel labelNomVendeur;
    private JLabel labelTelVendeur;

    public ClientRmi() {
        setTitle("Enchères");
        setSize(600,400);
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

        // Panel info produit (nom, prix et dernier enchérisseur)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        labelNomObjet = new JLabel("Objet: ");
        labelPrixActuel = new JLabel("Prix actuel: ");
        labelDernierEncherisseur = new JLabel("Dernier enchérisseur: ");
        infoPanel.add(labelNomObjet);
        infoPanel.add(labelPrixActuel);
        infoPanel.add(labelDernierEncherisseur);

        // Zone image
        zoneImage = new JLabel();

        // Conteneur central: infos + image
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(infoPanel);
        centerPanel.add(zoneImage);
        add(centerPanel, BorderLayout.CENTER);

        // Zone bouton enchere
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Votre enchère"));
        champEnchere = new JTextField(10);
        bottomPanel.add(champEnchere);
        JButton btnEnchere = new JButton("Enchérir");
        bottomPanel.add(btnEnchere);
        add(bottomPanel, BorderLayout.SOUTH);

        // Panel fin enchère
        panelFinEnchere = new JPanel();
        panelFinEnchere.setLayout(new BoxLayout(panelFinEnchere, BoxLayout.Y_AXIS));
        labelGagnant = new JLabel("Gagnant: ");
        labelNomVendeur = new JLabel("Vendeur: ");
        labelTelVendeur = new JLabel("Téléphone vendeur: ");
        panelFinEnchere.add(labelGagnant);
        panelFinEnchere.add(labelNomVendeur);
        panelFinEnchere.add(labelTelVendeur);
        panelFinEnchere.setVisible(false);
        add(panelFinEnchere, BorderLayout.EAST);

        
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

    // Mise à jour les labels fixes
    private void updateInfoProduit() {
        try {
            labelNomObjet.setText("Objet: " + produit.getNom());
            labelPrixActuel.setText("Prix actuel: " + produit.getPrix() + "€");
            labelDernierEncherisseur.setText("Dernier enchérisseur: " + produit.getNomAcheteur());
            panelFinEnchere.setVisible(false);
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

    // Source de cette fonction: https://www.baeldung.com/java-resize-image
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

    // Implémentation callback pour recevoir les notifs
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
                parent.labelPrixActuel.setText("Prix actuel: " + nouveauPrix + "€");
                parent.labelDernierEncherisseur.setText("Dernier enchérisseur: " + pseudoEncherisseur);
                parent.panelFinEnchere.setVisible(false);
            });
        }

        @Override
        public void finEnchere(String gagnant, String nomVendeur, String numVendeur) throws RemoteException {
            SwingUtilities.invokeLater(() -> {
                parent.labelGagnant.setText("Gagnant: " + gagnant);
                if (pseudo.equals(gagnant)) {
                    parent.labelNomVendeur.setText("Vendeur: " + nomVendeur);
                    parent.labelTelVendeur.setText("Téléphone vendeur: " + numVendeur);
                }
                parent.panelFinEnchere.setVisible(true);
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientRmi frame = new ClientRmi();
            frame.setVisible(true);
        });
    }
}

