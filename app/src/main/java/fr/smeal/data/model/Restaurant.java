package fr.smeal.data.model;

public class Restaurant {
    private String id;
    private String name;
    private String address;
    private String photoUrl;
    // Ajoute d'autres champs selon tes besoins (lat, lng, description...)

    // ⚠️ OBLIGATOIRE pour Firebase (Constructeur vide)
    public Restaurant() { }

    public Restaurant(String id, String name, String address, String photoUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.photoUrl = photoUrl;
    }

    // Getters et Setters (OBLIGATOIRES pour que Firebase puisse lire/écrire)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}