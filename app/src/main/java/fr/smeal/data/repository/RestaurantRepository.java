package fr.smeal.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.smeal.data.model.Restaurant;
import fr.smeal.utils.FirestoreCallback;

public class RestaurantRepository {

    private static RestaurantRepository instance;
    private final FirebaseFirestore db;
    private final String COLLECTION_NAME = "restaurants";

    private RestaurantRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized RestaurantRepository getInstance() {
        if (instance == null) {
            instance = new RestaurantRepository();
        }
        return instance;
    }

    // Récupérer la liste des restaurants
    public void getRestaurants(FirestoreCallback<List<Restaurant>> callback) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Restaurant> restaurants = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            restaurant.setId(document.getId());
                            restaurants.add(restaurant);
                        }
                        callback.onSuccess(restaurants);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
}