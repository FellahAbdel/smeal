package fr.smeal.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.smeal.data.model.Avis;
import fr.smeal.data.model.Utilisateur;

public class AvisRepository {

    private static final String COLLECTION = "avis";
    private final CollectionReference collectionRef;

    public AvisRepository() {
        this.collectionRef = FirebaseFirestore.getInstance().collection(COLLECTION);
    }

    // CREATE / UPDATE
    public Task<Void> saveAvis(String uid, Avis avis) {
        return collectionRef.document(uid).set(avis);
    }

    // READ
    public Task<DocumentSnapshot> getAvis(String uid) {
        return collectionRef.document(uid).get();
    }

    // DELETE
    public Task<Void> deleteUtilisateur(String uid) {
        return collectionRef.document(uid).delete();
    }
}