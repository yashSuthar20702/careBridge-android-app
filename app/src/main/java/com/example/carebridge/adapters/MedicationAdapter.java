package com.example.carebridge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carebridge.R;
import com.example.carebridge.model.Medication;
import java.util.ArrayList;
import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private List<Medication> medicationList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private final boolean isBlue; // New flag

    public interface OnItemClickListener {
        void onItemClick(Medication medication);
    }

    // Updated constructor to accept isBlue flag
    public MedicationAdapter(List<Medication> medicationList, boolean isBlue) {
        this.isBlue = isBlue;
        if (medicationList != null) {
            this.medicationList = medicationList;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void updateList(List<Medication> newList) {
        this.medicationList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate different layout based on isBlue flag
        int layoutId = isBlue ? R.layout.item_medication_blue : R.layout.item_medication;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medicationList.get(position);
        holder.bind(medication);
    }

    @Override
    public int getItemCount() {
        return medicationList != null ? medicationList.size() : 0;
    }

    class MedicationViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvMedName, tvMedDosage, tvMedTime, tvMedDuration, tvMedInstructions;

        MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedName = itemView.findViewById(R.id.tvMedName);
            tvMedDosage = itemView.findViewById(R.id.tvMedDosage);
            tvMedTime = itemView.findViewById(R.id.tvMedTime);
            tvMedDuration = itemView.findViewById(R.id.tvMedDuration);
            tvMedInstructions = itemView.findViewById(R.id.tvMedInstructions);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(medicationList.get(position));
                    }
                }
            });
        }

        void bind(Medication medication) {
            // Medicine name
            tvMedName.setText(medication.getMedicine_name());

            // Dosage
            tvMedDosage.setText("Dosage: " + medication.getDosage());

            // Timing (Morning, Afternoon, etc.)
            String timeSummary = medication.getTimeSummary();
            tvMedTime.setText(timeSummary.isEmpty() ? "Timing: N/A" : "Take at: " + timeSummary);

            // Duration
            tvMedDuration.setText("For " + medication.getDuration_days() + " days");

            // Extra instructions
            String instructions = medication.getExtra_instructions() != null && !medication.getExtra_instructions().isEmpty()
                    ? medication.getExtra_instructions()
                    : "No additional instructions";
            tvMedInstructions.setText("Instructions: " + instructions +
                    (medication.getWith_food() == 1 ? " (With food)" : " (Without food)"));
        }
    }
}
