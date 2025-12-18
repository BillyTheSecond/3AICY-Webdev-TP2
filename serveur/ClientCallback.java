package serveur;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    
    // Notifier le client d'une nouvelle enchère
    void nouvelleEnchere(String pseudo, int nouveauPrix) throws RemoteException;
    
    // Notifier la fin des enchères avec les coordonnées du vendeur
    void finEnchere(String gagnant, String nomVendeur, String numVendeur) throws RemoteException;
}
