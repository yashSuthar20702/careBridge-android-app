package com.example.carebridge.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.core.content.ContextCompat;

import com.example.carebridge.R;
import com.example.carebridge.controller.AssignedPatientController;
import com.example.carebridge.model.AssignedPatientInfo;
import com.example.carebridge.utils.SharedPrefManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class GuardianPatientsFragment extends Fragment {

    private ShimmerFrameLayout shimmerFrameLayout;
    private TextView tvAssignedCount, tvActiveCount, tvWarningMessage;
    private LinearLayout patientsListContainer, mainContainer, summaryCards, patientsSection;
    private MaterialCardView cardWarning;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int[] borderColors;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guardian_patients, container, false);

        // Initialize views
        shimmerFrameLayout = view.findViewById(R.id.shimmer);
        tvAssignedCount = view.findViewById(R.id.tvAssignedCount);
        tvActiveCount = view.findViewById(R.id.tvActiveCount);
        patientsListContainer = view.findViewById(R.id.patientsListContainer);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        cardWarning = view.findViewById(R.id.cardWarning);
        tvWarningMessage = view.findViewById(R.id.tvWarningMessage);
        mainContainer = view.findViewById(R.id.mainContainer);
        summaryCards = view.findViewById(R.id.summaryCards);
        patientsSection = view.findViewById(R.id.patientsSection);

        // Set color borders
        borderColors = new int[]{
                ContextCompat.getColor(requireContext(), R.color.accent_blue),
                ContextCompat.getColor(requireContext(), R.color.accent_purple),
                ContextCompat.getColor(requireContext(), R.color.accent_orange)
        };

        // Pull-to-refresh setup
        swipeRefreshLayout.setOnRefreshListener(this::loadAssignedPatients);

        // Start shimmer safely after view is created
        shimmerFrameLayout.post(this::showLoading);

        // Delay first data load to ensure Fragment is attached
        view.post(this::loadAssignedPatients);

        return view;
    }

    /** Show shimmer loader and hide everything else */
    private void showLoading() {
        if (!isAdded()) return;
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();

        summaryCards.setVisibility(View.GONE);
        patientsSection.setVisibility(View.GONE);
        cardWarning.setVisibility(View.GONE);
    }

    /** Show data section and hide shimmer/warning */
    private void showData() {
        if (!isAdded()) return;
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);

        cardWarning.setVisibility(View.GONE);
        summaryCards.setVisibility(View.VISIBLE);
        patientsSection.setVisibility(View.VISIBLE);
        patientsListContainer.setVisibility(View.VISIBLE);
    }

    /** Show warning card and hide all other sections */
    private void showWarning(String message) {
        if (!isAdded()) return;
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);

        summaryCards.setVisibility(View.GONE);
        patientsSection.setVisibility(View.GONE);
        patientsListContainer.setVisibility(View.GONE);

        cardWarning.setVisibility(View.VISIBLE);
        tvWarningMessage.setText(message);
    }

    /** Load patient data from API */
    private void loadAssignedPatients() {
        if (!isAdded() || getActivity() == null) return;

        String guardianId = new SharedPrefManager(requireContext()).getReferenceId();
        AssignedPatientController controller = new AssignedPatientController();

        controller.getAssignedPatients(guardianId, new AssignedPatientController.AssignedPatientsCallback() {
            @Override
            public void onSuccess(List<AssignedPatientInfo> patients) {
                if (!isAdded() || getActivity() == null) return;

                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;

                    swipeRefreshLayout.setRefreshing(false);

                    if (patients == null || patients.isEmpty()) {
                        showWarning("No patients are currently assigned to you.");
                        return;
                    }

                    showData();

                    tvAssignedCount.setText(String.valueOf(patients.size()));

                    int activeCount = 0;
                    for (AssignedPatientInfo p : patients) {
                        if ("Active".equalsIgnoreCase(p.getStatus())) activeCount++;
                    }
                    tvActiveCount.setText(String.valueOf(activeCount));

                    patientsListContainer.removeAllViews();
                    Context context = getContext();

                    for (int i = 0; i < patients.size(); i++) {
                        AssignedPatientInfo patient = patients.get(i);
                        View cardView = LayoutInflater.from(context).inflate(R.layout.patient_card, patientsListContainer, false);

                        MaterialCardView cardMaterial = cardView.findViewById(R.id.patientCard);
                        int borderColor = borderColors[i % borderColors.length];
                        cardMaterial.setStrokeColor(borderColor);

                        TextView tvName = cardView.findViewById(R.id.tvPatientName);
                        tvName.setText(patient.getFull_name());
                        tvName.setTypeface(Typeface.DEFAULT_BOLD);
                        tvName.setTextSize(22);

                        ((TextView) cardView.findViewById(R.id.tvPatientRole)).setText(patient.getRole());

                        TextView tvStatus = cardView.findViewById(R.id.tvPatientStatus);
                        tvStatus.setText(patient.getStatus());
                        if ("Active".equalsIgnoreCase(patient.getStatus())) {
                            tvStatus.setBackgroundResource(R.drawable.bg_status_active);
                        } else {
                            tvStatus.setBackgroundResource(R.drawable.status_inactive_bg);
                        }

                        ((TextView) cardView.findViewById(R.id.tvPatientAge)).setText(getString(R.string.white_space) + String.valueOf(patient.getAge()));
                        ((TextView) cardView.findViewById(R.id.tvPatientGender)).setText(getString(R.string.white_space) + safe(patient.getGender()));
                        ((TextView) cardView.findViewById(R.id.tvPatientContact)).setText(getString(R.string.white_space) + safe(patient.getContact_number()));
                        ((TextView) cardView.findViewById(R.id.tvPatientEmail)).setText(getString(R.string.white_space) + safe(patient.getEmail()));
                        ((TextView) cardView.findViewById(R.id.tvPatientAssignedDate)).setText(getString(R.string.white_space) + safe(patient.getAssigned_date()));
                        ((TextView) cardView.findViewById(R.id.tvPatientNotes)).setText(getString(R.string.white_space) + safe(patient.getNotes()));

                        patientsListContainer.addView(cardView);
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                if (!isAdded() || getActivity() == null) return;

                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;

                    swipeRefreshLayout.setRefreshing(false);
                    showWarning("Failed to load patient data. Please check your internet and swipe down to retry.");
                });
            }
        });
    }

    private String safe(String value) {
        return (value == null || value.trim().isEmpty()) ? "N/A" : value;
    }
}
