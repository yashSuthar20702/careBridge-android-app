package com.example.carebridge.wear.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.wear.R;
import com.example.carebridge.wear.models.MealItem;

import java.util.List;

/**
 * RecyclerView adapter for displaying meal plan items on Wear OS.
 */
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private final List<MealItem> mealList;

    /**
     * Adapter constructor.
     */
    public MealAdapter(@NonNull List<MealItem> mealList) {
        this.mealList = mealList;
    }

    /**
     * Inflates the meal item layout.
     */
    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    /**
     * Binds meal data to the UI.
     */
    @Override
    public void onBindViewHolder(
            @NonNull MealViewHolder holder,
            int position
    ) {
        MealItem meal = mealList.get(position);

        holder.mealName.setText(meal.getName());
        holder.mealTime.setText(meal.getTime());
        holder.mealDescription.setText(meal.getDescription());
    }

    /**
     * Returns number of meals.
     */
    @Override
    public int getItemCount() {
        return mealList.size();
    }

    /**
     * ViewHolder for a single meal item.
     * Package-private + static is the correct RecyclerView pattern.
     */
    static class MealViewHolder extends RecyclerView.ViewHolder {

        final TextView mealName;
        final TextView mealTime;
        final TextView mealDescription;

        MealViewHolder(@NonNull View itemView) {
            super(itemView);

            mealName = itemView.findViewById(R.id.meal_name);
            mealTime = itemView.findViewById(R.id.meal_time);
            mealDescription = itemView.findViewById(R.id.meal_description);
        }
    }
}