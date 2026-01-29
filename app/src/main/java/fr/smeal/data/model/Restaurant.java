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
    private String photoUrl; // URL de l'image (ex: une image random pour commencer)

    // ⚠️ OBLIGATOIRE : Le constructeur vide pour Firebase
    public Restaurant() { }

    // Constructeur complet pour nous aider à créer des objets manuellement si besoin
    public Restaurant(String nom, String address, String photoUrl) {
        this.nom = nom;
        this.adresse = address;
        this.photoUrl = photoUrl;
    }

    @Exclude
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}