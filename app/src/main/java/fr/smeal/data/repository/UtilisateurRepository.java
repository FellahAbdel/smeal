package fr.smeal.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import fr.smeal.data.model.Utilisateur;

public class UtilisateurRepository {

    private static final String COLLECTION = "utilisateurs";
    private final CollectionReference collectionRef;

    public UtilisateurRepository() {
        this.collectionRef = FirebaseFirestore.getInstance().collection(COLLECTION);
    }

    public Task<Void> saveUtilisateur(String uid, Utilisateur utilisateur) {
        return collectionRef.document(uid).set(utilisateur);
    }

    // READ
    public Task<DocumentSnapshot> getUtilisateur(String uid) {
        return collectionRef.document(uid).get();
    }

    // DELETE
    public Task<Void> deleteUtilisateur(String uid) {
        return collectionRef.document(uid).delete();
    }
}