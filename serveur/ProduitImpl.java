package serveur;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

public class ProduitImpl extends UnicastRemoteObject implements Produit {

    private String nom;
    private int prix;
    private Date date_fin;
    private String nom_vendeur;
    private String num_vendeur;
    private String nom_acheteur;

    public ProduitImpl(String nom, int prix, Date date_fin, String nom_vendeur, String num_vendeur) throws RemoteException {
        super();
        this.nom = nom;
        this.prix = prix;
        this.date_fin = date_fin;
        this.nom_vendeur = nom_vendeur;
        this.num_vendeur = num_vendeur;
        this.nom_acheteur = "Aucun";
    }

    @Override
    public synchronized void encherir(String pseudoAcheteur, int nouveauPrix) throws RemoteException {
        if (nouveauPrix > this.prix) {
            this.prix = nouveauPrix;
            this.nom_acheteur = pseudoAcheteur;
            System.out.println("Nouvelle enchère: " + pseudoAcheteur + " - " + nouveauPrix + "€");
        } else {
            System.out.println("Enchère refusée: prix trop bas");
        }
    }

    @Override
    public void annoncerFin() throws RemoteException {
        System.out.println("Fin des enchères !");
        System.out.println("Gagnant: " + nom_acheteur + " avec " + prix + "€");
    }

    @Override
    public String getNom() throws RemoteException {
        return this.nom;
    }

    @Override
    public int getPrix() throws RemoteException {
        return this.prix;
    }

    @Override
    public String getNomAcheteur() throws RemoteException {
        return this.nom_acheteur;
    }

    public Date getDate_fin() {
        return this.date_fin;
    }

    public String getNom_vendeur() {
        return this.nom_vendeur;
    }

    public String getNum_vendeur() {
        return this.num_vendeur;
    }
}
