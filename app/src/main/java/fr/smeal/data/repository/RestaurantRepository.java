package fr.smeal.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.smeal.data.model.Avis;
import fr.smeal.data.model.Menu;
import fr.smeal.data.model.Restaurant;
import fr.smeal.utils.FirestoreCallback;

public class RestaurantRepository {

    private static RestaurantRepository instance;
    private final FirebaseFirestore db;
    private final String COLLECTION_RESTAURANTS = "restaurants";
    private final String COLLECTION_MENUS = "menus";
    private final String COLLECTION_AVIS = "avis";

    private RestaurantRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized RestaurantRepository getInstance() {
        if (instance == null) {
            instance = new RestaurantRepository();
        }
        return instance;
    }

    public void getRestaurants(FirestoreCallback<List<Restaurant>> callback) {
        db.collection(COLLECTION_RESTAURANTS)
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

    public void getMenusForRestaurant(String restaurantId, FirestoreCallback<List<Menu>> callback) {
        db.collection(COLLECTION_MENUS)
                .whereEqualTo("idRestaurant", restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Menu> menus = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Menu menu = document.toObject(Menu.class);
                            menu.setId(document.getId());
                            menus.add(menu);
                        }
                        callback.onSuccess(menus);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void getAvisForRestaurant(String restaurantId, FirestoreCallback<List<Avis>> callback) {
        db.collection(COLLECTION_AVIS)
                .whereEqualTo("idRestaurant", restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Avis> avisList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Avis avis = document.toObject(Avis.class);
                            avis.setId(document.getId());
                            avisList.add(avis);
                        }
                        callback.onSuccess(avisList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void addAvis(Avis avis, FirestoreCallback<Void> callback) {
        db.collection(COLLECTION_AVIS)
                .add(avis)
                .addOnSuccessListener(documentReference -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }
}