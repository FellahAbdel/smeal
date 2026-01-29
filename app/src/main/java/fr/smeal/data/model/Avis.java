package fr.smeal.data.model;

public class Avis {
    private String id;
    private String idRestaurant;
    private String idUtilisateur;
    private String titre;
    private String description;
    private int note;

    public Avis() { }

    public Avis(String id, String idRestaurant, String idUtilisateur, String titre, String description, int note) {
        this.id = id;
        this.idRestaurant = idRestaurant;
        this.idUtilisateur = idUtilisateur;
        this.titre = titre;
        this.description = description;
        this.note = note;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdRestaurant() { return idRestaurant; }
    public void setIdRestaurant(String idRestaurant) { this.idRestaurant = idRestaurant; }
    public String getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(String idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }
}
