package fr.smeal.ui.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import fr.smeal.R;
import fr.smeal.databinding.FragmentImageEditBinding;

public class ImageEditFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "ImageEditFragment";
    private FragmentImageEditBinding binding;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor proximitySensor;
    private Uri imageUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUri = Uri.parse(getArguments().getString("imageUri"));
        }
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentImageEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadTakenPhoto();
        setupStickers();

        binding.btnBackEdit.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());
        binding.btnSaveEdit.setOnClickListener(v -> saveImageLocally());
    }

    private void loadTakenPhoto() {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            binding.photoEditorView.setBaseBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Fichier non trouvé", e);
        }
    }

    private void setupStickers() {
        int[] stickerRes = {android.R.drawable.btn_star_big_on, android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_send};
        for (int res : stickerRes) {
            ImageView iv = new ImageView(getContext());
            iv.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            iv.setPadding(10, 10, 10, 10);
            iv.setImageResource(res);
            iv.setOnClickListener(v -> {
                Bitmap stickerBitmap = BitmapFactory.decodeResource(getResources(), res);
                binding.photoEditorView.addSticker(stickerBitmap);
            });
            binding.stickerContainer.addView(iv);
        }
    }

    private void saveImageLocally() {
        Bitmap editedBitmap = binding.photoEditorView.getFinalBitmap();
        if (editedBitmap == null) return;

        // On enregistre l'image dans le cache local de l'appli
        File outputDir = requireContext().getExternalCacheDir();
        File outputFile = new File(outputDir, "edited_" + UUID.randomUUID().toString() + ".jpg");

        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            editedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            Uri localUri = Uri.fromFile(outputFile);
            
            // On renvoie l'URI LOCAL au DetailsFragment
            Bundle result = new Bundle();
            result.putString("editedImageUri", localUri.toString());
            getParentFragmentManager().setFragmentResult("imageEditKey", result);
            
            Navigation.findNavController(requireView()).popBackStack(R.id.detailsFragment, false);
            
        } catch (IOException e) {
            Log.e(TAG, "Erreur sauvegarde locale", e);
            Toast.makeText(getContext(), "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lightSensor != null) sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        if (proximitySensor != null) sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];
            float saturation = Math.min(lux / 500f, 1f);
            binding.photoEditorView.setSaturation(saturation);
            binding.tvSensorInfo.setText("Luminosité : " + (int)lux + " lx");
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            float contrast = (distance < 5) ? 2.0f : 1.0f;
            binding.photoEditorView.setContrast(contrast);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}