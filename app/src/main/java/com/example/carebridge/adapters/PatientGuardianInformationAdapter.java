package com.example.carebridge.adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carebridge.R;
import com.example.carebridge.model.PatientGuardianInfo;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PatientGuardianInformationAdapter extends RecyclerView.Adapter<PatientGuardianInformationAdapter.GuardianViewHolder> {

    private List<PatientGuardianInfo> guardianList;

    public PatientGuardianInformationAdapter(List<PatientGuardianInfo> guardianList) {
        this.guardianList = guardianList;
    }

    @NonNull
    @Override
    public GuardianViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guardian, parent, false);
        return new GuardianViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuardianViewHolder holder, int position) {
        PatientGuardianInfo guardian = guardianList.get(position);

        setBoldLabel(holder.tvGuardianName, "", safeString(guardian.getFull_name()));
        setBoldLabel(holder.tvGuardianRelationship, "Relationship:", safeString(guardian.getRole()));
        setBoldLabel(holder.tvGuardianPhone, "Phone:", safeString(guardian.getPhone()));
        setBoldLabel(holder.tvGuardianEmail, "Email:", safeString(guardian.getEmail()));

        // Set Call Button
        holder.btnCallGuardian.setText("Call " + safeString(guardian.getFull_name()));
        holder.btnCallGuardian.setOnClickListener(v -> {
            String phone = guardian.getPhone();
            if (phone != null && !phone.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return guardianList.size();
    }

    public void setData(List<PatientGuardianInfo> newList) {
        this.guardianList = newList;
        notifyDataSetChanged();
    }

    private void setBoldLabel(TextView textView, String label, String value) {
        SpannableString spannable = new SpannableString(label + " " + value);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), 0);
        textView.setText(spannable);
    }

    private String safeString(String value) {
        return (value != null && !value.isEmpty()) ? value : "N/A";
    }

    static class GuardianViewHolder extends RecyclerView.ViewHolder {
        TextView tvGuardianName, tvGuardianRelationship, tvGuardianPhone, tvGuardianEmail;
        MaterialButton btnCallGuardian;

        public GuardianViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGuardianName = itemView.findViewById(R.id.tvGuardianName);
            tvGuardianRelationship = itemView.findViewById(R.id.tvGuardianRelationship);
            tvGuardianPhone = itemView.findViewById(R.id.tvGuardianPhone);
            tvGuardianEmail = itemView.findViewById(R.id.tvGuardianEmail);
            btnCallGuardian = itemView.findViewById(R.id.btnCallGuardian);
        }
    }
}
