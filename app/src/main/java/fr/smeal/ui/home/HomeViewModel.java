package fr.smeal.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import fr.smeal.data.model.Restaurant;
import fr.smeal.utils.FirestoreCallback;
import fr.smeal.data.repository.RestaurantRepository;

public class HomeViewModel extends ViewModel {

    private final RestaurantRepository repository;
    private final MutableLiveData<List<Restaurant>> restaurants = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public HomeViewModel() {
        repository = RestaurantRepository.getInstance();
        loadRestaurants(); // Charge les données dès la création
    }

    public void loadRestaurants() {
        repository.getRestaurants(new FirestoreCallback<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> result) {
                restaurants.setValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                error.setValue(e.getMessage());
            }
        });
    }

    public LiveData<List<Restaurant>> getRestaurants() {
        return restaurants;
    }

    public LiveData<String> getError() {
        return error;
    }
}