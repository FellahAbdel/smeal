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

import fr.smeal.R;
import fr.smeal.databinding.FragmentHomeBinding;

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

        // Configuration du clic sur un restaurant pour aller vers les détails
        adapter.setOnRestaurantClickListener(restaurant -> {
            Bundle args = new Bundle();
            args.putString("restaurantId", restaurant.getId());
            Navigation.findNavController(view).navigate(R.id.detailsFragment, args);
        });

        // 2. Initialisation du ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // 3. Afficher la ProgressBar au début
        fragmentHomeBinding.progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Démarrage du chargement des restaurants...");

        // 4. Observation des données
        viewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            Log.d(TAG, "Données reçues: " + (restaurants != null ? restaurants.size() : "null") + " items");
            fragmentHomeBinding.progressBar.setVisibility(View.GONE);
            if (restaurants != null && !restaurants.isEmpty()) {
                adapter.setRestaurants(restaurants);
            } else {
                Toast.makeText(getContext(), "Aucun restaurant trouvé", Toast.LENGTH_SHORT).show();
            }
        });

        // 5. Observation des erreurs
        viewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            Log.e(TAG, "Erreur reçue: " + errorMessage);
            fragmentHomeBinding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Erreur : " + errorMessage, Toast.LENGTH_LONG).show();
        });
    }
}