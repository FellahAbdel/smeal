package fr.smeal.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import fr.smeal.data.model.Restaurant;
import fr.smeal.databinding.FragmentDetailsBinding;
import fr.smeal.ui.home.HomeViewModel;

public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Récupérer l'ID passé en argument
        if (getArguments() != null) {
            String restaurantId = getArguments().getString("restaurantId");
            
            // On cherche le restaurant dans la liste du ViewModel
            viewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
                for (Restaurant r : restaurants) {
                    if (r.getId().equals(restaurantId)) {
                        displayRestaurant(r);
                        break;
                    }
                }
            });
        }

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void displayRestaurant(Restaurant r) {
        binding.tvNomDetails.setText(r.getNom());
        binding.tvAdresseDetails.setText(r.getAdresse());
        binding.collapsingToolbar.setTitle(r.getNom());

        if (r.getImageUrl() != null && !r.getImageUrl().isEmpty()) {
            Glide.with(this).load(r.getImageUrl()).centerCrop().into(binding.ivDetails);
        }
    }
}