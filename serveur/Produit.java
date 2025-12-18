import java.rmi.Remote;

public interface Produit extends Remote{

    // Client
    public Produit getDonnees();
    public void encherir();

    // Serveur
    public void annoncerFin();
}
