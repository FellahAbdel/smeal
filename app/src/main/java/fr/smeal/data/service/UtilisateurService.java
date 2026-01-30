package fr.smeal.data.service;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import fr.smeal.data.model.Utilisateur;
import fr.smeal.data.repository.UtilisateurRepository;

public class UtilisateurService {

    private final UtilisateurRepository repository;

    public UtilisateurService() {
        // Le service initialise le repository dont il a besoin
        this.repository = new UtilisateurRepository();
    }

    // Exemple de logique métier : Création de profil
    public Task<Void> creerProfilUtilisateur(String uid, Utilisateur utilisateur) {
        // 1. Validation des données (Règle métier)
        if (utilisateur == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }
        if (uid == null || uid.isEmpty()) {
            throw new IllegalArgumentException("L'ID utilisateur est invalide");
        }

        // 2. Si tout est bon, on appelle le repository pour sauvegarder
        return repository.saveUtilisateur(uid, utilisateur);
    }

    // Exemple de logique métier : Récupération de profil
    public Task<Utilisateur> getProfilUtilisateur(String uid) {
        // On récupère le Snapshot du repository et on le transforme en Objet Utilisateur
        // pour que la Vue (Activity) ne manipule que des objets propres, pas des Snapshots.

        return repository.getUtilisateur(uid).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return document.toObject(Utilisateur.class);
                }
            }
            return null; // Ou lancer une exception selon ta gestion d'erreur
        });
    }

    // Autres méthodes métier (ex: changerAdresse, supprimerCompte...)
}