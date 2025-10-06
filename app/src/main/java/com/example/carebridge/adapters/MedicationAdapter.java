package com.example.carebridge.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carebridge.R;
import com.example.carebridge.model.Medication;
import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private List<Medication> medicationList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Medication medication);
    }

    public MedicationAdapter(List<Medication> medicationList) {
        this.medicationList = medicationList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medicationList.get(position);
        holder.bind(medication);
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    public class MedicationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMedName, tvMedDosage, tvMedTime;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedName = itemView.findViewById(R.id.tvMedName);
            tvMedDosage = itemView.findViewById(R.id.tvMedDosage);
            tvMedTime = itemView.findViewById(R.id.tvMedTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(medicationList.get(position));
                        }
                    }
                }
            });
        }

        public void bind(Medication medication) {
            tvMedName.setText(medication.getName());
            tvMedDosage.setText(medication.getDosage());
            tvMedTime.setText(medication.getTime());
        }
    }
}