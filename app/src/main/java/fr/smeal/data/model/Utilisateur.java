package fr.smeal.data.model;

import com.google.firebase.firestore.DocumentId;

public class Utilisateur {
    @DocumentId
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String telephone;
    private int points;

    public Utilisateur() { }

    public Utilisateur(String id, String nom, String prenom, String email, String adresse, String telephone, int points) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adresse = adresse;
        this.telephone = telephone;
        this.points = points;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
