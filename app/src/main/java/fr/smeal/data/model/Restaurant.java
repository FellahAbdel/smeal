package fr.smeal.data.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

// Cette annotation permet à Firebase d'ignorer les champs qu'il ne connait pas (sécurité)
@IgnoreExtraProperties
public class Restaurant {

    // On exclut l'ID de la sauvegarde interne de l'objet JSON car c'est la clé du document
    @Exclude
    private String id;

    private String nom;
    private String adresse;
    private String imageUrl; // Nommé imageUrl dans Firestore
    private double latitude;
    private double longitude;

    // ⚠️ OBLIGATOIRE : Le constructeur vide pour Firebase
    public Restaurant() { }

    // Constructeur complet pour nous aider à créer des objets manuellement si besoin
    public Restaurant(String nom, String adresse, String imageUrl, double latitude, double longitude) {
        this.nom = nom;
        this.adresse = adresse;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Exclude
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}