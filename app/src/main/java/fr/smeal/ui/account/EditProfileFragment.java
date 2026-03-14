package fr.smeal.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import fr.smeal.data.service.UtilisateurService;
import fr.smeal.databinding.FragmentEditProfileBinding;

public class EditProfileFragment extends Fragment {
    private FragmentEditProfileBinding binding;
    private UtilisateurService utilisateurService;
    private String currentUid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utilisateurService = new UtilisateurService();
        currentUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserData();
        binding.btnSave.setOnClickListener(v -> saveChanges());
    }

    private void loadUserData() {
        utilisateurService.getProfilUtilisateur(currentUid).addOnSuccessListener(user -> {
            if (user != null) {
                binding.etNom.setText(user.getNom());
                binding.etPrenom.setText(user.getPrenom());
                binding.etAdresse.setText(user.getPrenom());
                binding.etTelephone.setText(user.getPrenom());
                binding.etEmail.setText(user.getPrenom());
            }
        });
    }

    private void saveChanges() {
        String nom = Objects.requireNonNull(binding.etNom.getText()).toString().trim();
        String prenom = Objects.requireNonNull(binding.etPrenom.getText()).toString().trim();
        String adresse = Objects.requireNonNull(binding.etAdresse.getText()).toString().trim();
        String telephone = Objects.requireNonNull(binding.etTelephone.getText()).toString().trim();
        String email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();

        utilisateurService.updateProfil(currentUid, nom, prenom, adresse, telephone, email)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profil mis à jour !", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp(); // Retour au profil
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}