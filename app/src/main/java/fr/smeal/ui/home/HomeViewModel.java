package fr.smeal.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import fr.smeal.data.model.Avis;
import fr.smeal.data.model.Menu;
import fr.smeal.data.model.Restaurant;
import fr.smeal.utils.FirestoreCallback;
import fr.smeal.data.repository.RestaurantRepository;

import androidx.lifecycle.MediatorLiveData;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class HomeViewModel extends ViewModel {

    private final RestaurantRepository repository;
    private final MutableLiveData<List<Restaurant>> restaurants = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<String> selectedCategory = new MutableLiveData<>("Tout");
    private final MutableLiveData<Double> minRating = new MutableLiveData<>(0.0);
    private final MediatorLiveData<List<Restaurant>> filteredRestaurants = new MediatorLiveData<>();
    private final MutableLiveData<List<Menu>> menus = new MutableLiveData<>();
    private final MutableLiveData<List<Avis>> avis = new MutableLiveData<>();
    private final MutableLiveData<List<Avis>> allAvis = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public HomeViewModel() {
        repository = RestaurantRepository.getInstance();
        
        filteredRestaurants.addSource(restaurants, list -> filterRestaurants());
        filteredRestaurants.addSource(searchQuery, query -> filterRestaurants());
        filteredRestaurants.addSource(selectedCategory, cat -> filterRestaurants());
        filteredRestaurants.addSource(minRating, rating -> filterRestaurants());

        loadRestaurants();
        loadAllAvis();
    }

    private void filterRestaurants() {
        List<Restaurant> fullList = restaurants.getValue();
        String query = searchQuery.getValue();
        String category = selectedCategory.getValue();
        Double rating = minRating.getValue();

        if (fullList == null) {
            filteredRestaurants.setValue(new ArrayList<>());
            return;
        }

        String lowerQuery = (query != null) ? query.toLowerCase().trim() : "";
        double minR = (rating != null) ? rating : 0.0;
        String catFilter = (category != null) ? category : "Tout";

        List<Restaurant> filtered = fullList.stream()
                .filter(r -> {
                    // Filter by Search Query
                    boolean matchesQuery = lowerQuery.isEmpty() ||
                            (r.getNom() != null && r.getNom().toLowerCase().contains(lowerQuery)) ||
                            (r.getAdresse() != null && r.getAdresse().toLowerCase().contains(lowerQuery)) ||
                            (r.getCuisineType() != null && r.getCuisineType().toLowerCase().contains(lowerQuery));

                    // Filter by Category
                    boolean matchesCategory = catFilter.equals("Tout") ||
                            (r.getCuisineType() != null && r.getCuisineType().equalsIgnoreCase(catFilter));

                    // Filter by Rating
                    boolean matchesRating = r.getRating() >= minR;

                    return matchesQuery && matchesCategory && matchesRating;
                })
                .collect(Collectors.toList());
        
        filteredRestaurants.setValue(filtered);
    }

    public void setSelectedCategory(String category) {
        selectedCategory.setValue(category);
    }

    public void setMinRating(Double rating) {
        minRating.setValue(rating);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<List<Restaurant>> getFilteredRestaurants() {
        return filteredRestaurants;
    }

    public void loadRestaurants() {
        repository.getRestaurants(new FirestoreCallback<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> result) {
                restaurants.setValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                error.setValue("Erreur chargement restaurants: " + e.getMessage());
            }
        });
    }

    public void loadAllAvis() {
        repository.getAllAvis(new FirestoreCallback<List<Avis>>() {
            @Override
            public void onSuccess(List<Avis> result) {
                allAvis.setValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                error.setValue("Erreur chargement tous les avis: " + e.getMessage());
            }
        });
    }

    public void loadMenus(String restaurantId) {
        repository.getMenusForRestaurant(restaurantId, new FirestoreCallback<List<Menu>>() {
            @Override
            public void onSuccess(List<Menu> result) {
                menus.setValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                error.setValue("Erreur chargement menus: " + e.getMessage());
            }
        });
    }

    public void loadAvis(String restaurantId) {
        repository.getAvisForRestaurant(restaurantId, new FirestoreCallback<List<Avis>>() {
            @Override
            public void onSuccess(List<Avis> result) {
                avis.setValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                error.setValue("Erreur chargement avis: " + e.getMessage());
            }
        });
    }

    public LiveData<List<Restaurant>> getRestaurants() {
        return restaurants;
    }
    public LiveData<List<Menu>> getMenus() {
        return menus;
    }
    public LiveData<List<Avis>> getAvis() {
        return avis;
    }
    public LiveData<List<Avis>> getAllAvis() {
        return allAvis;
    }
    public LiveData<String> getError() {
        return error;
    }
}