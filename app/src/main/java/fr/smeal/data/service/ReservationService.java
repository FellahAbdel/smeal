package fr.smeal.data.service;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.smeal.data.model.Reservation;
import fr.smeal.data.repository.ReservationRepository;

public class ReservationService {

    private final ReservationRepository repository;

    public ReservationService() {
        this.repository = new ReservationRepository();
    }

    public Task<Void> creerReservation(String uid, Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("La reservation ne peut pas être null");
        }
        if (uid == null || uid.isEmpty()) {
            throw new IllegalArgumentException("L'ID reservation est invalide");
        }

        return repository.saveReservation(uid, reservation);
    }

    public Task<List<Reservation>> getReservationsByUser(String userId) {
        return repository.getReservationsByUser(userId).continueWith(task -> {
            List<Reservation> reservations = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    reservations.add(document.toObject(Reservation.class));
                }
            }
            return reservations;
        });
    }

    public Task<Reservation> getMenuRestaurant(String uid) {
        return repository.getReservation(uid).continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return document.toObject(Reservation.class);
                }
            }
            return null;
        });
    }
}