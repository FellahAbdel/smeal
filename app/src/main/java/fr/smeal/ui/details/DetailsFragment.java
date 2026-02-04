package fr.smeal.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import fr.smeal.data.model.Restaurant;
import fr.smeal.databinding.FragmentDetailsBinding;
import fr.smeal.ui.home.HomeViewModel;

public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;
    private HomeViewModel viewModel;
    private MenuAdapter menuAdapter;
    private AvisAdapter avisAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        setupRecyclerViews();

        if (getArguments() != null) {
            String restaurantId = getArguments().getString("restaurantId");
            
            // 1. Charger les infos du resto
            viewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
                for (Restaurant r : restaurants) {
                    if (r.getId().equals(restaurantId)) {
                        displayRestaurant(r);
                        break;
                    }
                }
            });

            // 2. Charger Menus et Avis spécifiquement pour ce resto
            viewModel.loadMenus(restaurantId);
            viewModel.loadAvis(restaurantId);
        }

        // Observation des Menus
        viewModel.getMenus().observe(getViewLifecycleOwner(), menus -> {
            menuAdapter.setMenus(menus);
        });

        // Observation des Avis
        viewModel.getAvis().observe(getViewLifecycleOwner(), avisList -> {
            avisAdapter.setAvisList(avisList);
        });

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupRecyclerViews() {
        // Horizontale pour les menus
        menuAdapter = new MenuAdapter();
        binding.rvMenus.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvMenus.setAdapter(menuAdapter);

        // Verticale pour les avis
        avisAdapter = new AvisAdapter();
        binding.rvAvis.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAvis.setAdapter(avisAdapter);
    }

    private void displayRestaurant(Restaurant r) {
        binding.tvNomDetails.setText(r.getNom());
        binding.tvAdresseDetails.setText(r.getAdresse());
        binding.tvCuisineType.setText(r.getCuisineType());
        binding.tvRatingDetails.setText(String.format("%.1f ★", r.getRating()));

        if (r.getImageUrl() != null && !r.getImageUrl().isEmpty()) {
            Glide.with(this).load(r.getImageUrl()).centerCrop().into(binding.ivDetails);
        }
    }
}