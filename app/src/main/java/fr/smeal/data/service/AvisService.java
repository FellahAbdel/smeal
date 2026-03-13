package fr.smeal.data.service;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.smeal.data.model.Avis;
import fr.smeal.data.model.Utilisateur;
import fr.smeal.data.repository.AvisRepository;
import fr.smeal.data.repository.UtilisateurRepository;

public class AvisService {

    private final AvisRepository repository;

    public AvisService() {
        this.repository = new AvisRepository();
    }

    public Task<Void> creerAvisUtilisateur(String uid, Avis avis) {
        if (avis == null) {
            throw new IllegalArgumentException("L'avis ne peut pas être null");
        }
        if (uid == null || uid.isEmpty()) {
            throw new IllegalArgumentException("L'ID avis est invalide");
        }

        return repository.saveAvis(uid, avis);
    }

    public Task<List<Avis>> getAvisByUser(String userId) {
        return repository.getAvisByUser(userId).continueWith(task -> {
            List<Avis> avisList = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    avisList.add(document.toObject(Avis.class));
                }
            }
            return avisList;
        });
    }

    public Task<Avis> getAvisUtilisateur(String uid) {
        return repository.getAvis(uid).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return document.toObject(Avis.class);
                }
            }
            return null;
        });
    }
}