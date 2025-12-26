package com.example.oya.myplaces;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.oya.myplaces.R;
import com.example.oya.myplaces.Place;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private List<Place> places;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Place place);
    }

    public PlacesAdapter(List<Place> places, OnItemClickListener listener) {
        this.places = places;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = places.get(position);

        holder.name.setText(place.getName());
        holder.description.setText(place.getDescription());
        holder.address.setText(place.getAddress());
        holder.category.setText(place.getCategory());

        // Здесь можно установить изображение
        // holder.image.setImageResource(place.getImageResId());

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(place);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView image;
        TextView name, description, address, category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            address = itemView.findViewById(R.id.address);
            category = itemView.findViewById(R.id.category);
        }
    }
}