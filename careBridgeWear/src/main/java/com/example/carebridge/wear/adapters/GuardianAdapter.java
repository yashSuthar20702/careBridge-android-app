package com.example.carebridge.wear.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.wear.R;
import com.example.carebridge.wear.models.Guardian;

import java.util.List;

public class GuardianAdapter extends RecyclerView.Adapter<GuardianAdapter.GuardianViewHolder> {

    private List<Guardian> guardianList;

    public GuardianAdapter(List<Guardian> guardianList) {
        this.guardianList = guardianList;
    }

    @NonNull
    @Override
    public GuardianViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guardian, parent, false);
        return new GuardianViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuardianViewHolder holder, int position) {
        Guardian guardian = guardianList.get(position);
        holder.bind(guardian);
    }

    @Override
    public int getItemCount() {
        return guardianList.size();
    }

    static class GuardianViewHolder extends RecyclerView.ViewHolder {
        private TextView guardianName;
        private TextView guardianDetails;
        private TextView guardianPhone;
        private ImageView iconUser;
        private ImageView iconHeart;
        private ImageView iconPhone;

        public GuardianViewHolder(@NonNull View itemView) {
            super(itemView);
            guardianName = itemView.findViewById(R.id.guardian_name);
            guardianDetails = itemView.findViewById(R.id.guardian_details);
            guardianPhone = itemView.findViewById(R.id.guardian_phone);
            iconUser = itemView.findViewById(R.id.icon_user);
            iconHeart = itemView.findViewById(R.id.icon_heart);
            iconPhone = itemView.findViewById(R.id.icon_phone);
        }

        public void bind(Guardian guardian) {
            guardianName.setText(guardian.getName());
            guardianDetails.setText(guardian.getType() + " • " + guardian.getRelation());
            guardianPhone.setText(guardian.getPhone());
        }
    }
}