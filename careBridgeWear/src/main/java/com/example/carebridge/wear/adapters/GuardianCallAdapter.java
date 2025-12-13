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

/**
 * RecyclerView adapter used to display guardians and trigger call action.
 * Designed for Wear OS and follows best practices.
 */
public class GuardianCallAdapter
        extends RecyclerView.Adapter<GuardianCallAdapter.GuardianCallViewHolder> {

    private static final String TAG = "GuardianCallAdapter";

    private final List<Guardian> guardianList;
    private final OnGuardianCallListener callListener;

    public interface OnGuardianCallListener {
        void onGuardianCall(Guardian guardian);
    }

    public GuardianCallAdapter(@NonNull List<Guardian> guardianList,
                               @NonNull OnGuardianCallListener callListener) {
        this.guardianList = guardianList;
        this.callListener = callListener;

        Log.d(
                TAG,
                String.valueOf(guardianList.size())
        );
    }

    @NonNull
    @Override
    public GuardianCallViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guardian_call, parent, false);
        return new GuardianCallViewHolder(view, callListener);
    }

    @Override
    public void onBindViewHolder(
            @NonNull GuardianCallViewHolder holder,
            int position
    ) {
        holder.bind(guardianList.get(position));
    }

    @Override
    public int getItemCount() {
        return guardianList.size();
    }

    /**
     * ViewHolder responsible for binding guardian data.
     */
    public static class GuardianCallViewHolder extends RecyclerView.ViewHolder {

        private final TextView guardianName;
        private final TextView guardianRelation;
        private final TextView guardianPhone;
        private final OnGuardianCallListener callListener;

        public GuardianCallViewHolder(
                @NonNull View itemView,
                @NonNull OnGuardianCallListener callListener
        ) {
            super(itemView);
            this.callListener = callListener;

            guardianName = itemView.findViewById(R.id.guardian_name);
            guardianRelation = itemView.findViewById(R.id.guardian_relation);
            guardianPhone = itemView.findViewById(R.id.guardian_phone);
        }

        /**
         * Binds guardian data and handles click events.
         */
        public void bind(@NonNull Guardian guardian) {
            guardianName.setText(guardian.getName());
            guardianRelation.setText(guardian.getRelation());
            guardianPhone.setText(guardian.getPhone());

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();

                if (position == RecyclerView.NO_POSITION) {
                    Log.e(
                            TAG,
                            itemView.getContext()
                                    .getString(R.string.log_invalid_position)
                    );
                    return;
                }

                callListener.onGuardianCall(guardian);
            });
        }
    }
}