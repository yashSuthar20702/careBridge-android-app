package com.example.carebridge.wear.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.wear.databinding.ItemHealthInfoBinding;
import com.example.carebridge.wear.models.HealthInfo;

import java.util.List;

/**
 * RecyclerView Adapter for displaying patient health information
 * on Wear OS (e.g., Blood Group, Age, Address).

 * Uses ViewBinding for safe and clean view access.
 */
public class HealthInfoAdapter
        extends RecyclerView.Adapter<HealthInfoAdapter.HealthInfoViewHolder> {

    // Immutable list of health info items
    private final List<HealthInfo> healthInfoList;

    /**
     * Adapter constructor
     *
     * @param healthInfoList list of health information items
     */
    public HealthInfoAdapter(@NonNull List<HealthInfo> healthInfoList) {
        this.healthInfoList = healthInfoList;
    }

    /**
     * Inflates the item layout using ViewBinding
     */
    @NonNull
    @Override
    public HealthInfoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ItemHealthInfoBinding binding =
                ItemHealthInfoBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new HealthInfoViewHolder(binding);
    }

    /**
     * Binds data to the ViewHolder safely
     */
    @Override
    public void onBindViewHolder(
            @NonNull HealthInfoViewHolder holder,
            int position
    ) {
        if (position < 0 || position >= healthInfoList.size()) {
            return;
        }

        holder.bind(healthInfoList.get(position));
    }

    /**
     * Returns total number of items
     */
    @Override
    public int getItemCount() {
        return healthInfoList.size();
    }

    /**
     * ViewHolder class for a single health info item
     */
    static final class HealthInfoViewHolder
            extends RecyclerView.ViewHolder {

        private final ItemHealthInfoBinding binding;

        /**
         * ViewHolder constructor using ViewBinding
         */
        HealthInfoViewHolder(
                @NonNull ItemHealthInfoBinding binding
        ) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Binds HealthInfo model data to UI components
         */
        void bind(@NonNull HealthInfo healthInfo) {
            binding.infoLabel.setText(healthInfo.getLabel());
            binding.infoValue.setText(healthInfo.getValue());
            binding.infoIcon.setImageResource(healthInfo.getIconRes());
        }
    }
}