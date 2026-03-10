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
    private final MediatorLiveData<List<Restaurant>> filteredRestaurants = new MediatorLiveData<>();
    private final MutableLiveData<List<Menu>> menus = new MutableLiveData<>();
    private final MutableLiveData<List<Avis>> avis = new MutableLiveData<>();
    private final MutableLiveData<List<Avis>> allAvis = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public HomeViewModel() {
        repository = RestaurantRepository.getInstance();
        
        filteredRestaurants.addSource(restaurants, list -> filterRestaurants());
        filteredRestaurants.addSource(searchQuery, query -> filterRestaurants());

        loadRestaurants();
        loadAllAvis();
    }

    private void filterRestaurants() {
        List<Restaurant> fullList = restaurants.getValue();
        String query = searchQuery.getValue();

        if (fullList == null) {
            filteredRestaurants.setValue(new ArrayList<>());
            return;
        }

        if (query == null || query.isEmpty()) {
            filteredRestaurants.setValue(fullList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            List<Restaurant> filtered = fullList.stream()
                    .filter(r -> (r.getNom() != null && r.getNom().toLowerCase().contains(lowerQuery)) ||
                                (r.getAdresse() != null && r.getAdresse().toLowerCase().contains(lowerQuery)) ||
                                (r.getCuisineType() != null && r.getCuisineType().toLowerCase().contains(lowerQuery)))
                    .collect(Collectors.toList());
            filteredRestaurants.setValue(filtered);
        }
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