package fr.smeal.data.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Restaurant {

    @Exclude
    private String id;

    private String nom;
    private String adresse;
    private String imageUrl;
    private double latitude;
    private double longitude;
    private String cuisineType; // Nouveau champ
    private double rating;      // Note moyenne

    public Restaurant() { }

    public Restaurant(String nom, String adresse, String imageUrl, double latitude, double longitude, String cuisineType, double rating) {
        this.nom = nom;
        this.adresse = adresse;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cuisineType = cuisineType;
        this.rating = rating;
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

    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}