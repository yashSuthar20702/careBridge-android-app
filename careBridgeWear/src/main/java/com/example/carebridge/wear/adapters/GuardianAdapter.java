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

/**
 * GuardianAdapter

 * Displays guardian information in a Wear OS optimized RecyclerView.
 */
public class GuardianAdapter
        extends RecyclerView.Adapter<GuardianAdapter.GuardianViewHolder> {

    private final List<Guardian> guardianList;

    public GuardianAdapter(List<Guardian> guardianList) {
        this.guardianList = guardianList;
    }

    @NonNull
    @Override
    public GuardianViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guardian, parent, false);
        return new GuardianViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull GuardianViewHolder holder,
            int position
    ) {
        holder.bind(guardianList.get(position));
    }

    @Override
    public int getItemCount() {
        return guardianList == null ? 0 : guardianList.size();
    }

    /**
     * ViewHolder for guardian list items
     * Public because it is exposed via RecyclerView.Adapter generics
     */
    public static class GuardianViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView guardianName;
        private final TextView guardianDetails;
        private final TextView guardianPhone;

        public GuardianViewHolder(@NonNull View itemView) {
            super(itemView);

            guardianName = itemView.findViewById(R.id.guardian_name);
            guardianDetails = itemView.findViewById(R.id.guardian_details);
            guardianPhone = itemView.findViewById(R.id.guardian_phone);
        }

        void bind(Guardian guardian) {
            guardianName.setText(guardian.getName());

            guardianDetails.setText(
                    itemView.getContext().getString(
                            R.string.guardian_details_format,
                            guardian.getType(),
                            guardian.getRelation()
                    )
            );

            guardianPhone.setText(guardian.getPhone());
        }
    }
}