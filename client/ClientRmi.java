package client;

import serveur.Produit;

import java.rmi.Naming;
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
            
            // Afficher les infos du produit
            System.out.println("\n=== Enchère en cours ===");
            System.out.println("Produit: " + produit.getNom());
            System.out.println("Prix actuel: " + produit.getPrix() + "€");
            System.out.println("Dernier enchérisseur: " + produit.getNomAcheteur());
            
            // Boucle d'enchères
            while (true) {
                System.out.print("\nVotre enchère (0 pour quitter): ");
                int montant = scanner.nextInt();
                
                if (montant == 0) {
                    break;
                }
                
                produit.encherir(pseudo, montant);
                
                // Afficher le nouveau prix
                System.out.println("Prix actuel: " + produit.getPrix() + "€");
                System.out.println("Dernier enchérisseur: " + produit.getNomAcheteur());
            }
            
            scanner.close();
            System.out.println("Déconnexion.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
