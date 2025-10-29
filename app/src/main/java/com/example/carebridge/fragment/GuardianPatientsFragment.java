package com.example.carebridge.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;
import com.example.carebridge.R;
import com.example.carebridge.controller.AssignedPatientController;
import com.example.carebridge.model.AssignedPatientInfo;
import com.example.carebridge.utils.SharedPrefManager;

import java.util.List;

public class GuardianPatientsFragment extends Fragment {
    private ShimmerFrameLayout shimmerFrameLayout;
    private TextView tvAssignedCount, tvActiveCount;
    private LinearLayout patientsListContainer;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int[] borderColors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guardian_patients, container, false);

        shimmerFrameLayout = view.findViewById(R.id.shimmer);
        tvAssignedCount = view.findViewById(R.id.tvAssignedCount);
        tvActiveCount = view.findViewById(R.id.tvActiveCount);
        patientsListContainer = view.findViewById(R.id.patientsListContainer);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        borderColors = new int[]{
                ContextCompat.getColor(requireContext(), R.color.accent_blue),
                ContextCompat.getColor(requireContext(), R.color.accent_purple),
                ContextCompat.getColor(requireContext(), R.color.accent_orange)
        };

        loadAssignedPatients();
        swipeRefreshLayout.setOnRefreshListener(this::loadAssignedPatients);

        return view;
    }

    private void loadAssignedPatients() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        swipeRefreshLayout.setRefreshing(false);

        String guardianId = new SharedPrefManager(requireContext()).getReferenceId();

        AssignedPatientController controller = new AssignedPatientController();
        controller.getAssignedPatients(guardianId, new AssignedPatientController.AssignedPatientsCallback() {
            @Override
            public void onSuccess(List<AssignedPatientInfo> patients) {
                requireActivity().runOnUiThread(() -> {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

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

                        // Set alternate border colors
                        MaterialCardView cardMaterial = cardView.findViewById(R.id.patientCard);
                        int borderColor = borderColors[i % borderColors.length];
                        cardMaterial.setStrokeColor(borderColor);

                        // Only Name/Title: Bold and big!
                        TextView tvName = cardView.findViewById(R.id.tvPatientName);
                        tvName.setText(patient.getFull_name());
                        tvName.setTypeface(Typeface.DEFAULT_BOLD);
                        tvName.setTextSize(22);  // Make it larger

                        // No need to set font/bold for other info, XML handles it!
                        ((TextView) cardView.findViewById(R.id.tvPatientRole)).setText(patient.getRole());
                        TextView tvStatus = cardView.findViewById(R.id.tvPatientStatus);
                        tvStatus.setText(patient.getStatus());
                        if ("Active".equalsIgnoreCase(patient.getStatus())) {
                            tvStatus.setBackgroundResource(R.drawable.bg_status_active);
                        } else {
                            tvStatus.setBackgroundResource(R.drawable.status_inactive_bg);
                        }

                        ((TextView) cardView.findViewById(R.id.tvPatientAge)).setText(String.valueOf(patient.getAge()));
                        ((TextView) cardView.findViewById(R.id.tvPatientGender)).setText(safe(patient.getGender()));
                        ((TextView) cardView.findViewById(R.id.tvPatientContact)).setText(safe(patient.getContact_number()));
                        ((TextView) cardView.findViewById(R.id.tvPatientEmail)).setText(safe(patient.getEmail()));
                        ((TextView) cardView.findViewById(R.id.tvPatientAssignedDate)).setText(safe(patient.getAssigned_date()));
                        ((TextView) cardView.findViewById(R.id.tvPatientNotes)).setText(safe(patient.getNotes()));

                        patientsListContainer.addView(cardView);
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                requireActivity().runOnUiThread(() -> {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    patientsListContainer.removeAllViews();
                    TextView errorView = new TextView(getContext());
                    errorView.setText(message);
                    errorView.setTextColor(getResources().getColor(R.color.error_red));
                    patientsListContainer.addView(errorView);
                });
            }
        });
    }

    private String safe(String value) {
        return (value == null || value.trim().isEmpty()) ? "N/A" : value;
    }
}
