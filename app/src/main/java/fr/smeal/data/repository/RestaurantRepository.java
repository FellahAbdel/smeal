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

    // Constructeur privé
    private RestaurantRepository() {
        // C'est ICI que l'on récupère l'instance de la BDD connectée
        db = FirebaseFirestore.getInstance();
    }

    // Singleton : pour récupérer le repository partout dans l'app
    public static synchronized RestaurantRepository getInstance() {
        if (instance == null) {
            instance = new RestaurantRepository();
        }
        return instance;
    }

    // Méthode pour récupérer tous les restaurants
    public void getRestaurants(FirestoreCallback<List<Restaurant>> callback) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Restaurant> restaurants = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Magie : Firebase transforme le JSON en objet Java
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            restaurant.setId(document.getId()); // On garde l'ID du document
                            restaurants.add(restaurant);
                        }
                        // On renvoie la liste via le callback
                        callback.onSuccess(restaurants);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // Tu pourras ajouter ici : addRestaurant(), deleteRestaurant(), etc.
}