package fr.smeal;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import fr.smeal.databinding.ActivityMainBinding;

import androidx.lifecycle.ViewModelProvider;
import fr.smeal.ui.home.HomeViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private HomeViewModel viewModel;
    private boolean isSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupNavigation();
        setupSearchBar();
        setupFilters();
        setupKeyboardListener();
    }

    private void setupFilters() {
        binding.chipGroupFilters.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipAll) {
                viewModel.setSelectedCategory("Tout");
                viewModel.setMinRating(0.0);
            } else if (checkedId == R.id.chipTopRated) {
                viewModel.setSelectedCategory("Tout");
                viewModel.setMinRating(4.5);
            } else if (checkedId == R.id.chipItalien) {
                viewModel.setSelectedCategory("Italien");
                viewModel.setMinRating(0.0);
            } else if (checkedId == R.id.chipJaponais) {
                viewModel.setSelectedCategory("Japonais");
                viewModel.setMinRating(0.0);
            } else if (checkedId == R.id.chipBurger) {
                viewModel.setSelectedCategory("Burger");
                viewModel.setMinRating(0.0);
            } else if (checkedId == R.id.chipSante) {
                viewModel.setSelectedCategory("Santé");
                viewModel.setMinRating(0.0);
            }
        });
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            // Liaison avec la BottomNavigationView (à l'intérieur du container)
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

            // Gestion de la visibilité du header et footer selon la destination
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                // Masquer pour les détails, la caméra ET l'édition d'image
                if (id == R.id.detailsFragment || id == R.id.cameraFragment || id == R.id.imageEditFragment || id == R.id.authFragment || id == R.id.registerFragment) {
                    binding.header.setVisibility(View.GONE);
                    binding.filterScrollView.setVisibility(View.GONE);
                    binding.bottomNavContainer.setVisibility(View.GONE);
                } else {
                    binding.header.setVisibility(View.VISIBLE);
                    binding.filterScrollView.setVisibility(View.VISIBLE);
                    // On affiche le footer uniquement si on n'est pas en mode recherche
                    if (!isSearching) {
                        binding.bottomNavContainer.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void setupKeyboardListener() {
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            binding.getRoot().getWindowVisibleDisplayFrame(r);
            int screenHeight = binding.getRoot().getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            // On masque le CONTAINER (le verre) et pas juste la navigation
            if (keypadHeight > screenHeight * 0.15) {
                binding.bottomNavContainer.setVisibility(View.GONE);
            } else {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                if (navHostFragment != null) {
                    NavController navController = navHostFragment.getNavController();
                    if (navController.getCurrentDestination() != null) {
                        int id = navController.getCurrentDestination().getId();
                        // Ne pas réafficher si on est sur détails, caméra, édition ou en recherche
                        if (id != R.id.detailsFragment && id != R.id.cameraFragment && id != R.id.imageEditFragment && id != R.id.authFragment && id != R.id.registerFragment && !isSearching) {
                            binding.bottomNavContainer.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void setupSearchBar() {
        binding.btnSearch.setOnClickListener(v -> toggleSearchMode(true));

        binding.btnBackSearch.setOnClickListener(v -> {
            toggleSearchMode(false);
            binding.etSearch.setText("");
            hideKeyboard();
            binding.etSearch.clearFocus();
        });

        binding.btnClearSearch.setOnClickListener(v -> binding.etSearch.setText(""));

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void toggleSearchMode(boolean searching) {
        this.isSearching = searching;
        if (searching) {
            binding.logo.setVisibility(View.GONE);
            binding.tvAppName.setVisibility(View.GONE);
            binding.btnSearch.setVisibility(View.GONE);
            binding.bottomNavContainer.setVisibility(View.GONE);

            binding.btnBackSearch.setVisibility(View.VISIBLE);
            binding.etSearch.setVisibility(View.VISIBLE);
            binding.etSearch.requestFocus();
            showKeyboard();
        } else {
            viewModel.setSearchQuery("");
            binding.logo.setVisibility(View.VISIBLE);
            binding.tvAppName.setVisibility(View.VISIBLE);
            binding.btnSearch.setVisibility(View.VISIBLE);
            binding.bottomNavContainer.setVisibility(View.VISIBLE);

            binding.btnBackSearch.setVisibility(View.GONE);
            binding.etSearch.setVisibility(View.GONE);
            binding.btnClearSearch.setVisibility(View.GONE);
        }
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.etSearch.getWindowToken(), 0);
        }
    }
}
