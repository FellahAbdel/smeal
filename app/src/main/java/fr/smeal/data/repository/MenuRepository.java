package fr.smeal.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.smeal.data.model.Menu;
import fr.smeal.data.model.Utilisateur;

public class MenuRepository {

    private static final String COLLECTION = "menus";
    private final CollectionReference collectionRef;

    public MenuRepository() {
        this.collectionRef = FirebaseFirestore.getInstance().collection(COLLECTION);
    }

    // CREATE / UPDATE
    public Task<Void> saveMenu(String uid, Menu menu) {
        return collectionRef.document(uid).set(menu);
    }

    // READ
    public Task<DocumentSnapshot> getMenu(String uid) {
        return collectionRef.document(uid).get();
    }

    // DELETE
    public Task<Void> deleteMenu(String uid) {
        return collectionRef.document(uid).delete();
    }
}