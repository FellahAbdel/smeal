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
        holder.bind(restaurant);
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

        public void bind(Restaurant restaurant) {
            itemRestaurantBinding.tvNom.setText(restaurant.getNom());
            itemRestaurantBinding.tvAdresse.setText(restaurant.getAdresse());

            // Chargement de l'image avec Glide en utilisant le nouveau champ getImageUrl()
            if (restaurant.getImageUrl() != null && !restaurant.getImageUrl().isEmpty()) {
                Glide.with(itemRestaurantBinding.getRoot().getContext())
                        .load(restaurant.getImageUrl())
                        .centerCrop()
                        .into(itemRestaurantBinding.ivRestaurant);
            }
        }
    }
}