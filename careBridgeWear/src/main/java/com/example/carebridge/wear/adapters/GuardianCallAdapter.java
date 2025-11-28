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

    private List<Guardian> guardianList;
    private OnGuardianCallListener callListener;

    public interface OnGuardianCallListener {
        void onGuardianCall(Guardian guardian);
    }

    public GuardianCallAdapter(List<Guardian> guardianList, OnGuardianCallListener callListener) {
        this.guardianList = guardianList;
        this.callListener = callListener;
        Log.d(TAG, "Adapter created with " + (guardianList != null ? guardianList.size() : 0) + " guardians");
        Log.d(TAG, "CallListener is " + (callListener != null ? "SET" : "NULL"));
    }

    @NonNull
    @Override
    public GuardianCallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating view holder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guardian_call, parent, false);
        return new GuardianCallViewHolder(view, callListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GuardianCallViewHolder holder, int position) {
        Guardian guardian = guardianList.get(position);
        Log.d(TAG, "Binding guardian at position " + position + ": " + guardian.getName() + " | Phone: " + guardian.getPhone());
        holder.bind(guardian, position);
    }

    @Override
    public int getItemCount() {
        int count = guardianList != null ? guardianList.size() : 0;
        Log.d(TAG, "Item count: " + count);
        return count;
    }

    static class GuardianCallViewHolder extends RecyclerView.ViewHolder {
        private TextView guardianName;
        private TextView guardianRelation;
        private TextView guardianPhone;
        private OnGuardianCallListener callListener;

        public GuardianCallViewHolder(@NonNull View itemView, OnGuardianCallListener callListener) {
            super(itemView);
            this.callListener = callListener;

            guardianName = itemView.findViewById(R.id.guardian_name);
            guardianRelation = itemView.findViewById(R.id.guardian_relation);
            guardianPhone = itemView.findViewById(R.id.guardian_phone);

            Log.d(TAG, "View holder created - views found: " +
                    (guardianName != null) + ", " +
                    (guardianRelation != null) + ", " +
                    (guardianPhone != null));
            Log.d(TAG, "CallListener in ViewHolder: " + (callListener != null ? "SET" : "NULL"));
        }

        public void bind(Guardian guardian, int position) {
            if (guardianName != null) {
                guardianName.setText(guardian.getName());
            }
            if (guardianRelation != null) {
                guardianRelation.setText(guardian.getRelation());
            }
            if (guardianPhone != null) {
                guardianPhone.setText(guardian.getPhone());
            }

            // Set click listener on the entire item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "=== CLICK DETECTED ===");
                    Log.d(TAG, "Position: " + position);
                    Log.d(TAG, "Guardian: " + guardian.getName());
                    Log.d(TAG, "Phone: " + guardian.getPhone());
                    Log.d(TAG, "CallListener: " + (callListener != null ? "PRESENT" : "NULL"));

                    if (callListener != null) {
                        callListener.onGuardianCall(guardian);
                        Log.d(TAG, "Call listener triggered successfully");
                    } else {
                        Log.e(TAG, "CallListener is NULL - cannot make call");
                    }
                }
            });

            // Also add long click for testing
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "LONG CLICK on guardian: " + guardian.getName());
                    return true;
                }
            });

            Log.d(TAG, "Bound guardian: " + guardian.getName() + " | Click listener set: " + (itemView.hasOnClickListeners()));
        }
    }
}