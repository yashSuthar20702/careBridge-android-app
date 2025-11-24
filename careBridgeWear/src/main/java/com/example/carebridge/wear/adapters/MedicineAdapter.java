package com.example.carebridge.wear.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.wear.R;
import com.example.carebridge.wear.models.Medicine;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private List<Medicine> medicineList;

    public MedicineAdapter(List<Medicine> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.bind(medicine);

        holder.itemView.setOnClickListener(v -> {
            medicine.setTaken(!medicine.isTaken());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return medicineList != null ? medicineList.size() : 0;
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        private TextView medicineName;
        private TextView medicineDosage;
        private TextView medicineTime;
        private ImageView statusIcon;
        private View itemView;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            medicineName = itemView.findViewById(R.id.medicine_name);
            medicineDosage = itemView.findViewById(R.id.medicine_dosage);
            medicineTime = itemView.findViewById(R.id.medicine_time);
            statusIcon = itemView.findViewById(R.id.status_icon);
        }

        public void bind(Medicine medicine) {
            medicineName.setText(medicine.getName());
            medicineDosage.setText(medicine.getDosage());
            medicineTime.setText(medicine.getTime());

            if (medicine.isTaken()) {
                itemView.setBackgroundResource(R.drawable.medicine_taken_bg);
                statusIcon.setImageResource(R.drawable.ic_check);
                medicineName.setAlpha(0.6f);
                medicineDosage.setAlpha(0.6f);
                medicineTime.setAlpha(0.6f);
            } else {
                itemView.setBackgroundResource(R.drawable.medicine_pending_bg);
                statusIcon.setImageResource(R.drawable.ic_circle);
                medicineName.setAlpha(1.0f);
                medicineDosage.setAlpha(1.0f);
                medicineTime.setAlpha(1.0f);
            }
        }
    }
}