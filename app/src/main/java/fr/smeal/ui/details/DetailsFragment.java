package fr.smeal.ui.details;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.google.firebase.storage.UploadTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import fr.smeal.R;
import fr.smeal.data.model.Avis;
import fr.smeal.data.model.Reservation;
import fr.smeal.data.model.Restaurant;
import fr.smeal.data.repository.RestaurantRepository;
import fr.smeal.databinding.DialogAddAvisBinding;
import fr.smeal.databinding.DialogReservationBinding;
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
    private String currentRestaurantName;
    
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
                        currentRestaurantName = r.getNom();
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
        binding.btnReserver.setOnClickListener(v -> showReservationDialog());
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
        binding.tvRatingDetails.setText(String.format(Locale.getDefault(), "%.1f", r.getRating()));
        if (r.getImageUrl() != null && !r.getImageUrl().isEmpty()) {
            Glide.with(this).load(r.getImageUrl()).centerCrop().into(binding.ivDetails);
        }
    }

    private void showReservationDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        DialogReservationBinding resBinding = DialogReservationBinding.inflate(getLayoutInflater());
        dialog.setContentView(resBinding.getRoot());

        final Calendar cal = Calendar.getInstance();
        final int[] nbPeople = {2};

        resBinding.btnPickDate.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                resBinding.btnPickDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        resBinding.btnPickTime.setOnClickListener(v -> {
            new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                resBinding.btnPickTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        });

        resBinding.btnPlusPeople.setOnClickListener(v -> {
            nbPeople[0]++;
            resBinding.tvNbPeople.setText(String.valueOf(nbPeople[0]));
        });

        resBinding.btnMinusPeople.setOnClickListener(v -> {
            if (nbPeople[0] > 1) {
                nbPeople[0]--;
                resBinding.tvNbPeople.setText(String.valueOf(nbPeople[0]));
            }
        });

        resBinding.btnConfirmRes.setOnClickListener(v -> {
            String dateStr = resBinding.btnPickDate.getText().toString();
            String timeStr = resBinding.btnPickTime.getText().toString();

            if (dateStr.equals("Choisir la date") || timeStr.equals("Heure")) {
                Toast.makeText(getContext(), "Merci de choisir une date et une heure", Toast.LENGTH_SHORT).show();
                return;
            }

            Reservation res = new Reservation();
            res.setIdRestaurant(currentRestaurantId);
            res.setNomRestaurant(currentRestaurantName);
            res.setDate(dateStr + " à " + timeStr);
            res.setNbPersonnes(nbPeople[0]);
            res.setPrenomUtilisateur("Utilisateur");
            res.setNomUtilisateur("Smeal");

            RestaurantRepository.getInstance().addReservation(res, new FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(getContext(), "Réservation confirmée !", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
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
            dialogBinding.btnAddPhoto.setEnabled(false);
            dialogBinding.layoutUploadProgress.setVisibility(View.VISIBLE);
            
            uploadPhotosAndSaveAvis(titre, desc, note, dialog, dialogBinding);
        });

        dialog.show();
    }

    private void uploadPhotosAndSaveAvis(String titre, String desc, int note, BottomSheetDialog dialog, DialogAddAvisBinding dialogBinding) {
        final List<String> finalUrls = Collections.synchronizedList(new ArrayList<>());
        final AtomicBoolean hasError = new AtomicBoolean(false);
        final AtomicInteger processedCount = new AtomicInteger(0);
        final int totalPhotos = sessionPhotos.size();
        
        if (totalPhotos == 0) {
            saveAvisToFirestore(titre, desc, note, finalUrls, 0, 0, dialog, dialogBinding.btnSubmitAvis);
            return;
        }

        double lat = sessionPhotos.get(0).lat;
        double lon = sessionPhotos.get(0).lon;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        
        // Tableau pour suivre la progression de chaque photo (0 à 100)
        final long[] progressPerPhoto = new long[totalPhotos];

        for (int i = 0; i < totalPhotos; i++) {
            final int index = i;
            LocalPhoto lp = sessionPhotos.get(i);
            
            new Thread(() -> {
                try {
                    requireActivity().runOnUiThread(() -> 
                        dialogBinding.tvUploadStatus.setText("Optimisation de la photo " + (index + 1) + "/" + totalPhotos + "...")
                    );

                    // 1. COMPRESSION
                    byte[] compressedData = compressImage(lp.uri);
                    
                    if (compressedData == null || compressedData.length == 0) {
                        handleUploadError("Erreur lors de la compression.", hasError, processedCount, totalPhotos, titre, desc, note, finalUrls, lat, lon, dialog, dialogBinding.btnSubmitAvis);
                        return;
                    }

                    // 2. UPLOAD
                    String filename = "avis/" + UUID.randomUUID().toString() + ".jpg";
                    StorageReference ref = storage.getReference().child(filename);
                    UploadTask uploadTask = ref.putBytes(compressedData);

                    uploadTask.addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressPerPhoto[index] = (long) progress;
                        
                        // Calcul progression globale
                        long totalProgress = 0;
                        for (long p : progressPerPhoto) totalProgress += p;
                        int globalPercent = (int) (totalProgress / totalPhotos);

                        requireActivity().runOnUiThread(() -> {
                            dialogBinding.progressUpload.setProgress(globalPercent);
                            dialogBinding.tvUploadStatus.setText("Envoi : " + globalPercent + "% (" + (processedCount.get() + 1) + "/" + totalPhotos + ")");
                        });
                    });

                    uploadTask.addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        if (!hasError.get()) {
                            finalUrls.add(uri.toString());
                            checkStatus(hasError, processedCount, totalPhotos, titre, desc, note, finalUrls, lat, lon, dialog, dialogBinding.btnSubmitAvis);
                        }
                    })).addOnFailureListener(e -> {
                        Log.e(TAG, "Erreur upload Firebase", e);
                        handleUploadError("Échec de l'envoi. Vérifiez votre connexion.", hasError, processedCount, totalPhotos, titre, desc, note, finalUrls, lat, lon, dialog, dialogBinding.btnSubmitAvis);
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Erreur thread upload", e);
                    handleUploadError("Erreur technique.", hasError, processedCount, totalPhotos, titre, desc, note, finalUrls, lat, lon, dialog, dialogBinding.btnSubmitAvis);
                }
            }).start();
        }
    }

    private byte[] compressImage(Uri uri) {
        try {
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            Bitmap original = BitmapFactory.decodeStream(is);
            if (is != null) is.close();

            if (original == null) return null;

            // Redimensionnement max 1280px (suffisant pour mobile)
            int maxWidth = 1280;
            int maxHeight = 1280;
            float ratio = Math.min((float) maxWidth / original.getWidth(), (float) maxHeight / original.getHeight());
            
            Bitmap resized = original;
            if (ratio < 1.0) {
                resized = Bitmap.createScaledBitmap(original, 
                    (int) (original.getWidth() * ratio), 
                    (int) (original.getHeight() * ratio), true);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Compression JPEG à 75% (excellent compromis poids/qualité)
            resized.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            return baos.toByteArray();

        } catch (Exception e) {
            Log.e(TAG, "Erreur compression", e);
            return null;
        }
    }

    private void handleUploadError(String message, AtomicBoolean hasError, AtomicInteger count, int total, String titre, String desc, int note, List<String> urls, double lat, double lon, BottomSheetDialog dialog, View btnSubmit) {
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