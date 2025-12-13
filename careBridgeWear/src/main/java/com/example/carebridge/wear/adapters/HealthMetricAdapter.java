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

/**
 * Adapter for displaying health metrics on Wear OS home screen.
 * Handles selection state and visual highlighting.
 */
public class HealthMetricAdapter
        extends RecyclerView.Adapter<HealthMetricAdapter.HealthMetricViewHolder> {

    /**
     * Callback for metric click actions.
     */
    public interface OnHealthMetricClickListener {
        void onHealthMetricClick(HealthMetric metric);
    }

    private final List<HealthMetric> healthMetrics;
    private final OnHealthMetricClickListener clickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public HealthMetricAdapter(
            @NonNull List<HealthMetric> healthMetrics,
            @NonNull OnHealthMetricClickListener clickListener
    ) {
        this.healthMetrics = healthMetrics;
        this.clickListener = clickListener;
    }

    /**
     * Updates selected metric index.
     */
    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HealthMetricViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_health_metric, parent, false);
        return new HealthMetricViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull HealthMetricViewHolder holder,
            int position
    ) {
        HealthMetric metric = healthMetrics.get(position);
        boolean isSelected = position == selectedPosition;

        holder.bind(metric, isSelected);

        holder.itemView.setOnClickListener(v ->
                clickListener.onHealthMetricClick(metric)
        );
    }

    @Override
    public int getItemCount() {
        return healthMetrics.size();
    }

    /**
     * ViewHolder responsible for binding a single health metric.
     */
    static class HealthMetricViewHolder extends RecyclerView.ViewHolder {

        private final View metricContainer;
        private final View selectionCursor;
        private final View iconBackground;
        private final ImageView metricIcon;
        private final ImageView arrowIcon;
        private final TextView metricLabel;
        private final TextView metricValue;
        private final TextView metricUnit;
        private final TextView metricDescription;

        HealthMetricViewHolder(@NonNull View itemView) {
            super(itemView);

            metricContainer = itemView.findViewById(R.id.metric_container);
            selectionCursor = itemView.findViewById(R.id.selection_cursor);
            iconBackground = itemView.findViewById(R.id.icon_background);
            metricIcon = itemView.findViewById(R.id.metric_icon);
            arrowIcon = itemView.findViewById(R.id.arrow_icon);
            metricLabel = itemView.findViewById(R.id.metric_label);
            metricValue = itemView.findViewById(R.id.metric_value);
            metricUnit = itemView.findViewById(R.id.metric_unit);
            metricDescription = itemView.findViewById(R.id.metric_description);
        }

        /**
         * Binds metric data and updates selection UI.
         */
        void bind(@NonNull HealthMetric metric, boolean isSelected) {
            Context context = itemView.getContext();

            selectionCursor.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            metricIcon.setImageResource(metric.getIconRes());
            metricLabel.setText(metric.getLabel());
            metricValue.setText(metric.getValue());
            metricUnit.setText(metric.getUnit());
            metricDescription.setText(metric.getDescription());

            int mainColor = ContextCompat.getColor(context, metric.getColorRes());
            metricIcon.setColorFilter(mainColor);

            int startColor = ContextCompat.getColor(context, metric.getBgGradientStart());
            int endColor = ContextCompat.getColor(context, metric.getBgGradientEnd());

            float alpha = isSelected ? 0.9f : 0.4f;
            startColor = applyAlpha(startColor, alpha);
            endColor = applyAlpha(endColor, alpha);

            arrowIcon.setAlpha(isSelected ? 1f : 0.5f);
            metricLabel.setAlpha(isSelected ? 1f : 0.8f);
            metricValue.setAlpha(isSelected ? 1f : 0.8f);
            metricUnit.setAlpha(isSelected ? 1f : 0.8f);
            metricDescription.setAlpha(isSelected ? 1f : 0.8f);

            GradientDrawable background = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{startColor, endColor}
            );

            background.setCornerRadius(
                    context.getResources().getDimension(R.dimen.metric_corner_radius)
            );

            metricContainer.setBackground(background);
            iconBackground.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.gray_800)
            );
        }

        /**
         * Applies alpha to a color value.
         */
        private int applyAlpha(int color, float alpha) {
            int a = (int) (alpha * 255);
            return (a << 24) | (color & 0x00FFFFFF);
        }
    }
}