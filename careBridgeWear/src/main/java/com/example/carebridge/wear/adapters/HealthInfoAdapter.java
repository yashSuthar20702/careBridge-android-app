package com.example.carebridge.wear.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.wear.R;
import com.example.carebridge.wear.models.HealthInfo;

import java.util.List;

public class HealthInfoAdapter extends RecyclerView.Adapter<HealthInfoAdapter.HealthInfoViewHolder> {

    private List<HealthInfo> healthInfoList;

    public HealthInfoAdapter(List<HealthInfo> healthInfoList) {
        this.healthInfoList = healthInfoList;
    }

    @NonNull
    @Override
    public HealthInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_health_info, parent, false);
        return new HealthInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthInfoViewHolder holder, int position) {
        HealthInfo healthInfo = healthInfoList.get(position);
        holder.bind(healthInfo);
    }

    @Override
    public int getItemCount() {
        return healthInfoList.size();
    }

    static class HealthInfoViewHolder extends RecyclerView.ViewHolder {
        private TextView infoLabel;
        private TextView infoValue;
        private ImageView infoIcon;

        public HealthInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            infoLabel = itemView.findViewById(R.id.info_label);
            infoValue = itemView.findViewById(R.id.info_value);
            infoIcon = itemView.findViewById(R.id.info_icon);
        }

        public void bind(HealthInfo healthInfo) {
            infoLabel.setText(healthInfo.getLabel());
            infoValue.setText(healthInfo.getValue());
            infoIcon.setImageResource(healthInfo.getIconRes());
        }
    }
}