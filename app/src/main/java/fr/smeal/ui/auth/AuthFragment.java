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
import fr.smeal.R;
import fr.smeal.databinding.FragmentAuthBinding;

public class AuthFragment extends Fragment {
    private FragmentAuthBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        // Redirection automatique si déjà connecté
        if (mAuth.getCurrentUser() != null) {
            goToHome();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogin.setOnClickListener(v -> loginUser());
        binding.btnRegister.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_authFragment_to_registerFragment)
        );
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        boolean isValid = true;
        if (email.isEmpty()) {
            binding.etEmail.setError("Email requis");
            isValid = false;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError("Mot de passe requis");
            isValid = false;
        }

        if (!isValid) return;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        goToHome();
                    } else {
                        Toast.makeText(getContext(), "Erreur de connexion : " + (task.getException() != null ? task.getException().getMessage() : "Inconnue"), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToHome() {
        Navigation.findNavController(requireView()).navigate(R.id.action_authFragment_to_homeFragment);
    }
}