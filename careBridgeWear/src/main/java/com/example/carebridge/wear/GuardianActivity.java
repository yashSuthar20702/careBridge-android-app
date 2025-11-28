package com.example.carebridge.wear;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carebridge.shared.controller.PatientGuardianInfoController;
import com.example.carebridge.shared.model.PatientGuardianInfo;
import com.example.carebridge.wear.adapters.GuardianAdapter;
import com.example.carebridge.wear.databinding.ActivityGuardianBinding;
import com.example.carebridge.wear.models.Guardian;
import com.example.carebridge.wear.utils.WearSharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class GuardianActivity extends AppCompatActivity {

    private static final String TAG = "GuardianActivity";

    private ActivityGuardianBinding binding;
    private List<Guardian> guardianList;
    private GuardianAdapter adapter;
    private PatientGuardianInfoController guardianController;
    private WearSharedPrefManager wearSharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuardianBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize controllers and managers
        guardianController = new PatientGuardianInfoController(this);
        wearSharedPrefManager = new WearSharedPrefManager(this);

        setupRecyclerView();
        setupClickListeners();
        fetchGuardiansData();
    }

    private void setupRecyclerView() {
        guardianList = new ArrayList<>();
        adapter = new GuardianAdapter(guardianList);
        binding.guardianRecyclerView.setAdapter(adapter);
        binding.guardianRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Show loading state initially
        showLoadingState();
    }

    private void setupClickListeners() {
        binding.guardianBackButton.setOnClickListener(v -> finish());
    }

    private void fetchGuardiansData() {
        Log.d(TAG, "Starting to fetch guardians data for GuardianActivity...");

        guardianController.getCurrentGuardian(new PatientGuardianInfoController.PatientGuardianCallback() {
            @Override
            public void onSuccess(List<PatientGuardianInfo> patientGuardianList) {
                Log.d(TAG, "Guardians data fetched successfully. Count: " + patientGuardianList.size());

                // Convert PatientGuardianInfo to Guardian list
                convertToGuardianList(patientGuardianList);

                // Update UI on main thread
                runOnUiThread(() -> {
                    hideLoadingState();
                    adapter.notifyDataSetChanged();

                    // Show empty state if no guardians
                    if (guardianList.isEmpty()) {
                        showEmptyState();
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "Failed to fetch guardians data: " + message);

                // Update UI on main thread
                runOnUiThread(() -> {
                    hideLoadingState();
                    showErrorState(message);
                    // Fallback to sample data if API fails
                    initializeSampleData();
                });
            }
        });
    }

    private void convertToGuardianList(List<PatientGuardianInfo> patientGuardianList) {
        guardianList.clear();

        for (PatientGuardianInfo patientGuardian : patientGuardianList) {
            // Map PatientGuardianInfo to your Guardian model
            Guardian guardian = new Guardian(
                    patientGuardian.getFull_name(),
                    patientGuardian.getType(),
                    patientGuardian.getRole(),
                    patientGuardian.getPhone()
            );
            guardianList.add(guardian);
        }

        // If no data from API, ensure list is empty
        if (guardianList.isEmpty()) {
            Log.d(TAG, "No guardians found in API response");
        }
    }

    private void initializeSampleData() {
        // Fallback to sample data if API fails
        guardianList.clear();
        guardianList.add(new Guardian("Yash", "Family", "Friend", "+1 519-569-2560"));
        guardianList.add(new Guardian("Dhwani", "Caretaker", "Primary Nurse", "+1 519-568-2540"));
        adapter.notifyDataSetChanged();
    }

    private void showLoadingState() {
        runOnUiThread(() -> {
            binding.guardianRecyclerView.setVisibility(View.GONE);
            // You can add a progress bar to your layout if needed
        });
    }

    private void hideLoadingState() {
        runOnUiThread(() -> {
            binding.guardianRecyclerView.setVisibility(View.VISIBLE);
        });
    }

    private void showEmptyState() {
        Log.d(TAG, "No guardians available to display");
        // You can add an empty state view to your layout
    }

    private void showErrorState(String message) {
        Log.e(TAG, "Error state: " + message);
        // You can add an error message view to your layout
    }
}