package fr.smeal.ui.auth;

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

import fr.smeal.R;
import fr.smeal.data.model.Utilisateur;
import fr.smeal.data.service.UtilisateurService;
import fr.smeal.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private FirebaseAuth mAuth;
    private UtilisateurService utilisateurService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        utilisateurService = new UtilisateurService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnRegisterSubmit.setOnClickListener(v -> registerUser());

        binding.btnRegisterSubmit.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());
    };

    private void registerUser() {
        String username = Objects.requireNonNull(binding.etUsername.getText()).toString().trim();
        String email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();
        String address = Objects.requireNonNull(binding.etAddress.getText()).toString().trim();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || address.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Création du compte Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        assert mAuth.getCurrentUser() != null;
                        String uid = mAuth.getCurrentUser().getUid();
                        saveUserToFirestore(uid, username, email, address);
                    } else {
                        Toast.makeText(getContext(), "Erreur Auth : " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String uid, String username, String email, String address) {
        // 2. Création de l'objet Utilisateur (selon le modèle de votre projet)
        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setId(uid);
        nouvelUtilisateur.setNom(username); // On utilise username pour le nom ici
        nouvelUtilisateur.setEmail(email);
        nouvelUtilisateur.setAdresse(address);

        // 3. Sauvegarde via le service
        utilisateurService.creerProfilUtilisateur(uid, nouvelUtilisateur)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_homeFragment);
                    } else {
                        Toast.makeText(getContext(), "Erreur BDD : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}