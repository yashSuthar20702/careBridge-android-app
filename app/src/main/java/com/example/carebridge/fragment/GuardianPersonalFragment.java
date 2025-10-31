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
    private View cardWarning; // new warning card view

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

    private void bindViews(View view) {
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        cardWarning = view.findViewById(R.id.cardWarning); // bind warning card

        // Personal Info
        tvName = view.findViewById(R.id.tvName);
        tvType = view.findViewById(R.id.tvType);
        tvOccupation = view.findViewById(R.id.tvOccupation);
        tvAvailability = view.findViewById(R.id.tvAvailability);
        tvNotes = view.findViewById(R.id.tvNotes);

        // Contact Info
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvAddress = view.findViewById(R.id.tvAddress);
    }

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
                        showWarning("No guardian data found.");
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

                    showWarning("Failed to fetch data Please check your network and swipe down to retry.");
                    Log.e(TAG, message);
                    Toast.makeText(requireContext(),
                            "Failed to fetch guardian info: " + message,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showWarning(String message) {
        cardWarning.setVisibility(View.VISIBLE);
        TextView tvWarningMessage = cardWarning.findViewById(R.id.tvWarningMessage);
        tvWarningMessage.setText(message);
    }

    private void displayGuardianInfo(GuardianInfo guardianInfo) {
        setBoldLabel(tvName, "Full Name:", safeString(guardianInfo.getFull_name()));
        setBoldLabel(tvType, "Type:", safeString(guardianInfo.getType()));
        setBoldLabel(tvOccupation, "Occupation:", safeString(guardianInfo.getOccupation()));
        setBoldLabel(tvAvailability, "Availability:", safeString(guardianInfo.getAvailability()));
        setBoldLabel(tvNotes, "Notes:", safeString(guardianInfo.getNotes()));

        setBoldLabel(tvPhone, "Phone:", safeString(guardianInfo.getPhone()));
        setBoldLabel(tvEmail, "Email:", safeString(guardianInfo.getEmail()));
        setBoldLabel(tvAddress, "Address:", safeString(guardianInfo.getAddress()));
    }

    private void setBoldLabel(TextView textView, String label, String value) {
        SpannableString spannable = new SpannableString(label + " " + value);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), 0);
        textView.setText(spannable);
    }

    private String safeString(String value) {
        return (value != null && !value.isEmpty()) ? value : "N/A";
    }
}
