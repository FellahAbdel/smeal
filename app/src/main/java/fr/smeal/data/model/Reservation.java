package fr.smeal.data.model;

import com.google.firebase.firestore.DocumentId;

public class Reservation {
    @DocumentId
    private String id;
    private String idRestaurant;
    private String nomRestaurant;
    private String idUtilisateur;
    private String nomUtilisateur;
    private String prenomUtilisateur;
    private String date;
    private int nbPersonnes;

    public Reservation() { }

    public Reservation(String id, String idRestaurant, String nomRestaurant, String idUtilisateur, String nomUtilisateur, String prenomUtilisateur, String date, int nbPersonnes) {
        this.id = id;
        this.idRestaurant = idRestaurant;
        this.nomRestaurant = nomRestaurant;
        this.idUtilisateur = idUtilisateur;
        this.nomUtilisateur = nomUtilisateur;
        this.prenomUtilisateur = prenomUtilisateur;
        this.date = date;
        this.nbPersonnes = nbPersonnes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdRestaurant() { return idRestaurant; }
    public void setIdRestaurant(String idRestaurant) { this.idRestaurant = idRestaurant; }
    public String getNomRestaurant() { return nomRestaurant; }
    public void setNomRestaurant(String nomRestaurant) { this.nomRestaurant = nomRestaurant; }
    public String getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(String idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }
    public String getPrenomUtilisateur() { return prenomUtilisateur; }
    public void setPrenomUtilisateur(String prenomUtilisateur) { this.prenomUtilisateur = prenomUtilisateur; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getNbPersonnes() { return nbPersonnes; }
    public void setNbPersonnes(int nbPersonnes) { this.nbPersonnes = nbPersonnes; }
}
