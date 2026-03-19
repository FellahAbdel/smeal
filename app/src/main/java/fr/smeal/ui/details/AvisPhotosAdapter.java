package fr.smeal.ui.details;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import fr.smeal.databinding.ItemAvisPhotoBinding;

public class AvisPhotosAdapter extends RecyclerView.Adapter<AvisPhotosAdapter.ViewHolder> {

    private final List<String> imageUrls;
    private OnPhotoClickListener listener;

    public interface OnPhotoClickListener {
        void onPhotoClick(int position);
    }

    public AvisPhotosAdapter(List<String> imageUrls, OnPhotoClickListener listener) {
        this.imageUrls = imageUrls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAvisPhotoBinding binding = ItemAvisPhotoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = imageUrls.get(position);
        Glide.with(holder.binding.getRoot().getContext())
                .load(url)
                .centerCrop()
                .into(holder.binding.ivAvisPhotoItem);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPhotoClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemAvisPhotoBinding binding;
        ViewHolder(ItemAvisPhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}