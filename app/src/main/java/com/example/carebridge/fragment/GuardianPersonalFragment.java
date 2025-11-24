package com.example.carebridge.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.carebridge.R;
import com.example.carebridge.shared.controller.GuardianController;
import com.example.carebridge.shared.model.GuardianInfo;
import com.facebook.shimmer.ShimmerFrameLayout;

public class GuardianPersonalFragment extends Fragment {

    private static final String TAG = "GuardianPersonalFragment";

    private TextView tvName, tvType, tvOccupation, tvAvailability, tvNotes;
    private TextView tvPhone, tvEmail, tvAddress;

    private ShimmerFrameLayout shimmerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    private View rootView;
    private View cardWarning;

    private GuardianController guardianController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_guardian_personal, container, false);
        bindViews(rootView);

        guardianController = new GuardianController(requireContext());

        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();

        swipeRefreshLayout.setOnRefreshListener(this::fetchGuardianData);

        rootView.post(this::fetchGuardianData);

        return rootView;
    }

    /** Initialize all view references */
    private void bindViews(View view) {
        shimmerLayout   = view.findViewById(R.id.shimmerLayout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        cardWarning     = view.findViewById(R.id.cardWarning);

        // Personal Info
        tvName          = view.findViewById(R.id.tvName);
        tvType          = view.findViewById(R.id.tvType);
        tvOccupation    = view.findViewById(R.id.tvOccupation);
        tvAvailability  = view.findViewById(R.id.tvAvailability);
        tvNotes         = view.findViewById(R.id.tvNotes);

        // Contact Info
        tvPhone         = view.findViewById(R.id.tvPhone);
        tvEmail         = view.findViewById(R.id.tvEmail);
        tvAddress       = view.findViewById(R.id.tvAddress);
    }

    /** Fetch guardian data */
    private void fetchGuardianData() {
        if (rootView == null) return;

        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        rootView.findViewById(R.id.cardContent).setVisibility(View.GONE);
        cardWarning.setVisibility(View.GONE);

        guardianController.getCurrentGuardian(new GuardianController.GuardianCallback() {
            @Override
            public void onSuccess(GuardianInfo guardianInfo) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    if (guardianInfo == null) {
                        showWarning(getString(R.string.no_guardian_data_found));
                    } else {
                        displayGuardianInfo(guardianInfo);
                        rootView.findViewById(R.id.cardContent).setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    showWarning(getString(R.string.network_error_retry_message));
                    Log.e(TAG, message);
                    Toast.makeText(requireContext(),
                            getString(R.string.guardian_fetch_failed_prefix) + message,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /** Show warning card */
    private void showWarning(String message) {
        cardWarning.setVisibility(View.VISIBLE);
        TextView msg = cardWarning.findViewById(R.id.tvWarningMessage);
        msg.setText(message);
    }

    /** Populate UI: ALL LABELS REMOVED */
    private void displayGuardianInfo(GuardianInfo guardianInfo) {
        tvName.setText(safeString(guardianInfo.getFull_name()));
        tvType.setText(safeString(guardianInfo.getType()));
        tvOccupation.setText(safeString(guardianInfo.getOccupation()));
        tvAvailability.setText(safeString(guardianInfo.getAvailability()));
        tvNotes.setText(safeString(guardianInfo.getNotes()));

        tvPhone.setText(safeString(guardianInfo.getPhone()));
        tvEmail.setText(safeString(guardianInfo.getEmail()));
        tvAddress.setText(safeString(guardianInfo.getAddress()));
    }

    /** Safe string utility */
    private String safeString(String value) {
        return (value != null && !value.trim().isEmpty())
                ? value
                : getString(R.string.not_available_text);
    }
}
