
import java.util.Date;

public class ProduitImpl implements Produit {

    private String nom;
    private int prix;
    // imageicon pour image avec jFrame
    private Date date_fin;
    private String nom_vendeur;
    private String num_vendeur;
    private String nom_acheteur;

    public ProduitImpl(String nom, int prix, Date date_fin, String nom_vendeur, String num_vendeur, String nom_acheteur) {
        this.nom = nom;
        this.prix = prix;
        this.date_fin = date_fin;
        this.nom_vendeur = nom_vendeur;
        this.num_vendeur = num_vendeur;
        this.nom_acheteur = nom_acheteur;
    }

    public void encherir(int prix) {
        setPrix(prix);
    }





    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getPrix() {
        return this.prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public Date getDate_fin() {
        return this.date_fin;
    }

    public void setDate_fin(Date date_fin) {
        this.date_fin = date_fin;
    }

    public String getNom_vendeur() {
        return this.nom_vendeur;
    }

    public void setNom_vendeur(String nom_vendeur) {
        this.nom_vendeur = nom_vendeur;
    }

    public String getNum_vendeur() {
        return this.num_vendeur;
    }

    public void setNum_vendeur(String num_vendeur) {
        this.num_vendeur = num_vendeur;
    }

    public String getNom_acheteur() {
        return this.nom_acheteur;
    }

    public void setNom_acheteur(String nom_acheteur) {
        this.nom_acheteur = nom_acheteur;
    }

}
