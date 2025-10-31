package com.example.carebridge.fragment;

import android.os.Bundle;
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
import com.example.carebridge.controller.PatientGuardianInfoController;
import com.example.carebridge.model.PatientGuardianInfo;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class PatientGuardianInfoFragment extends Fragment {

    private static final String TAG = "PatientGuardianInfoFragment";

    private ShimmerFrameLayout shimmerLayout;
    private TextView tvNoGuardianMessage, tvWarningMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MaterialCardView cardWarning;
    private PatientGuardianInformationAdapter adapter;
    private PatientGuardianInfoController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_guardian_info, container, false);

        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        tvNoGuardianMessage = view.findViewById(R.id.tvNoGuardianMessage);
        tvWarningMessage = view.findViewById(R.id.tvWarningMessage);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerViewGuardians);
        cardWarning = view.findViewById(R.id.cardWarning);

        controller = new PatientGuardianInfoController(requireContext());

        adapter = new PatientGuardianInformationAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::fetchGuardianData);

        showLoadingState();
        fetchGuardianData();

        return view;
    }

    private void showLoadingState() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        recyclerView.setVisibility(View.GONE);
        tvNoGuardianMessage.setVisibility(View.GONE);
        cardWarning.setVisibility(View.GONE);
    }

    private void fetchGuardianData() {
        if (!swipeRefreshLayout.isRefreshing()) showLoadingState();

        controller.getCurrentGuardian(new PatientGuardianInfoController.PatientGuardianCallback() {
            @Override
            public void onSuccess(List<PatientGuardianInfo> guardianList) {
                if (!isAdded() || getView() == null) return;

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                cardWarning.setVisibility(View.GONE);

                if (guardianList == null || guardianList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    tvNoGuardianMessage.setVisibility(View.VISIBLE);
                    tvNoGuardianMessage.setText("No guardian is assigned right now. Please contact your doctor to assign a guardian.");
                } else {
                    adapter.setData(guardianList);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoGuardianMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String message) {
                if (!isAdded() || getView() == null) return;

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                tvNoGuardianMessage.setVisibility(View.GONE);

                cardWarning.setVisibility(View.VISIBLE);
                tvWarningMessage.setText("Failed to load guardian info. Please check your internet connection and swipe down to retry.");

                Log.e(TAG, "[API ERROR] " + message);
            }
        });
    }
}
