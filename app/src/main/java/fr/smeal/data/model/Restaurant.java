package fr.smeal.data.model;

import com.google.firebase.firestore.DocumentId;

public class Restaurant {
    @DocumentId
    private String id;
    private String nom;
    private String adresse;
    private String imageUrl;
    // Ajoute d'autres champs selon tes besoins (lat, lng, description...)

    // ⚠️ OBLIGATOIRE pour Firebase (Constructeur vide)
    public Restaurant() { }

    public Restaurant(String id, String nom, String adresse, String imageUrl) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.imageUrl = imageUrl;
    }

    // Getters et Setters (OBLIGATOIRES pour que Firebase puisse lire/écrire)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}