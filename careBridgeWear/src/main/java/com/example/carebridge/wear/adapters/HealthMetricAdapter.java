package com.example.carebridge.wear.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.wear.R;
import com.example.carebridge.wear.models.HealthMetric;

import java.util.List;

public class HealthMetricAdapter extends RecyclerView.Adapter<HealthMetricAdapter.HealthMetricViewHolder> {

    public interface OnHealthMetricClickListener {
        void onHealthMetricClick(HealthMetric metric);
    }

    private List<HealthMetric> healthMetrics;
    private OnHealthMetricClickListener clickListener;
    private Context context;
    private int selectedPosition = -1;

    public HealthMetricAdapter(List<HealthMetric> healthMetrics, OnHealthMetricClickListener clickListener) {
        this.healthMetrics = healthMetrics;
        this.clickListener = clickListener;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HealthMetricViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_health_metric, parent, false);
        return new HealthMetricViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthMetricViewHolder holder, int position) {
        HealthMetric metric = healthMetrics.get(position);
        boolean isSelected = (position == selectedPosition);
        holder.bind(metric, isSelected);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onHealthMetricClick(metric);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return exact number of items (3)
        return healthMetrics.size();
    }

    class HealthMetricViewHolder extends RecyclerView.ViewHolder {
        private View metricContainer;
        private View selectionCursor;
        private View iconBackground;
        private ImageView metricIcon;
        private TextView metricLabel;
        private TextView metricValue;
        private TextView metricUnit;
        private TextView metricDescription;
        private ImageView arrowIcon;

        public HealthMetricViewHolder(@NonNull View itemView) {
            super(itemView);
            metricContainer = itemView.findViewById(R.id.metric_container);
            selectionCursor = itemView.findViewById(R.id.selection_cursor);
            iconBackground = itemView.findViewById(R.id.icon_background);
            metricIcon = itemView.findViewById(R.id.metric_icon);
            metricLabel = itemView.findViewById(R.id.metric_label);
            metricValue = itemView.findViewById(R.id.metric_value);
            metricUnit = itemView.findViewById(R.id.metric_unit);
            metricDescription = itemView.findViewById(R.id.metric_description);
            arrowIcon = itemView.findViewById(R.id.arrow_icon);
        }

        public void bind(HealthMetric metric, boolean isSelected) {
            // Set cursor visibility
            selectionCursor.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            // Set icon and text
            metricIcon.setImageResource(metric.getIconRes());
            metricLabel.setText(metric.getLabel());
            metricValue.setText(metric.getValue());
            metricUnit.setText(metric.getUnit());
            metricDescription.setText(metric.getDescription());

            // Set colors based on selection
            int color = ContextCompat.getColor(context, metric.getColorRes());
            metricIcon.setColorFilter(color);

            // Set gradient background with different intensity based on selection
            int startColor = ContextCompat.getColor(context, metric.getBgGradientStart());
            int endColor = ContextCompat.getColor(context, metric.getBgGradientEnd());

            if (isSelected) {
                // Brighter gradient for selected item
                startColor = adjustColorAlpha(startColor, 0.9f);
                endColor = adjustColorAlpha(endColor, 0.9f);
                arrowIcon.setAlpha(1.0f);
                metricLabel.setAlpha(1.0f);
                metricValue.setAlpha(1.0f);
                metricUnit.setAlpha(1.0f);
                metricDescription.setAlpha(1.0f);
            } else {
                // Dimmer gradient for non-selected items
                startColor = adjustColorAlpha(startColor, 0.4f);
                endColor = adjustColorAlpha(endColor, 0.4f);
                arrowIcon.setAlpha(0.5f);
                metricLabel.setAlpha(0.8f);
                metricValue.setAlpha(0.8f);
                metricUnit.setAlpha(0.8f);
                metricDescription.setAlpha(0.8f);
            }

            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[] { startColor, endColor }
            );
            gradient.setCornerRadius(16f);
            metricContainer.setBackground(gradient);

            // Set icon background
            iconBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_800));
        }

        private int adjustColorAlpha(int color, float alpha) {
            int red = (color >> 16) & 0xFF;
            int green = (color >> 8) & 0xFF;
            int blue = color & 0xFF;
            int alphaValue = (int) (alpha * 255);
            return (alphaValue << 24) | (red << 16) | (green << 8) | blue;
        }
    }
}