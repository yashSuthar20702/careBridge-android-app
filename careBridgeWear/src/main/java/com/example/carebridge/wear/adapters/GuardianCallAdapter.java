package com.example.carebridge.wear.adapters;

import android.util.Log;
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

    private static final String TAG = "GuardianCallAdapter";

    private final List<Guardian> guardianList;
    private final OnGuardianCallListener callListener;

    public interface OnGuardianCallListener {
        void onGuardianCall(Guardian guardian);
    }

    public GuardianCallAdapter(List<Guardian> guardianList, OnGuardianCallListener callListener) {
        this.guardianList = guardianList;
        this.callListener = callListener;

        Log.d(TAG, "🟢 Adapter created with " + (guardianList != null ? guardianList.size() : 0) + " guardians");
    }

    @NonNull
    @Override
    public GuardianCallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guardian_call, parent, false);

        Log.d(TAG, "🟢 ViewHolder created");
        return new GuardianCallViewHolder(view, callListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GuardianCallViewHolder holder, int position) {
        Guardian guardian = guardianList.get(position);

        Log.d(TAG, "🔄 Binding guardian at position " + position + ": "
                + guardian.getName() + " | " + guardian.getPhone());

        holder.bind(guardian);
    }

    @Override
    public int getItemCount() {
        int count = guardianList != null ? guardianList.size() : 0;
        Log.d(TAG, "📌 Adapter count = " + count);
        return count;
    }

    static class GuardianCallViewHolder extends RecyclerView.ViewHolder {

        private final TextView guardianName;
        private final TextView guardianRelation;
        private final TextView guardianPhone;
        private final OnGuardianCallListener callListener;

        private static final String VH_TAG = "GuardianVH";

        public GuardianCallViewHolder(@NonNull View itemView, OnGuardianCallListener callListener) {
            super(itemView);

            this.callListener = callListener;

            guardianName = itemView.findViewById(R.id.guardian_name);
            guardianRelation = itemView.findViewById(R.id.guardian_relation);
            guardianPhone = itemView.findViewById(R.id.guardian_phone);

            Log.d(VH_TAG, "🟢 ViewHolder initialized (Views found successfully)");
        }

        public void bind(Guardian guardian) {
            guardianName.setText(guardian.getName());
            guardianRelation.setText(guardian.getRelation());
            guardianPhone.setText(guardian.getPhone());

            Log.d(VH_TAG, "🔗 Binding: " + guardian.getName());

            // CLICK HANDLER FOR ITEM
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();

                Log.d(VH_TAG, "👉 CLICK at position: " + pos);

                if (pos == RecyclerView.NO_POSITION) {
                    Log.e(VH_TAG, "❌ Invalid adapter position");
                    return;
                }

                if (callListener == null) {
                    Log.e(VH_TAG, "❌ CallListener is NULL");
                    return;
                }

                Log.d(VH_TAG, "📞 Calling guardian: " + guardian.getName());
                callListener.onGuardianCall(guardian);
            });

            // OPTIONAL LONG PRESS LOG
            itemView.setOnLongClickListener(v -> {
                Log.d(VH_TAG, "🔍 Long click → " + guardian.getName());
                return true;
            });
        }
    }
}