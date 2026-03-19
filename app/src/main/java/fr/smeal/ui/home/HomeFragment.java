package fr.smeal.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;
import fr.smeal.data.model.Restaurant;

import fr.smeal.R;
import fr.smeal.databinding.FragmentHomeBinding;

import androidx.navigation.fragment.FragmentNavigator;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding fragmentHomeBinding;
    private HomeViewModel viewModel;
    private RestaurantAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Configuration du RecyclerView
        fragmentHomeBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RestaurantAdapter();
        fragmentHomeBinding.recyclerView.setAdapter(adapter);

        // Configuration du clic sur un restaurant pour aller vers les détails avec transition d'élément partagé
        adapter.setOnRestaurantClickListener((restaurant, imageView) -> {
            Bundle args = new Bundle();
            args.putString("restaurantId", restaurant.getId());
            
            // On utilise le même nom unique que dans l'adapter
            String transitionName = "image_" + restaurant.getId();
            
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(imageView, transitionName)
                    .build();

            Navigation.findNavController(view).navigate(R.id.detailsFragment, args, null, extras);
        });

        // 2. Initialisation du ViewModel (Shared with Activity for Search)
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // 3. Démarrer le Shimmer au début
        fragmentHomeBinding.shimmerViewContainer.setVisibility(View.VISIBLE);
        fragmentHomeBinding.shimmerViewContainer.startShimmer();
        fragmentHomeBinding.recyclerView.setVisibility(View.GONE);
        Log.d(TAG, "Démarrage du chargement des restaurants...");

        // 4. Observation des données filtrées
        viewModel.getFilteredRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            Log.d(TAG, "Données filtrées reçues: " + (restaurants != null ? restaurants.size() : "null") + " items");
            
            // Arrêter le Shimmer dès qu'on reçoit des données (même une liste vide filtrée)
            fragmentHomeBinding.shimmerViewContainer.stopShimmer();
            fragmentHomeBinding.shimmerViewContainer.setVisibility(View.GONE);
            fragmentHomeBinding.recyclerView.setVisibility(View.VISIBLE);

            if (restaurants != null) {
                adapter.setRestaurants(restaurants);
                
                List<Restaurant> allRestaurants = viewModel.getRestaurants().getValue();
                boolean isInitialLoading = (allRestaurants == null);

                if (restaurants.isEmpty()) {
                    if (!isInitialLoading) {
                        fragmentHomeBinding.layoutEmptyState.setVisibility(View.VISIBLE);
                        fragmentHomeBinding.recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    fragmentHomeBinding.layoutEmptyState.setVisibility(View.GONE);
                    fragmentHomeBinding.recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        // 5. Observation des erreurs
        viewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            Log.e(TAG, "Erreur reçue: " + errorMessage);
            fragmentHomeBinding.shimmerViewContainer.stopShimmer();
            fragmentHomeBinding.shimmerViewContainer.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Erreur : " + errorMessage, Toast.LENGTH_LONG).show();
        });
    }
}