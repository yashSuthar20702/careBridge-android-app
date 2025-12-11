package com.example.carebridge.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.carebridge.shared.controller.AssignedPatientController;
import com.example.carebridge.shared.model.AssignedPatientInfo;
import com.example.carebridge.shared.utils.SharedPrefManager;
import com.example.carebridge.view.AddMealActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * GuardianPatientsFragment
 * ------------------------
 * Displays the list of patients assigned to a guardian.
 * Includes summary cards, shimmer loading animation, and network error handling.
 */
public class GuardianPatientsFragment extends Fragment {

    // --- UI Elements ---
    private ShimmerFrameLayout shimmerFrameLayout;
    private TextView tvAssignedCount, tvActiveCount, tvWarningMessage;
    private LinearLayout patientsListContainer, summaryCards, patientsSection;
    private MaterialCardView cardWarning;
    private SwipeRefreshLayout swipeRefreshLayout;

    // --- Misc ---
    private int[] borderColors;
    private boolean isViewCreated = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guardian_patients, container, false);

        // Initialize UI components
        shimmerFrameLayout = view.findViewById(R.id.shimmer);
        tvAssignedCount = view.findViewById(R.id.tvAssignedCount);
        tvActiveCount = view.findViewById(R.id.tvActiveCount);
        patientsListContainer = view.findViewById(R.id.patientsListContainer);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        cardWarning = view.findViewById(R.id.cardWarning);
        tvWarningMessage = view.findViewById(R.id.tvWarningMessage);
        summaryCards = view.findViewById(R.id.summaryCards);
        patientsSection = view.findViewById(R.id.patientsSection);

        // Accent border colors for patient cards
        borderColors = new int[]{
                ContextCompat.getColor(requireContext(), R.color.accent_blue),
                ContextCompat.getColor(requireContext(), R.color.accent_purple),
                ContextCompat.getColor(requireContext(), R.color.accent_orange)
        };

        // Enable pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        // Show shimmer while loading initial data
        showLoading();

        // Small delay ensures layout is drawn before loading data
        new Handler().postDelayed(this::loadAssignedPatients, 200);

        isViewCreated = true;
        return view;
    }

    /** Called on swipe-to-refresh gesture. */
    private void refreshData() {
        showLoading();
        loadAssignedPatients();
    }

    /** Display shimmer loading animation and hide all data views. */
    private void showLoading() {
        if (!isAdded()) return;

        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();

        summaryCards.setVisibility(View.GONE);
        patientsSection.setVisibility(View.GONE);
        cardWarning.setVisibility(View.GONE);
    }

    /** Display loaded data and stop shimmer. */
    private void showData() {
        if (!isAdded()) return;

        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);

        cardWarning.setVisibility(View.GONE);
        summaryCards.setVisibility(View.VISIBLE);
        patientsSection.setVisibility(View.VISIBLE);
        patientsListContainer.setVisibility(View.VISIBLE);
    }

    /** Show a warning or error message (e.g., no patients or network error). */
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

    /**
     * Fetch assigned patients for the logged-in guardian.
     * Displays shimmer during load and updates UI after response.
     */
    private void loadAssignedPatients() {
        if (!isAdded() || getActivity() == null) return;

        showLoading();

        String guardianId = new SharedPrefManager(requireContext()).getReferenceId();
        AssignedPatientController controller = new AssignedPatientController();

        controller.getAssignedPatients(guardianId, new AssignedPatientController.AssignedPatientsCallback() {
            @Override
            public void onSuccess(List<AssignedPatientInfo> patients) {
                if (!isAdded() || getActivity() == null) return;

                requireActivity().runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (patients == null || patients.isEmpty()) {
                        showWarning(getString(R.string.no_patients_assigned_message));
                        return;
                    }

                    showData();

                    // --- Summary Stats ---
                    tvAssignedCount.setText(String.valueOf(patients.size()));
                    int activeCount = (int) patients.stream()
                            .filter(p -> "Active".equalsIgnoreCase(p.getStatus()))
                            .count();
                    tvActiveCount.setText(String.valueOf(activeCount));

                    // --- Populate patient cards ---
                    populatePatientCards(patients);
                });
            }

            @Override
            public void onFailure(String message) {
                if (!isAdded() || getActivity() == null) return;
                requireActivity().runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    showWarning(getString(R.string.patient_data_load_error));
                });
            }
        });
    }

    /** Dynamically inflates patient cards and displays them in the container. */
    private void populatePatientCards(List<AssignedPatientInfo> patients) {
        patientsListContainer.removeAllViews();
        Context context = getContext();
        if (context == null) return;

        for (int i = 0; i < patients.size(); i++) {
            AssignedPatientInfo patient = patients.get(i);

            // Inflate card layout
            View cardView = LayoutInflater.from(context)
                    .inflate(R.layout.patient_card, patientsListContainer, false);

            // Set card border color
            MaterialCardView cardMaterial = cardView.findViewById(R.id.patientCard);
            cardMaterial.setStrokeColor(borderColors[i % borderColors.length]);

            // Set patient name and style
            TextView tvName = cardView.findViewById(R.id.tvPatientName);
            tvName.setText(patient.getFull_name());
            tvName.setTypeface(Typeface.DEFAULT_BOLD);
            tvName.setTextSize(22);

            // Fill patient details
            ((TextView) cardView.findViewById(R.id.tvPatientRole)).setText(patient.getRole());
            TextView tvStatus = cardView.findViewById(R.id.tvPatientStatus);
            tvStatus.setText(patient.getStatus());

            // Apply active/inactive status style
            if ("Active".equalsIgnoreCase(patient.getStatus())) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_active);
            } else {
                tvStatus.setBackgroundResource(R.drawable.bg_status_inactive);
            }


            // Fill remaining info fields safely
            ((TextView) cardView.findViewById(R.id.tvPatientAge))
                    .setText(getString(R.string.white_space) + patient.getAge());
            ((TextView) cardView.findViewById(R.id.tvPatientGender))
                    .setText(getString(R.string.white_space) + safe(patient.getGender()));
            ((TextView) cardView.findViewById(R.id.tvPatientContact))
                    .setText(getString(R.string.white_space) + safe(patient.getContact_number()));
            ((TextView) cardView.findViewById(R.id.tvPatientEmail))
                    .setText(getString(R.string.white_space) + safe(patient.getEmail()));
            ((TextView) cardView.findViewById(R.id.tvPatientAssignedDate))
                    .setText(getString(R.string.white_space) + safe(patient.getAssigned_date()));
            ((TextView) cardView.findViewById(R.id.tvPatientNotes))
                    .setText(getString(R.string.white_space) + safe(patient.getNotes()));

            // ADD THIS: Add Meal button click listener
            MaterialButton btnAddMeal = cardView.findViewById(R.id.btnAddMeal);
            btnAddMeal.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AddMealActivity.class);
                intent.putExtra("PATIENT_ID", patient.getPatient_id());
                intent.putExtra("PATIENT_NAME", patient.getFull_name());
                startActivity(intent);
            });

            patientsListContainer.addView(cardView);
        }

        // Force UI redraw to fix delayed rendering issue
        patientsListContainer.invalidate();
        patientsListContainer.requestLayout();
    }

    /** Returns a safe, non-null string for display. */
    private String safe(String value) {
        return (value == null || value.trim().isEmpty())
                ? getString(R.string.not_available_text)
                : value;
    }
}