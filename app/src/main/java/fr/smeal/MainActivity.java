package fr.smeal;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}


//
//// Dans ton Activity ou ViewModel
//
//// 1. Initialisation
//        UtilisateurService userService = new UtilisateurService();
//
//// DONNÉES DE TEST
//        String monAuthId = "user_auth_12345"; // Simule l'ID venant de Firebase Auth
//        Utilisateur nouveauUser = new Utilisateur(null, "Dupont", "Marie", "marie@test.com", "Paris");
//
//// 2. Appel du Service
//        userService.creerProfilUtilisateur(monAuthId, nouveauUser)
//                .addOnSuccessListener(aVoid -> {
//                    // Succès : Mise à jour de l'UI
//                    System.out.println("Profil créé avec succès via le Service !");
//                })
//                .addOnFailureListener(e -> {
//                    // Erreur : Afficher un Toast ou une alerte
//                    System.out.println("Erreur métier ou technique : " + e.getMessage());
//                });