package serveur;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Calendar;
import java.util.Date;

public class ServeurRmi {
    
    public static void main(String[] args) {
        try {
            // Créer le registre RMI
            LocateRegistry.createRegistry(1099);
            System.out.println("Registre RMI créé");

            // Créer le produit
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 5); // Enchères pendant 5 minutes
            Date dateFin = cal.getTime();
            
            ProduitImpl produit = new ProduitImpl(
                "iPhone 15 Pro",
                500,
                dateFin,
                "Jean Dupont",
                "0612345678"
            );
            
            // Enregistrer le produit dans le registre
            Naming.rebind("ProduitEnchere", produit);
            System.out.println("Produit enregistré: " + produit.getNom());
            System.out.println("Prix initial: " + produit.getPrix() + "€");
            System.out.println("Serveur prêt.");
            
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}


