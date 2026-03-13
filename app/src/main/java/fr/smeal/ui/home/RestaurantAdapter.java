package fr.smeal.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import fr.smeal.data.model.Restaurant;
import fr.smeal.databinding.ItemRestaurantBinding;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurants = new ArrayList<>();
    private OnRestaurantClickListener listener;

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant, android.widget.ImageView imageView);
    }

    public void setOnRestaurantClickListener(OnRestaurantClickListener listener) {
        this.listener = listener;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRestaurantBinding binding = ItemRestaurantBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant, listener);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRestaurantBinding itemRestaurantBinding;

        public ViewHolder(ItemRestaurantBinding itemRestaurantBinding) {
            super(itemRestaurantBinding.getRoot());
            this.itemRestaurantBinding = itemRestaurantBinding;
        }

        public void bind(Restaurant restaurant, OnRestaurantClickListener listener) {
            itemRestaurantBinding.tvNom.setText(restaurant.getNom());
            itemRestaurantBinding.tvAdresse.setText(restaurant.getAdresse());
            itemRestaurantBinding.tvRating.setText(String.valueOf(restaurant.getRating()));

            // TransitionName unique basé sur l'ID du restaurant
            itemRestaurantBinding.ivRestaurant.setTransitionName("image_" + restaurant.getId());

            if (restaurant.getImageUrl() != null && !restaurant.getImageUrl().isEmpty()) {
                Glide.with(itemRestaurantBinding.getRoot().getContext())
                        .load(restaurant.getImageUrl())
                        .centerCrop()
                        .into(itemRestaurantBinding.ivRestaurant);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRestaurantClick(restaurant, itemRestaurantBinding.ivRestaurant);
                }
            });
        }
    }
}