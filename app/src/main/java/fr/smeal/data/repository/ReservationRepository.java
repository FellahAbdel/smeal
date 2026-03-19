package fr.smeal.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import fr.smeal.data.model.Menu;
import fr.smeal.data.model.Reservation;

public class ReservationRepository {

    private static final String COLLECTION = "reservations";
    private final CollectionReference collectionRef;

    public ReservationRepository() {
        this.collectionRef = FirebaseFirestore.getInstance().collection(COLLECTION);
    }

    // CREATE / UPDATE
    public Task<Void> saveReservation(String uid, Reservation reservation) {
        return collectionRef.document(uid).set(reservation);
    }

    // READ
    public Task<DocumentSnapshot> getReservation(String uid) {
        return collectionRef.document(uid).get();
    }

    public Task<QuerySnapshot> getReservationsByUser(String userId) {
        return collectionRef.whereEqualTo("idUtilisateur", userId)
                .limit(5)
                .get();
    }

    // DELETE
    public Task<Void> deleteReservation(String uid) {
        return collectionRef.document(uid).delete();
    }
}