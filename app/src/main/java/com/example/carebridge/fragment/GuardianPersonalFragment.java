package com.example.carebridge.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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
import com.example.carebridge.controller.GuardianController;
import com.example.carebridge.model.GuardianInfo;
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
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        cardWarning = view.findViewById(R.id.cardWarning);

        // Personal Info fields
        tvName = view.findViewById(R.id.tvName);
        tvType = view.findViewById(R.id.tvType);
        tvOccupation = view.findViewById(R.id.tvOccupation);
        tvAvailability = view.findViewById(R.id.tvAvailability);
        tvNotes = view.findViewById(R.id.tvNotes);

        // Contact Info fields
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvAddress = view.findViewById(R.id.tvAddress);
    }

    /** Fetch guardian data from API with loading states */
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

                getActivity().runOnUiThread(() -> {
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

                getActivity().runOnUiThread(() -> {
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

    /** Display warning message for errors */
    private void showWarning(String message) {
        cardWarning.setVisibility(View.VISIBLE);
        TextView tvWarningMessage = cardWarning.findViewById(R.id.tvWarningMessage);
        tvWarningMessage.setText(message);
    }

    /** Populate UI with guardian information */
    private void displayGuardianInfo(GuardianInfo guardianInfo) {
        setBoldLabel(tvName, getString(R.string.full_name_label), safeString(guardianInfo.getFull_name()));
        setBoldLabel(tvType, getString(R.string.type_label), safeString(guardianInfo.getType()));
        setBoldLabel(tvOccupation, getString(R.string.occupation_label), safeString(guardianInfo.getOccupation()));
        setBoldLabel(tvAvailability, getString(R.string.availability_label), safeString(guardianInfo.getAvailability()));
        setBoldLabel(tvNotes, getString(R.string.notes_label), safeString(guardianInfo.getNotes()));

        setBoldLabel(tvPhone, getString(R.string.phone_label), safeString(guardianInfo.getPhone()));
        setBoldLabel(tvEmail, getString(R.string.email_label), safeString(guardianInfo.getEmail()));
        setBoldLabel(tvAddress, getString(R.string.address_label), safeString(guardianInfo.getAddress()));
    }

    /** Apply bold styling to label text */
    private void setBoldLabel(TextView textView, String label, String value) {
        SpannableString spannable = new SpannableString(label + " " + value);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), 0);
        textView.setText(spannable);
    }

    /** Handle null or empty string values */
    private String safeString(String value) {
        return (value != null && !value.isEmpty()) ? value : getString(R.string.not_available_text);
    }
}