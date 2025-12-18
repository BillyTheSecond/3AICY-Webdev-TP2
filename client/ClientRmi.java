package client;

import serveur.ClientCallback;
import serveur.Produit;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ClientRmi {

    public static void main(String[] args) {
        try {
            // Se connecter au serveur
            Produit produit = (Produit) Naming.lookup("rmi://localhost/ProduitEnchere");
            System.out.println("Connecté au serveur d'enchères");
            
            // Demander le pseudo
            Scanner scanner = new Scanner(System.in);
            System.out.print("Entrez votre pseudo: ");
            String pseudo = scanner.nextLine();
            
            // Créer et enregistrer le callback
            ClientCallbackImpl callback = new ClientCallbackImpl(pseudo);
            produit.enregistrerClient(callback);
            
            // Afficher les infos du produit
            System.out.println("\n=== Enchère en cours ===");
            System.out.println("Produit: " + produit.getNom());
            System.out.println("Prix actuel: " + produit.getPrix() + "€");
            System.out.println("Dernier enchérisseur: " + produit.getNomAcheteur());
            
            // Boucle d'enchères
            while (!callback.isEnchereTerminee()) {
                System.out.print("\nVotre enchère (0 pour quitter): ");
                int montant = scanner.nextInt();
                
                if (montant == 0) {
                    break;
                }
                
                produit.encherir(pseudo, montant);
            }
            
            scanner.close();
            System.out.println("Déconnexion.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Implémentation du callback pour recevoir les notifications
    static class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {
        private String pseudo;
        private boolean enchereTerminee = false;
        
        protected ClientCallbackImpl(String pseudo) throws RemoteException {
            super();
            this.pseudo = pseudo;
        }
        
        @Override
        public void nouvelleEnchere(String pseudoEncherisseur, int nouveauPrix) throws RemoteException {
            System.out.println("\n>>> Nouvelle enchère de " + pseudoEncherisseur + ": " + nouveauPrix + "€");
            if (!pseudoEncherisseur.equals(pseudo)) {
                System.out.print("Votre enchère (0 pour quitter): ");
            }
        }
        
        @Override
        public void finEnchere(String gagnant, String nomVendeur, String numVendeur) throws RemoteException {
            enchereTerminee = true;
            System.out.println("\n========================================");
            System.out.println("FIN DES ENCHÈRES !");
            System.out.println("Gagnant: " + gagnant);
            
            if (gagnant.equals(pseudo)) {
                System.out.println("\nFélicitations ! Vous avez remporté l'enchère !");
                System.out.println("Coordonnées du vendeur:");
                System.out.println("  Nom: " + nomVendeur);
                System.out.println("  Téléphone: " + numVendeur);
            }
            System.out.println("========================================");
        }
        
        public boolean isEnchereTerminee() {
            return enchereTerminee;
        }
    }
}

