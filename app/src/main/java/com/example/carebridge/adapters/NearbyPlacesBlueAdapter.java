package com.example.carebridge.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.R;
import com.example.carebridge.model.NearbyPlace;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

public class NearbyPlacesBlueAdapter extends RecyclerView.Adapter<NearbyPlacesBlueAdapter.ViewHolder> {

    private List<NearbyPlace> list;
    private Context context;
    private GoogleMap googleMap;

    public NearbyPlacesBlueAdapter(Context ctx, List<NearbyPlace> list, GoogleMap map) {
        this.context = ctx;
        this.list = list;
        this.googleMap = map;
    }

    // Allow attaching the map reference later
    public void setMapReference(GoogleMap map) {
        this.googleMap = map;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_place_blue, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NearbyPlace place = list.get(position);

        holder.name.setText(place.name);
        holder.address.setText(place.address);
        holder.distance.setText(place.distance);

        // Zoom to marker when item clicked
        holder.itemView.setOnClickListener(v -> {
            if (googleMap != null) {
                googleMap.animateCamera(
                        com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                                place.latLng, 17
                        )
                );
            }
        });

        // Open Google Maps navigation
        holder.btnNavigate.setOnClickListener(v -> {
            String uri = "google.navigation:q=" +
                    place.latLng.latitude + "," + place.latLng.longitude;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, address, distance;
        Button btnNavigate;

        public ViewHolder(View v) {
            super(v);

            name = v.findViewById(R.id.tvPlaceName);
            address = v.findViewById(R.id.tvPlaceAddress);
            distance = v.findViewById(R.id.tvPlaceDistance);
            btnNavigate = v.findViewById(R.id.btnNavigate);
        }
    }
}

