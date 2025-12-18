package serveur;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Produit extends Remote, Serializable {

    // Client - récupérer les données du produit
    public String getNom() throws RemoteException;
    public int getPrix() throws RemoteException;
    public String getNomAcheteur() throws RemoteException;
    public byte[] getImage() throws RemoteException;
    
    // Client - s'enregistrer pour recevoir les notifications
    public void enregistrerClient(ClientCallback client) throws RemoteException;
    
    // Client - placer une enchère
    public void encherir(String pseudoAcheteur, int nouveauPrix) throws RemoteException;

    // Serveur - annoncer la fin
    public void annoncerFin() throws RemoteException;
}
