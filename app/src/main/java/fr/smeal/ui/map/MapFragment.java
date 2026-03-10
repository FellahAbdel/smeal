package fr.smeal.ui.map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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
import java.util.List;
import java.util.Map;

import fr.smeal.R;
import fr.smeal.data.model.Avis;
import fr.smeal.data.model.Restaurant;
import fr.smeal.databinding.DialogImageViewerBinding;
import fr.smeal.databinding.FragmentMapBinding;
import fr.smeal.ui.details.ImageViewerAdapter;
import fr.smeal.ui.home.HomeViewModel;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private GoogleMap mMap;
    private HomeViewModel viewModel;
    private MapRestaurantAdapter adapter;
    private final Map<Marker, Restaurant> markerRestaurantMap = new HashMap<>();
    private final Map<Marker, Avis> markerAvisMap = new HashMap<>();

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

        adapter.setOnRestaurantClickListener(this::navigateToDetails);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(marker -> {
            if (markerRestaurantMap.containsKey(marker)) {
                showRestaurantQuickCard(markerRestaurantMap.get(marker));
            } else if (markerAvisMap.containsKey(marker)) {
                Avis avis = markerAvisMap.get(marker);
                if (avis != null && avis.getImageUrls() != null) {
                    showFullScreenImages(avis.getImageUrls(), 0);
                }
            }
            return false;
        });

        mMap.setOnMapClickListener(latLng -> {
            if (binding != null) {
                binding.layoutSelectedRestaurantContainer.setVisibility(View.GONE);
                binding.rvRestaurantsMap.setVisibility(View.VISIBLE);
            }
        });

        // Observation des restaurants filtrés
        viewModel.getFilteredRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            if (restaurants != null && mMap != null) {
                updateRestaurantMarkers(restaurants);
            }
        });

        // Observation de TOUS les avis pour les photos
        viewModel.getAllAvis().observe(getViewLifecycleOwner(), avisList -> {
            if (avisList != null && mMap != null) {
                updatePhotoMarkers(avisList);
            }
        });
    }

    private void updateRestaurantMarkers(List<Restaurant> restaurants) {
        if (!isAdded() || getContext() == null) return;
        
        BitmapDescriptor dotIcon = bitmapDescriptorFromVector(getContext(), R.drawable.ic_map_pin);
        LatLng firstRestaurantPos = null;

        // On nettoie les anciens marqueurs de restos
        for (Marker m : markerRestaurantMap.keySet()) m.remove();
        markerRestaurantMap.clear();

        for (Restaurant r : restaurants) {
            if (r.getLatitude() != 0 && r.getLongitude() != 0) {
                LatLng pos = new LatLng(r.getLatitude(), r.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .icon(dotIcon)
                        .anchor(0.5f, 0.5f)
                        .title(r.getNom()));
                if (marker != null) {
                    markerRestaurantMap.put(marker, r);
                    if (firstRestaurantPos == null) firstRestaurantPos = pos;
                }
            }
        }
        if (firstRestaurantPos != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstRestaurantPos, 12f));
        }
        adapter.setRestaurants(restaurants);
    }

    private void updatePhotoMarkers(List<Avis> avisList) {
        if (!isAdded() || getContext() == null) return;

        // On nettoie les anciens marqueurs de photos
        for (Marker m : markerAvisMap.keySet()) m.remove();
        markerAvisMap.clear();

        for (Avis avis : avisList) {
            if (avis.getLatitude() != 0 && avis.getLongitude() != 0 && avis.getImageUrls() != null && !avis.getImageUrls().isEmpty()) {
                String firstImageUrl = avis.getImageUrls().get(0);
                
                Glide.with(this).asBitmap().load(firstImageUrl).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (!isAdded() || mMap == null) return;
                        
                        Bitmap circularBitmap = getCircularBitmap(resource);
                        LatLng pos = new LatLng(avis.getLatitude(), avis.getLongitude());
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .icon(BitmapDescriptorFactory.fromBitmap(circularBitmap))
                                .anchor(0.5f, 0.5f));
                        if (marker != null) markerAvisMap.put(marker, avis);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
            }
        }
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = 120; // Taille de la miniature
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, size, size);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, size, size, false), rect, rect, paint);
        
        // Bordure blanche style Liquid Glass
        paint.setXfermode(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        paint.setStrokeWidth(6);
        canvas.drawCircle(size / 2f, size / 2f, (size / 2f) - 3, paint);
        
        return output;
    }

    private void showFullScreenImages(List<String> urls, int startPosition) {
        if (!isAdded()) return;
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        DialogImageViewerBinding dialogBinding = DialogImageViewerBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        ImageViewerAdapter adapter = new ImageViewerAdapter(urls);
        dialogBinding.viewPagerImages.setAdapter(adapter);
        dialogBinding.viewPagerImages.setCurrentItem(startPosition, false);

        dialogBinding.btnCloseViewer.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showRestaurantQuickCard(Restaurant r) {
        if (binding == null) return;
        binding.rvRestaurantsMap.setVisibility(View.GONE);
        binding.layoutSelectedRestaurantContainer.setVisibility(View.VISIBLE);
        binding.layoutSelectedRestaurant.tvNom.setText(r.getNom());
        binding.layoutSelectedRestaurant.tvAdresse.setText(r.getAdresse());
        if (r.getImageUrl() != null && !r.getImageUrl().isEmpty()) {
            Glide.with(this).load(r.getImageUrl()).centerCrop().into(binding.layoutSelectedRestaurant.ivRestaurant);
        }
        binding.layoutSelectedRestaurantContainer.setOnClickListener(v -> navigateToDetails(r));
        if (mMap != null) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(r.getLatitude(), r.getLongitude()), 15f));
    }

    private void navigateToDetails(Restaurant restaurant) {
        if (getView() != null) {
            Bundle args = new Bundle();
            args.putString("restaurantId", restaurant.getId());
            Navigation.findNavController(requireView()).navigate(R.id.detailsFragment, args);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        if (vectorDrawable == null) return null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}