package serveur;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ProduitImpl extends UnicastRemoteObject implements Produit {

    private String nom;
    private int prix;
    private Date date_fin;
    private String nom_vendeur;
    private String num_vendeur;
    private String nom_acheteur;
    private List<ClientCallback> clients;
    private Timer timer;
    private boolean enchereTerminee;

    public ProduitImpl(String nom, int prix, Date date_fin, String nom_vendeur, String num_vendeur) throws RemoteException {
        super();
        this.nom = nom;
        this.prix = prix;
        this.date_fin = date_fin;
        this.nom_vendeur = nom_vendeur;
        this.num_vendeur = num_vendeur;
        this.nom_acheteur = "Aucun";
        this.clients = new ArrayList<>();
        this.enchereTerminee = false;
        
        // Lancer le timer pour la fin des enchères
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    annoncerFin();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, date_fin);
    }

    @Override
    public synchronized void enregistrerClient(ClientCallback client) throws RemoteException {
        clients.add(client);
        System.out.println("Client enregistré. Total: " + clients.size());
    }

    @Override
    public synchronized void encherir(String pseudoAcheteur, int nouveauPrix) throws RemoteException {
        if (enchereTerminee) {
            System.out.println("Enchères terminées, enchère refusée");
            return;
        }
        
        if (nouveauPrix > this.prix) {
            this.prix = nouveauPrix;
            this.nom_acheteur = pseudoAcheteur;
            System.out.println("Nouvelle enchère: " + pseudoAcheteur + " - " + nouveauPrix + "€");
            
            // Notifier tous les clients
            notifierClients(pseudoAcheteur, nouveauPrix);
        } else {
            System.out.println("Enchère refusée: prix trop bas");
        }
    }

    @Override
    public synchronized void annoncerFin() throws RemoteException {
        if (enchereTerminee) return;
        
        enchereTerminee = true;
        System.out.println("Fin des enchères !");
        System.out.println("Gagnant: " + nom_acheteur + " avec " + prix + "€");
        
        // Notifier tous les clients de la fin
        List<ClientCallback> clientsASupprimer = new ArrayList<>();
        for (ClientCallback client : clients) {
            try {
                client.finEnchere(nom_acheteur, nom_vendeur, num_vendeur);
            } catch (RemoteException e) {
                System.out.println("Erreur notification client: " + e.getMessage());
                clientsASupprimer.add(client);
            }
        }
        clients.removeAll(clientsASupprimer);
        
        timer.cancel();
    }

    private void notifierClients(String pseudo, int nouveauPrix) {
        List<ClientCallback> clientsASupprimer = new ArrayList<>();
        for (ClientCallback client : clients) {
            try {
                client.nouvelleEnchere(pseudo, nouveauPrix);
            } catch (RemoteException e) {
                System.out.println("Erreur notification client: " + e.getMessage());
                clientsASupprimer.add(client);
            }
        }
        clients.removeAll(clientsASupprimer);
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
