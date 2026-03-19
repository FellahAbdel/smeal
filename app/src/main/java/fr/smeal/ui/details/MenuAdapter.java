package fr.smeal.ui.details;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import fr.smeal.data.model.Menu;
import fr.smeal.databinding.ItemMenuGlassBinding;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private List<Menu> menus = new ArrayList<>();

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMenuGlassBinding binding = ItemMenuGlassBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Menu menu = menus.get(position);
        holder.bind(menu);
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMenuGlassBinding binding;

        public ViewHolder(ItemMenuGlassBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Menu menu) {
            binding.tvNomMenu.setText(menu.getNom());
            binding.tvPrixMenu.setText(String.format("%.2f €", menu.getPrix()));
            if (menu.getImageUrl() != null && !menu.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(menu.getImageUrl())
                        .centerCrop()
                        .into(binding.ivMenu);
            }
        }
    }
}