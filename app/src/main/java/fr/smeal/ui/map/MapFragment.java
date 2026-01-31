package fr.smeal.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import fr.smeal.R;
import fr.smeal.data.model.Restaurant;
import fr.smeal.databinding.FragmentMapBinding;
import fr.smeal.ui.home.HomeViewModel;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private GoogleMap mMap;
    private HomeViewModel viewModel;
    private MapRestaurantAdapter adapter;
    private Map<Marker, Restaurant> markerRestaurantMap = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new MapRestaurantAdapter();
        binding.rvRestaurantsMap.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvRestaurantsMap.setAdapter(adapter);

        adapter.setOnRestaurantClickListener(restaurant -> {
            navigateToDetails(restaurant);
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // 1. Clic sur un point (dot) -> Affiche la grande card de sélection rapide
        mMap.setOnMarkerClickListener(marker -> {
            Restaurant r = markerRestaurantMap.get(marker);
            if (r != null) {
                showRestaurantQuickCard(r);
            }
            return false;
        });

        // 2. Clic sur le fond de la carte -> Masque le détail et remet la liste horizontale
        mMap.setOnMapClickListener(latLng -> {
            binding.layoutSelectedRestaurantContainer.setVisibility(View.GONE);
            binding.rvRestaurantsMap.setVisibility(View.VISIBLE);
        });

        // 3. Observation des restaurants
        viewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            if (restaurants != null && !restaurants.isEmpty()) {
                mMap.clear();
                markerRestaurantMap.clear();
                adapter.setRestaurants(restaurants);

                BitmapDescriptor dotIcon = bitmapDescriptorFromVector(getContext(), R.drawable.ic_map_pin);
                LatLng firstRestaurantPos = null;

                for (Restaurant r : restaurants) {
                    if (r.getLatitude() != 0 && r.getLongitude() != 0) {
                        LatLng pos = new LatLng(r.getLatitude(), r.getLongitude());
                        
                        // Ajout du marqueur personnalisé (point corail)
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .icon(dotIcon)
                                .anchor(0.5f, 0.5f)
                                .title(r.getNom()));
                        
                        markerRestaurantMap.put(marker, r);

                        // Capture de la position du premier restaurant pour le zoom initial
                        if (firstRestaurantPos == null) {
                            firstRestaurantPos = pos;
                        }
                    }
                }

                // Zoom automatique sur le premier restaurant au chargement
                if (firstRestaurantPos != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstRestaurantPos, 12f));
                }
            }
        });
    }

    private void showRestaurantQuickCard(Restaurant r) {
        binding.rvRestaurantsMap.setVisibility(View.GONE);
        binding.layoutSelectedRestaurantContainer.setVisibility(View.VISIBLE);
        
        binding.layoutSelectedRestaurant.tvNom.setText(r.getNom());
        binding.layoutSelectedRestaurant.tvAdresse.setText(r.getAdresse());
        
        if (r.getImageUrl() != null && !r.getImageUrl().isEmpty()) {
            Glide.with(this).load(r.getImageUrl()).centerCrop().into(binding.layoutSelectedRestaurant.ivRestaurant);
        }

        binding.layoutSelectedRestaurantContainer.setOnClickListener(v -> {
            navigateToDetails(r);
        });

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(r.getLatitude(), r.getLongitude()), 15f));
    }

    private void navigateToDetails(Restaurant restaurant) {
        Bundle args = new Bundle();
        args.putString("restaurantId", restaurant.getId());
        Navigation.findNavController(requireView()).navigate(R.id.detailsFragment, args);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}