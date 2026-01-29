package fr.smeal.data.model;

public class Menu {
    private String id;
    private String idRestaurant;
    private String nom;
    private String entree;
    private String plat;
    private String dessert;
    private String sauce;
    private double prix;
    private String imageUrl;

    public Menu() { }

    public Menu(String id, String idRestaurant, String nom, String entree, String plat, String dessert, String sauce, double prix, String imageUrl) {
        this.id = id;
        this.idRestaurant = idRestaurant;
        this.nom = nom;
        this.entree = entree;
        this.plat = plat;
        this.dessert = dessert;
        this.sauce = sauce;
        this.prix = prix;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdRestaurant() { return idRestaurant; }
    public void setIdRestaurant(String idRestaurant) { this.idRestaurant = idRestaurant; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEntree() { return entree; }
    public void setEntree(String entree) { this.entree = entree; }
    public String getPlat() { return plat; }
    public void setPlat(String plat) { this.plat = plat; }
    public String getDessert() { return dessert; }
    public void setDessert(String dessert) { this.dessert = dessert; }
    public String getSauce() { return sauce; }
    public void setSauce(String sauce) { this.sauce = sauce; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
