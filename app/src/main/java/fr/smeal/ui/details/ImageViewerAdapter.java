package fr.smeal.ui.details;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import fr.smeal.databinding.ItemImageViewerBinding;

public class ImageViewerAdapter extends RecyclerView.Adapter<ImageViewerAdapter.ViewHolder> {

    private final List<String> imageUrls;

    public ImageViewerAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageViewerBinding binding = ItemImageViewerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.binding.getRoot().getContext())
                .load(imageUrls.get(position))
                .fitCenter()
                .into(holder.binding.ivFullScreen);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemImageViewerBinding binding;
        ViewHolder(ItemImageViewerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}