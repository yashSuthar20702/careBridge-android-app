package com.example.carebridge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.R;
import com.example.carebridge.shared.model.PatientInfo;

import java.util.List;

public class PatientPagerAdapter extends RecyclerView.Adapter<PatientPagerAdapter.PatientViewHolder> {

    private final List<PatientInfo> patients;

    public PatientPagerAdapter(List<PatientInfo> patients) {
        this.patients = patients;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_patient_medicine_summary, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        PatientInfo patient = patients.get(position);

        holder.tvPatientName.setText(patient.getFullName());

        int total = patient.getTotalMedicines();
        int taken = patient.getTakenMedicines();
        int remaining = patient.getRemainingMedicines();

        holder.tvTotal.setText(String.valueOf(total));
        holder.tvTaken.setText(String.valueOf(taken));
        holder.tvRemaining.setText(String.valueOf(remaining));
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvTotal, tvTaken, tvRemaining;
        ImageView ivAssigned, ivTaken, ivRemaining;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvTotal = itemView.findViewById(R.id.tvTotalMedicines);
            tvTaken = itemView.findViewById(R.id.tvTakenMedicines);
            tvRemaining = itemView.findViewById(R.id.tvRemainingMedicines);
            ivAssigned = itemView.findViewById(R.id.ivAssigned);
            ivTaken = itemView.findViewById(R.id.ivTaken);
            ivRemaining = itemView.findViewById(R.id.ivRemaining);
        }
    }
}
