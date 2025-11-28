package com.example.carebridge.wear.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carebridge.wear.R;
import com.example.carebridge.wear.models.Guardian;
import java.util.List;

public class GuardianCallAdapter extends RecyclerView.Adapter<GuardianCallAdapter.GuardianCallViewHolder> {

    private List<Guardian> guardianList;
    private OnGuardianCallListener callListener;

    public interface OnGuardianCallListener {
        void onGuardianCall(Guardian guardian);
    }

    public GuardianCallAdapter(List<Guardian> guardianList, OnGuardianCallListener callListener) {
        this.guardianList = guardianList;
        this.callListener = callListener;
    }

    @NonNull
    @Override
    public GuardianCallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guardian_call, parent, false);
        return new GuardianCallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuardianCallViewHolder holder, int position) {
        Guardian guardian = guardianList.get(position);
        holder.bind(guardian);

        holder.itemView.setOnClickListener(v -> {
            if (callListener != null) {
                callListener.onGuardianCall(guardian);
            }
        });
    }

    @Override
    public int getItemCount() {
        return guardianList != null ? guardianList.size() : 0;
    }

    static class GuardianCallViewHolder extends RecyclerView.ViewHolder {
        private TextView guardianName;
        private TextView guardianRelation;
        private TextView guardianPhone;

        public GuardianCallViewHolder(@NonNull View itemView) {
            super(itemView);
            guardianName = itemView.findViewById(R.id.guardian_name);
            guardianRelation = itemView.findViewById(R.id.guardian_relation);
            guardianPhone = itemView.findViewById(R.id.guardian_phone);
        }

        public void bind(Guardian guardian) {
            guardianName.setText(guardian.getName());
            guardianRelation.setText(guardian.getRelation());
            guardianPhone.setText(guardian.getPhone());
        }
    }
}