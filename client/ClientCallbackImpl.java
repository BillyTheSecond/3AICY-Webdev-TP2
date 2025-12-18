package client;

import serveur.ClientCallback;
import serveur.Produit;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


// Implémentation du callback pour recevoir les notifications
public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {
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
        System.out.println("\n=========================================");
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
