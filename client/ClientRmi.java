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
            while (pseudo.contentEquals("") || !((pseudo.charAt(0) >= 65 && pseudo.charAt(0) <= 90) || (pseudo.charAt(0) >= 97 && pseudo.charAt(0) <= 122))) {
                System.out.print("Votre pseudo est invalide, il doit commencer par une lettre\nEntrez votre pseudo :");
                pseudo = scanner.nextLine();

            }
            
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
                // rajouter println pour afficher l'erreur
            }
            
            scanner.close();
            System.out.println("Déconnexion.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

