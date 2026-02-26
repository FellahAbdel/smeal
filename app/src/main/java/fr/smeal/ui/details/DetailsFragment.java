package fr.smeal.ui.details;

import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import fr.smeal.R;
import fr.smeal.data.model.Avis;
import fr.smeal.data.model.Restaurant;
import fr.smeal.data.repository.RestaurantRepository;
import fr.smeal.databinding.DialogAddAvisBinding;
import fr.smeal.databinding.FragmentDetailsBinding;
import fr.smeal.ui.home.HomeViewModel;
import fr.smeal.utils.FirestoreCallback;

public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";
    private FragmentDetailsBinding binding;
    private HomeViewModel viewModel;
    private MenuAdapter menuAdapter;
    private AvisAdapter avisAdapter;
    private String currentRestaurantId;
    
    private static class LocalPhoto {
        Uri uri;
        double lat, lon;
        LocalPhoto(Uri uri, double lat, double lon) {
            this.uri = uri; this.lat = lat; this.lon = lon;
        }
    }
    
    private final List<LocalPhoto> sessionPhotos = new ArrayList<>();

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

        getParentFragmentManager().setFragmentResultListener("imageEditKey", getViewLifecycleOwner(), (requestKey, bundle) -> {
            String uriStr = bundle.getString("editedImageUri");
            double lat = bundle.getDouble("latitude", 0);
            double lon = bundle.getDouble("longitude", 0);
            
            if (uriStr != null) {
                sessionPhotos.add(new LocalPhoto(Uri.parse(uriStr), lat, lon));
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

        if (!sessionPhotos.isEmpty()) {
            dialogBinding.cardAvisPhoto.setVisibility(View.VISIBLE);
            Glide.with(this).load(sessionPhotos.get(sessionPhotos.size() - 1).uri).into(dialogBinding.ivAvisPhoto);
            dialogBinding.btnAddPhoto.setText("Ajouter une autre photo (" + sessionPhotos.size() + ")");
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

            dialogBinding.btnSubmitAvis.setEnabled(false);
            Toast.makeText(getContext(), "Publication en cours...", Toast.LENGTH_SHORT).show();

            uploadPhotosAndSaveAvis(titre, desc, note, dialog, dialogBinding.btnSubmitAvis);
        });

        dialog.show();
    }

    private void uploadPhotosAndSaveAvis(String titre, String desc, int note, BottomSheetDialog dialog, View btnSubmit) {
        final List<String> finalUrls = Collections.synchronizedList(new ArrayList<>());
        final AtomicBoolean hasError = new AtomicBoolean(false);
        final AtomicInteger processedCount = new AtomicInteger(0);
        
        if (sessionPhotos.isEmpty()) {
            saveAvisToFirestore(titre, desc, note, finalUrls, 0, 0, dialog, btnSubmit);
            return;
        }

        double lat = sessionPhotos.get(0).lat;
        double lon = sessionPhotos.get(0).lon;
        FirebaseStorage storage = FirebaseStorage.getInstance();

        for (LocalPhoto lp : sessionPhotos) {
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(lp.uri);
                if (inputStream == null) {
                    handleUploadError("Erreur : Fichier inaccessible", hasError, processedCount, sessionPhotos.size(), titre, desc, note, finalUrls, lat, lon, dialog, btnSubmit);
                    continue;
                }

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[16384];
                int nRead;
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                byte[] bytes = buffer.toByteArray();
                inputStream.close();

                if (bytes.length == 0) {
                    handleUploadError("Erreur : Photo vide", hasError, processedCount, sessionPhotos.size(), titre, desc, note, finalUrls, lat, lon, dialog, btnSubmit);
                    continue;
                }

                String filename = "avis/" + UUID.randomUUID().toString() + ".jpg";
                StorageReference ref = storage.getReference().child(filename);

                ref.putBytes(bytes).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    if (!hasError.get()) {
                        finalUrls.add(uri.toString());
                        checkStatus(hasError, processedCount, sessionPhotos.size(), titre, desc, note, finalUrls, lat, lon, dialog, btnSubmit);
                    }
                })).addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur upload Firebase Storage", e);
                    handleUploadError("Échec de l'envoi d'une photo. L'avis n'a pas été publié.", hasError, processedCount, sessionPhotos.size(), titre, desc, note, finalUrls, lat, lon, dialog, btnSubmit);
                });
            } catch (Exception e) {
                Log.e(TAG, "Erreur lecture fichier", e);
                handleUploadError("Erreur technique lors de la préparation des photos.", hasError, processedCount, sessionPhotos.size(), titre, desc, note, finalUrls, lat, lon, dialog, btnSubmit);
            }
        }
    }

    private void handleUploadError(String message, AtomicBoolean hasError, AtomicInteger count, int total, String titre, String desc, int note, List<String> urls, double lat, double lon, BottomSheetDialog dialog, View btnSubmit) {
        // On ne montre le message d'erreur qu'une seule fois
        if (!hasError.getAndSet(true)) {
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                btnSubmit.setEnabled(true);
            });
        }
        checkStatus(hasError, count, total, titre, desc, note, urls, lat, lon, dialog, btnSubmit);
    }

    private void checkStatus(AtomicBoolean hasError, AtomicInteger count, int total, String titre, String desc, int note, List<String> urls, double lat, double lon, BottomSheetDialog dialog, View btnSubmit) {
        if (count.incrementAndGet() == total) {
            // Uniquement si AUCUNE erreur n'est survenue
            if (!hasError.get()) {
                saveAvisToFirestore(titre, desc, note, urls, lat, lon, dialog, btnSubmit);
            }
        }
    }

    private void saveAvisToFirestore(String titre, String desc, int note, List<String> urls, double lat, double lon, BottomSheetDialog dialog, View btnSubmit) {
        Avis avis = new Avis();
        avis.setIdRestaurant(currentRestaurantId);
        avis.setTitre(titre);
        avis.setDescription(desc);
        avis.setNote(note);
        avis.setImageUrls(urls);
        avis.setLatitude(lat);
        avis.setLongitude(lon);
        avis.setPrenomUtilisateur("Utilisateur");
        avis.setNomUtilisateur("Smeal");

        RestaurantRepository.getInstance().addAvis(avis, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), "Avis publié avec succès !", Toast.LENGTH_SHORT).show();
                sessionPhotos.clear();
                dialog.dismiss();
                viewModel.loadAvis(currentRestaurantId);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
            }
        });
    }
}