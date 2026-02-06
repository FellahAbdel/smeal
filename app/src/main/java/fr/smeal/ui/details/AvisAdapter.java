package fr.smeal.ui.details;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import fr.smeal.data.model.Avis;
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
        }
    }
}