package com.example.carebridge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.R;
import com.example.carebridge.shared.model.Medication;

import java.util.List;

public class MedicinePagerAdapter extends RecyclerView.Adapter<MedicinePagerAdapter.MedicineViewHolder> {

    private final List<Medication> medicationList;

    public MedicinePagerAdapter(List<Medication> medicationList) {
        this.medicationList = medicationList;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medication_blue, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medication med = medicationList.get(position);
        if (med == null) return;

        // ✅ FIXED: Changed from getMedicine_name() to getMedicineName()
        holder.tvMedName.setText(med.getMedicineName() != null ? med.getMedicineName() : "N/A");
        holder.tvMedDosage.setText(med.getDosage() != null ? med.getDosage() : "-");

        String timeSummary = med.getTimeSummary();
        holder.tvMedTime.setText(!timeSummary.isEmpty() ? timeSummary : "-");

        // ✅ FIXED: Changed from getDuration_days() to getDurationDays()
        holder.tvMedDuration.setText("For " + med.getDurationDays() + " day" + (med.getDurationDays() > 1 ? "s" : ""));

        // ✅ FIXED: Changed from getExtra_instructions() to getExtraInstructions()
        String instructions = med.getExtraInstructions() != null ? med.getExtraInstructions() : "";
        holder.tvMedInstructions.setText(instructions + " (" + med.getFoodInstructionText() + ")");

        holder.imgMedicineIcon.setImageResource(R.drawable.ic_pill);
    }

    @Override
    public int getItemCount() {
        return medicationList != null ? medicationList.size() : 0;
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedName, tvMedDosage, tvMedTime, tvMedDuration, tvMedInstructions;
        ImageView imgMedicineIcon;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedName = itemView.findViewById(R.id.tvMedName);
            tvMedDosage = itemView.findViewById(R.id.tvMedDosage);
            tvMedTime = itemView.findViewById(R.id.tvMedTime);
            tvMedDuration = itemView.findViewById(R.id.tvMedDuration);
            tvMedInstructions = itemView.findViewById(R.id.tvMedInstructions);
            imgMedicineIcon = itemView.findViewById(R.id.imgMedicineIcon);
        }
    }
}