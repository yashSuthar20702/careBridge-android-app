package com.example.carebridge.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.carebridge.R;
import com.example.carebridge.adapters.PatientGuardianInformationAdapter;
import com.example.carebridge.shared.controller.PatientGuardianInfoController;
import com.example.carebridge.shared.model.PatientGuardianInfo;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display patient’s guardian information.
 * Handles data loading, shimmer animations, error handling, and pull-to-refresh.
 */
public class PatientGuardianInfoFragment extends Fragment {

    private static final String TAG = "PatientGuardianInfoFragment";

    // UI Components
    private ShimmerFrameLayout shimmerLayout;
    private TextView tvNoGuardianMessage, tvWarningMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MaterialCardView cardWarning;

    // Helpers
    private PatientGuardianInformationAdapter adapter;
    private PatientGuardianInfoController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_guardian_info, container, false);

        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();

        // Initialize controller for API calls
        controller = new PatientGuardianInfoController(requireContext());

        // Show shimmer immediately when fragment loads
        showLoadingState();

        // Small delay ensures smooth UI transition before data fetch
        new Handler().postDelayed(this::fetchGuardianData, 200);

        return view;
    }

    /** Initialize all UI components */
    private void initViews(View view) {
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        tvNoGuardianMessage = view.findViewById(R.id.tvNoGuardianMessage);
        tvWarningMessage = view.findViewById(R.id.tvWarningMessage);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerViewGuardians);
        cardWarning = view.findViewById(R.id.cardWarning);
    }

    /** Setup RecyclerView with empty adapter initially */
    private void setupRecyclerView() {
        adapter = new PatientGuardianInformationAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    /** Setup pull-to-refresh listener */
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
    }

    /** Triggered on pull-to-refresh gesture */
    private void refreshData() {
        showLoadingState();
        fetchGuardianData();
    }

    /** Show shimmer loading animation and hide other views */
    private void showLoadingState() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();

        recyclerView.setVisibility(View.GONE);
        tvNoGuardianMessage.setVisibility(View.GONE);
        cardWarning.setVisibility(View.GONE);
    }

    /** Stop shimmer and restore UI to idle state */
    private void stopLoadingState() {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    /** Fetch guardian data from the API */
    private void fetchGuardianData() {
        if (!isAdded()) return; // Ensure fragment is still active

        if (!swipeRefreshLayout.isRefreshing()) showLoadingState();

        controller.getCurrentGuardian(new PatientGuardianInfoController.PatientGuardianCallback() {
            @Override
            public void onSuccess(List<PatientGuardianInfo> guardianList) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    stopLoadingState();
                    cardWarning.setVisibility(View.GONE);

                    if (guardianList == null || guardianList.isEmpty()) {
                        // No guardians found
                        recyclerView.setVisibility(View.GONE);
                        tvNoGuardianMessage.setVisibility(View.VISIBLE);
                        tvNoGuardianMessage.setText(getString(R.string.no_guardian_assigned_message));
                    } else {
                        // Update RecyclerView with data
                        adapter.setData(guardianList);
                        recyclerView.setVisibility(View.VISIBLE);
                        tvNoGuardianMessage.setVisibility(View.GONE);
                    }

                    // Force UI refresh to ensure smooth rendering
                    recyclerView.invalidate();
                });
            }

            @Override
            public void onFailure(String message) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    stopLoadingState();

                    recyclerView.setVisibility(View.GONE);
                    tvNoGuardianMessage.setVisibility(View.GONE);

                    // Show warning card with error message
                    cardWarning.setVisibility(View.VISIBLE);
                    tvWarningMessage.setText(getString(R.string.guardian_load_error_message));

                    Log.e(TAG, "[API ERROR] " + message);
                });
            }
        });
    }
}
