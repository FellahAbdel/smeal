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

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean isSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();
        setupSearchBar();
        setupKeyboardListener();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }
    }

    private void setupKeyboardListener() {
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            binding.getRoot().getWindowVisibleDisplayFrame(r);
            int screenHeight = binding.getRoot().getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                binding.bottomNavigation.setVisibility(View.GONE);
            } else {
                if (!isSearching) {
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void toggleSearchMode(boolean searching) {
        this.isSearching = searching;
        if (searching) {
            binding.logo.setVisibility(View.GONE);
            binding.tvAppName.setVisibility(View.GONE);
            binding.btnSearch.setVisibility(View.GONE);
            binding.bottomNavigation.setVisibility(View.GONE);

            binding.btnBackSearch.setVisibility(View.VISIBLE);
            binding.etSearch.setVisibility(View.VISIBLE);
            binding.etSearch.requestFocus();
            showKeyboard();
        } else {
            binding.logo.setVisibility(View.VISIBLE);
            binding.tvAppName.setVisibility(View.VISIBLE);
            binding.btnSearch.setVisibility(View.VISIBLE);
            binding.bottomNavigation.setVisibility(View.VISIBLE);

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