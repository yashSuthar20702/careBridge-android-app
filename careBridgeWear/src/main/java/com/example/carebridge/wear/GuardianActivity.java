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
import com.example.carebridge.wear.utils.Constants;
import com.example.carebridge.wear.utils.WearSharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class GuardianActivity extends AppCompatActivity {

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

    /**
     * Set up RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        guardianList = new ArrayList<>();
        adapter = new GuardianAdapter(guardianList);
        binding.guardianRecyclerView.setAdapter(adapter);
        binding.guardianRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Show loading state initially
        showLoadingState();
    }

    /**
     * Set up click listeners for UI elements
     */
    private void setupClickListeners() {
        binding.guardianBackButton.setOnClickListener(v -> finish());
    }

    /**
     * Fetch guardians data from API
     */
    private void fetchGuardiansData() {
        Log.d(Constants.TAG_GUARDIAN_ACTIVITY,
                Constants.LOG_EMOJI_INFO + Constants.SPACE + Constants.LOG_MSG_GUARDIAN_FETCHING_STARTED);

        guardianController.getCurrentGuardian(new PatientGuardianInfoController.PatientGuardianCallback() {
            @Override
            public void onSuccess(List<PatientGuardianInfo> patientGuardianList) {
                Log.d(Constants.TAG_GUARDIAN_ACTIVITY,
                        Constants.LOG_EMOJI_SUCCESS + Constants.SPACE + Constants.LOG_MSG_GUARDIAN_FETCH_SUCCESS +
                                Constants.COLON + Constants.SPACE + patientGuardianList.size());

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
                Log.e(Constants.TAG_GUARDIAN_ACTIVITY,
                        Constants.LOG_EMOJI_ERROR + Constants.SPACE + Constants.LOG_MSG_GUARDIAN_FETCH_FAILED +
                                Constants.COLON + Constants.SPACE + message);

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

    /**
     * Convert PatientGuardianInfo objects to Guardian model objects
     */
    private void convertToGuardianList(List<PatientGuardianInfo> patientGuardianList) {
        guardianList.clear();

        for (PatientGuardianInfo patientGuardian : patientGuardianList) {
            // Map PatientGuardianInfo to Guardian model
            Guardian guardian = new Guardian(
                    patientGuardian.getFull_name(),
                    patientGuardian.getType(),
                    patientGuardian.getRole(),
                    patientGuardian.getPhone()
            );
            guardianList.add(guardian);
        }

        // Log if no data from API
        if (guardianList.isEmpty()) {
            Log.d(Constants.TAG_GUARDIAN_ACTIVITY,
                    Constants.LOG_EMOJI_INFO + Constants.SPACE + Constants.LOG_MSG_GUARDIAN_NO_DATA);
        }
    }

    /**
     * Initialize with sample data when API fails
     */
    private void initializeSampleData() {
        // Fallback to sample data if API fails
        guardianList.clear();
        guardianList.add(new Guardian(Constants.SAMPLE_NAME_YASH, Constants.SAMPLE_TYPE_FAMILY,
                Constants.SAMPLE_RELATION_FRIEND, Constants.SAMPLE_PHONE_YASH));
        guardianList.add(new Guardian(Constants.SAMPLE_NAME_DHWANI, Constants.SAMPLE_TYPE_CARETAKER,
                Constants.SAMPLE_RELATION_NURSE, Constants.SAMPLE_PHONE_DHWANI));
        adapter.notifyDataSetChanged();
    }

    /**
     * Show loading state while fetching data
     */
    private void showLoadingState() {
        runOnUiThread(() -> {
            binding.guardianRecyclerView.setVisibility(View.GONE);
            // You can add a progress bar to your layout if needed
        });
    }

    /**
     * Hide loading state when data is loaded
     */
    private void hideLoadingState() {
        runOnUiThread(() -> {
            binding.guardianRecyclerView.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Show empty state when no guardians available
     */
    private void showEmptyState() {
        Log.d(Constants.TAG_GUARDIAN_ACTIVITY,
                Constants.LOG_EMOJI_INFO + Constants.SPACE + Constants.LOG_MSG_GUARDIAN_EMPTY_STATE);
        // You can add an empty state view to your layout
    }

    /**
     * Show error state with message
     */
    private void showErrorState(String message) {
        Log.e(Constants.TAG_GUARDIAN_ACTIVITY,
                Constants.LOG_EMOJI_ERROR + Constants.SPACE + Constants.LOG_MSG_GUARDIAN_ERROR_STATE +
                        Constants.COLON + Constants.SPACE + message);
        // You can add an error message view to your layout
    }
}