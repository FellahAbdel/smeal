package fr.smeal.data.model;

public class Reservation {
    private String id;
    private String idRestaurant;
    private String idUtilisateur;
    private String date;
    private int nbPersonnes;

    public Reservation() { }

    public Reservation(String id, String idRestaurant, String idUtilisateur, String date, int nbPersonnes) {
        this.id = id;
        this.idRestaurant = idRestaurant;
        this.idUtilisateur = idUtilisateur;
        this.date = date;
        this.nbPersonnes = nbPersonnes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(String idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getIdRestaurant() { return idRestaurant; }
    public void setIdRestaurant(String idRestaurant) { this.idRestaurant = idRestaurant; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getNbPersonnes() { return nbPersonnes; }
    public void setNbPersonnes(int nbPersonnes) { this.nbPersonnes = nbPersonnes; }
}
