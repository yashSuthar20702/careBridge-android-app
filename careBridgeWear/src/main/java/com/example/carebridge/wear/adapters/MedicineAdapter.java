package com.example.carebridge.wear.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.shared.model.Medication;
import com.example.carebridge.wear.R;

import java.util.List;

/**
 * RecyclerView adapter for displaying medicine details on Wear OS.
 * Items are displayed in read-only mode.
 */
public class MedicineAdapter
        extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private final List<Medication> medicineList;

    /**
     * Adapter constructor.
     */
    public MedicineAdapter(@NonNull List<Medication> medicineList) {
        this.medicineList = medicineList;
    }

    /**
     * Inflates medicine item layout.
     */
    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    /**
     * Binds medicine data to UI.
     */
    @Override
    public void onBindViewHolder(
            @NonNull MedicineViewHolder holder,
            int position
    ) {
        holder.bind(medicineList.get(position));
    }

    /**
     * Returns number of medicines.
     */
    @Override
    public int getItemCount() {
        return medicineList != null ? medicineList.size() : 0;
    }

    /**
     * ViewHolder for a single medicine item.
     * Package-private + static follows RecyclerView best practice.
     */
    static class MedicineViewHolder extends RecyclerView.ViewHolder {

        final TextView medicineName;
        final TextView medicineDosage;
        final TextView medicineTime;
        final TextView medicineFood;

        MedicineViewHolder(@NonNull View itemView) {
            super(itemView);

            medicineName = itemView.findViewById(R.id.medicine_name);
            medicineDosage = itemView.findViewById(R.id.medicine_dosage);
            medicineTime = itemView.findViewById(R.id.medicine_time);
            medicineFood = itemView.findViewById(R.id.medicine_food);
        }

        /**
         * Displays medicine details.
         */
        void bind(@NonNull Medication medicine) {

            medicineName.setText(medicine.getMedicineName());
            medicineDosage.setText(medicine.getDosage());
            medicineTime.setText(medicine.getTimeSummary());
            medicineFood.setText(medicine.getFoodInstructionText());

            float alpha = medicine.isTaken() ? 0.6f : 1.0f;

            medicineName.setAlpha(alpha);
            medicineDosage.setAlpha(alpha);
            medicineTime.setAlpha(alpha);
            medicineFood.setAlpha(alpha);
        }
    }
}