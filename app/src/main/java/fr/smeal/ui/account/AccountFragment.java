package fr.smeal.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.firebase.auth.FirebaseAuth;
import fr.smeal.R;
import fr.smeal.data.model.Utilisateur;
import fr.smeal.data.service.UtilisateurService;
import fr.smeal.databinding.FragmentAccountBinding;

import fr.smeal.data.model.Reservation;
import fr.smeal.data.service.ReservationService;
import fr.smeal.databinding.ItemReservationGlassBinding;
import java.util.List;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;
    private FirebaseAuth mAuth;
    private UtilisateurService utilisateurService;
    private ReservationService reservationService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        utilisateurService = new UtilisateurService();
        reservationService = new ReservationService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        loadUserProfile();
        loadUserReservations();

        binding.btnLogout.setOnClickListener(v -> logout());
        binding.btnEditProfile.setOnClickListener(v -> {
            // Logique pour modifier le profil (à implémenter plus tard)
        });
    }

    private void loadUserProfile() {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            utilisateurService.getProfilUtilisateur(uid).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Utilisateur user = task.getResult();
                    updateUI(user);
                }
            });
        }
    }

    private void loadUserReservations() {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            reservationService.getReservationsByUser(uid).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    displayReservations(task.getResult());
                }
            });
        }
    }

    private void displayReservations(List<Reservation> reservations) {
        binding.layoutReservations.removeAllViews();
        
        // On met à jour le compteur avec la taille réelle de la liste
        binding.tvStatOrders.setText(String.valueOf(reservations.size()));

        if (reservations.isEmpty()) {
            binding.tvNoReservations.setVisibility(View.VISIBLE);
            binding.layoutReservations.addView(binding.tvNoReservations);
            return;
        }

        binding.tvNoReservations.setVisibility(View.GONE);

        for (Reservation res : reservations) {
            ItemReservationGlassBinding itemBinding = ItemReservationGlassBinding.inflate(
                    getLayoutInflater(), binding.layoutReservations, false);
            
            itemBinding.tvRestoName.setText(res.getNomRestaurant());
            itemBinding.tvDate.setText(res.getDate());
            itemBinding.tvNbPers.setText(res.getNbPersonnes() + " pers.");
            
            binding.layoutReservations.addView(itemBinding.getRoot());
        }
    }

    private void updateUI(Utilisateur user) {
        if (user == null) return;

        String fullName = (user.getPrenom() != null ? user.getPrenom() : "") + " " + (user.getNom() != null ? user.getNom() : "");
        binding.tvUserName.setText(fullName.trim().isEmpty() ? "Utilisateur Smeal" : fullName);
        
        binding.tvEmail.setText(user.getEmail());
        binding.tvAddress.setText(user.getAdresse() != null ? user.getAdresse() : "Adresse non renseignée");
        binding.tvPhone.setText(user.getTelephone() != null ? user.getTelephone() : "Non renseigné");
        
        binding.tvStatPoints.setText(String.valueOf(user.getPoints()));
        
        // Initialisation à 0, sera mis à jour par displayReservations()
        binding.tvStatOrders.setText("0");
        binding.tvStatReviews.setText("0"); 
        
        if (user.getPoints() > 100) {
            binding.tvUserLevel.setText("Gourmet Or");
        } else {
            binding.tvUserLevel.setText("Gourmet Argent");
        }
    }

    private void logout() {
        mAuth.signOut();
        Navigation.findNavController(requireView()).navigate(R.id.authFragment);
    }
}