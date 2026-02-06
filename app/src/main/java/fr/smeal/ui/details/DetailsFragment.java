package fr.smeal.ui.details;

import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import fr.smeal.R;
import fr.smeal.data.model.Avis;
import fr.smeal.data.model.Restaurant;
import fr.smeal.data.repository.RestaurantRepository;
import fr.smeal.databinding.DialogAddAvisBinding;
import fr.smeal.databinding.FragmentDetailsBinding;
import fr.smeal.ui.home.HomeViewModel;
import fr.smeal.utils.FirestoreCallback;

public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;
    private HomeViewModel viewModel;
    private MenuAdapter menuAdapter;
    private AvisAdapter avisAdapter;
    private String currentRestaurantId;

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
            currentRestaurantId = getArguments().getString("restaurantId");
            
            // 1. Charger les infos du resto
            viewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
                for (Restaurant r : restaurants) {
                    if (r.getId().equals(currentRestaurantId)) {
                        displayRestaurant(r);
                        break;
                    }
                }
            });

            // 2. Charger Menus et Avis spécifiquement pour ce resto
            viewModel.loadMenus(currentRestaurantId);
            viewModel.loadAvis(currentRestaurantId);
        }

        // Observation des Menus
        viewModel.getMenus().observe(getViewLifecycleOwner(), menus -> {
            menuAdapter.setMenus(menus);
        });

        // Observation des Avis
        viewModel.getAvis().observe(getViewLifecycleOwner(), avisList -> {
            avisAdapter.setAvisList(avisList);
        });

        // Clic pour donner un avis
        binding.btnDonnerAvis.setOnClickListener(v -> showAddAvisDialog());

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupRecyclerViews() {
        menuAdapter = new MenuAdapter();
        binding.rvMenus.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvMenus.setAdapter(menuAdapter);

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

    private void showAddAvisDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        DialogAddAvisBinding dialogBinding = DialogAddAvisBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        // LIEN AVEC LA CAMERA
        dialogBinding.btnAddPhoto.setOnClickListener(v -> {
            dialog.dismiss(); // Fermer le formulaire pour ouvrir la caméra
            Navigation.findNavController(requireView()).navigate(R.id.action_detailsFragment_to_cameraFragment);
        });

        dialogBinding.btnSubmitAvis.setOnClickListener(v -> {
            String titre = dialogBinding.etTitreAvis.getText().toString().trim();
            String desc = dialogBinding.etDescAvis.getText().toString().trim();
            int note = (int) dialogBinding.rbAvis.getRating();

            if (titre.isEmpty() || desc.isEmpty() || note == 0) {
                Toast.makeText(getContext(), "Merci de remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Création de l'objet Avis (infos utilisateurs factices pour l'instant)
            Avis avis = new Avis();
            avis.setIdRestaurant(currentRestaurantId);
            avis.setTitre(titre);
            avis.setDescription(desc);
            avis.setNote(note);
            avis.setPrenomUtilisateur("Utilisateur");
            avis.setNomUtilisateur("Smeal");

            RestaurantRepository.getInstance().addAvis(avis, new FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(getContext(), "Avis publié !", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    viewModel.loadAvis(currentRestaurantId); // Recharger les avis
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}