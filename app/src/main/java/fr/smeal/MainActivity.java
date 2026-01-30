package fr.smeal;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import fr.smeal.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Pas besoin de plus de code pour l'instant,
        // le FragmentContainerView dans le XML gère tout tout seul !
    }
}