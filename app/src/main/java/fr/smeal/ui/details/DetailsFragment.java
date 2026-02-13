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

import java.util.ArrayList;
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
    
    // Liste locale pour stocker les photos prises durant la session de rédaction de l'avis
    private List<String> sessionImageUrls = new ArrayList<>();

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
            
            viewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
                for (Restaurant r : restaurants) {
                    if (r.getId().equals(currentRestaurantId)) {
                        displayRestaurant(r);
                        break;
                    }
                }
            });

            viewModel.loadMenus(currentRestaurantId);
            viewModel.loadAvis(currentRestaurantId);
        }

        viewModel.getMenus().observe(getViewLifecycleOwner(), menus -> menuAdapter.setMenus(menus));
        viewModel.getAvis().observe(getViewLifecycleOwner(), avisList -> avisAdapter.setAvisList(avisList));

        // Récupération de l'image retouchée après le retour de l'éditeur
        getParentFragmentManager().setFragmentResultListener("imageEditKey", getViewLifecycleOwner(), (requestKey, bundle) -> {
            String url = bundle.getString("editedImageUri");
            if (url != null) {
                sessionImageUrls.add(url);
                // On réouvre automatiquement le dialogue pour continuer l'avis
                showAddAvisDialog();
            }
        });

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

        // Afficher l'aperçu si des photos ont été prises
        if (!sessionImageUrls.isEmpty()) {
            dialogBinding.cardAvisPhoto.setVisibility(View.VISIBLE);
            // On affiche la dernière photo prise par exemple
            Glide.with(this).load(sessionImageUrls.get(sessionImageUrls.size() - 1)).into(dialogBinding.ivAvisPhoto);
            dialogBinding.btnAddPhoto.setText("Ajouter une autre photo (" + sessionImageUrls.size() + ")");
        }

        dialogBinding.btnAddPhoto.setOnClickListener(v -> {
            dialog.dismiss();
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

            Avis avis = new Avis();
            avis.setIdRestaurant(currentRestaurantId);
            avis.setTitre(titre);
            avis.setDescription(desc);
            avis.setNote(note);
            avis.setImageUrls(new ArrayList<>(sessionImageUrls)); // On passe toute la liste des photos
            avis.setPrenomUtilisateur("Utilisateur");
            avis.setNomUtilisateur("Smeal");

            RestaurantRepository.getInstance().addAvis(avis, new FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(getContext(), "Avis publié avec " + sessionImageUrls.size() + " photo(s) !", Toast.LENGTH_SHORT).show();
                    sessionImageUrls.clear(); // Vider la liste après publication
                    dialog.dismiss();
                    viewModel.loadAvis(currentRestaurantId);
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