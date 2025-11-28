package com.example.carebridge.wear;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carebridge.shared.controller.PatientGuardianInfoController;
import com.example.carebridge.shared.model.PatientGuardianInfo;
import com.example.carebridge.wear.adapters.GuardianCallAdapter;
import com.example.carebridge.wear.databinding.ActivityCallBinding;
import com.example.carebridge.wear.models.Guardian;
import com.example.carebridge.wear.utils.WearSharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class CallActivity extends AppCompatActivity implements GuardianCallAdapter.OnGuardianCallListener {

    private static final String TAG = "CallActivity";

    private ActivityCallBinding binding;
    private List<Guardian> guardianList;
    private GuardianCallAdapter adapter;
    private PatientGuardianInfoController guardianController;
    private WearSharedPrefManager wearSharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "=== CallActivity Created ===");

        // Initialize controllers and managers
        guardianController = new PatientGuardianInfoController(this);
        wearSharedPrefManager = new WearSharedPrefManager(this);

        setupClickListeners();
        setupRecyclerView();
        fetchGuardiansData();

        // Add test button for debugging
        setupTestButton();
    }

    private void setupClickListeners() {
        binding.callBackButton.setOnClickListener(v -> finish());
        binding.callEmergencyContact.setOnClickListener(v -> {
            Log.d(TAG, "Emergency contact clicked");
            makeCall("911");
        });
    }

    private void setupRecyclerView() {
        guardianList = new ArrayList<>();
        Log.d(TAG, "Setting up RecyclerView with adapter");
        adapter = new GuardianCallAdapter(guardianList, this);
        binding.guardiansRecyclerView.setAdapter(adapter);
        binding.guardiansRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Ensure RecyclerView is clickable
        binding.guardiansRecyclerView.setClickable(true);

        // Show loading state initially
        showLoadingState();

        Log.d(TAG, "RecyclerView setup complete. Adapter: " + (adapter != null));
    }

    private void setupTestButton() {
        // Temporary test button to verify basic functionality
        binding.callEmergencyContact.postDelayed(() -> {
            Log.d(TAG, "=== TEST: Adding temporary debug button ===");
            // This will help us verify if the issue is with RecyclerView or general click handling
        }, 1000);
    }

    private void fetchGuardiansData() {
        Log.d(TAG, "Starting to fetch guardians data...");

        guardianController.getCurrentGuardian(new PatientGuardianInfoController.PatientGuardianCallback() {
            @Override
            public void onSuccess(List<PatientGuardianInfo> patientGuardianList) {
                Log.d(TAG, "Guardians data fetched successfully. Count: " + patientGuardianList.size());

                // Convert PatientGuardianInfo to Guardian list
                convertToGuardianList(patientGuardianList);

                // Update UI on main thread
                runOnUiThread(() -> {
                    hideLoadingState();
                    int itemCount = adapter.getItemCount();
                    Log.d(TAG, "Notifying adapter. Item count: " + itemCount);
                    adapter.notifyDataSetChanged();

                    // Show empty state if no guardians
                    if (guardianList.isEmpty()) {
                        showEmptyState();
                    } else {
                        Log.d(TAG, "Guardians loaded: " + guardianList.size());
                        for (Guardian g : guardianList) {
                            Log.d(TAG, " - " + g.getName() + ": " + g.getPhone());
                        }
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
                    patientGuardian.getFull_name() != null ? patientGuardian.getFull_name() : "Unknown",
                    patientGuardian.getType() != null ? patientGuardian.getType() : "Guardian",
                    patientGuardian.getRole() != null ? patientGuardian.getRole() : "Caregiver",
                    patientGuardian.getPhone() != null ? patientGuardian.getPhone() : ""
            );
            guardianList.add(guardian);
            Log.d(TAG, "Converted guardian: " + guardian.getName() + " | " + guardian.getPhone());
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
        guardianList.add(new Guardian("Jasjit S", "Family", "Primary Guardian", "+1 519-573-0317"));
        guardianList.add(new Guardian("Dr. Smith", "Medical", "Primary Doctor", "+1 519-555-1234"));

        Log.d(TAG, "Initialized sample data with " + guardianList.size() + " guardians");
        adapter.notifyDataSetChanged();
    }

    private void showLoadingState() {
        runOnUiThread(() -> {
            binding.guardiansRecyclerView.setVisibility(View.GONE);
            Log.d(TAG, "Loading state shown");
        });
    }

    private void hideLoadingState() {
        runOnUiThread(() -> {
            binding.guardiansRecyclerView.setVisibility(View.VISIBLE);
            Log.d(TAG, "Loading state hidden");
        });
    }

    private void showEmptyState() {
        Log.d(TAG, "No guardians available to display");
        Toast.makeText(this, "No guardians found", Toast.LENGTH_SHORT).show();
    }

    private void showErrorState(String message) {
        Log.e(TAG, "Error state: " + message);
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGuardianCall(Guardian guardian) {
        Log.d(TAG, "=== onGuardianCall CALLED ===");
        Log.d(TAG, "Calling guardian: " + guardian.getName());
        Log.d(TAG, "Phone number: " + guardian.getPhone());

        if (guardian.getPhone() != null && !guardian.getPhone().isEmpty()) {
            makeCall(guardian.getPhone());
        } else {
            Log.e(TAG, "Cannot call - phone number is empty for guardian: " + guardian.getName());
            Toast.makeText(this, "No phone number available for " + guardian.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    private void makeCall(String phoneNumber) {
        try {
            Log.d(TAG, "Attempting to call: " + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
            Log.d(TAG, "Call intent started successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initiate call: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Failed to make call", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity resumed");
    }
}