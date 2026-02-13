package fr.smeal.ui.details;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import fr.smeal.R;
import fr.smeal.data.model.Avis;
import fr.smeal.databinding.DialogImageViewerBinding;
import fr.smeal.databinding.ItemAvisGlassBinding;

public class AvisAdapter extends RecyclerView.Adapter<AvisAdapter.ViewHolder> {

    private List<Avis> avisList = new ArrayList<>();

    public void setAvisList(List<Avis> avisList) {
        this.avisList = avisList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAvisGlassBinding binding = ItemAvisGlassBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Avis avis = avisList.get(position);
        holder.bind(avis);
    }

    @Override
    public int getItemCount() {
        return avisList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAvisGlassBinding binding;

        public ViewHolder(ItemAvisGlassBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Avis avis) {
            binding.tvUserAvis.setText(avis.getPrenomUtilisateur() + " " + avis.getNomUtilisateur());
            binding.tvNoteAvis.setText(avis.getNote() + " ★");
            binding.tvTitreAvis.setText(avis.getTitre());
            binding.tvDescriptionAvis.setText(avis.getDescription());

            // Gestion des photos de l'avis
            if (avis.getImageUrls() != null && !avis.getImageUrls().isEmpty()) {
                binding.rvAvisPhotos.setVisibility(View.VISIBLE);
                
                AvisPhotosAdapter photosAdapter = new AvisPhotosAdapter(avis.getImageUrls(), position -> {
                    showFullScreenImages(avis.getImageUrls(), position);
                });
                
                binding.rvAvisPhotos.setLayoutManager(new LinearLayoutManager(
                        binding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false));
                binding.rvAvisPhotos.setAdapter(photosAdapter);
            } else {
                binding.rvAvisPhotos.setVisibility(View.GONE);
            }
        }

        private void showFullScreenImages(List<String> urls, int startPosition) {
            Dialog dialog = new Dialog(binding.getRoot().getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            DialogImageViewerBinding dialogBinding = DialogImageViewerBinding.inflate(LayoutInflater.from(binding.getRoot().getContext()));
            dialog.setContentView(dialogBinding.getRoot());

            ImageViewerAdapter adapter = new ImageViewerAdapter(urls);
            dialogBinding.viewPagerImages.setAdapter(adapter);
            dialogBinding.viewPagerImages.setCurrentItem(startPosition, false);

            // Mise à jour du compteur
            updateCounter(dialogBinding.tvImageCounter, startPosition, urls.size());

            dialogBinding.viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    updateCounter(dialogBinding.tvImageCounter, position, urls.size());
                }
            });

            dialogBinding.btnCloseViewer.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }

        private void updateCounter(TextView tv, int current, int total) {
            tv.setText((current + 1) + " / " + total);
        }
    }
}