package com.example.carebridge.wear;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carebridge.shared.controller.PatientController;
import com.example.carebridge.shared.model.PatientInfo;
import com.example.carebridge.wear.adapters.HealthInfoAdapter;
import com.example.carebridge.wear.databinding.ActivityHealthInfoBinding;
import com.example.carebridge.wear.models.HealthInfo;
import com.example.carebridge.wear.utils.WearSharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class HealthInfoActivity extends AppCompatActivity {

    private static final String TAG = "HealthInfoActivity";

    private ActivityHealthInfoBinding binding;
    private List<HealthInfo> healthInfoList;
    private HealthInfoAdapter adapter;
    private PatientController patientController;
    private WearSharedPrefManager wearSharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize controllers and managers
        patientController = new PatientController(this);
        wearSharedPrefManager = new WearSharedPrefManager(this);

        setupRecyclerView();
        setupClickListeners();
        fetchPatientData();
    }

    private void setupRecyclerView() {
        healthInfoList = new ArrayList<>();
        adapter = new HealthInfoAdapter(healthInfoList);
        binding.healthInfoRecyclerView.setAdapter(adapter);
        binding.healthInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Show loading state initially
        showLoadingState();
    }

    private void setupClickListeners() {
        binding.healthInfoBackButton.setOnClickListener(v -> finish());
    }

    private void fetchPatientData() {
        Log.d(TAG, "Starting to fetch patient data...");

        // Show loading state
        showLoadingState();

        patientController.getCurrentPatient(new PatientController.PatientCallback() {
            @Override
            public void onSuccess(PatientInfo patientInfo) {
                Log.d(TAG, "Patient data fetched successfully: " + patientInfo.getFullName());

                // Convert PatientInfo to HealthInfo list
                convertPatientInfoToHealthInfo(patientInfo);

                // Update UI on main thread
                runOnUiThread(() -> {
                    hideLoadingState();
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "Failed to fetch patient data: " + message);

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

    private void convertPatientInfoToHealthInfo(PatientInfo patientInfo) {
        healthInfoList.clear();

        // Add basic patient information
        if (patientInfo.getFullName() != null && !patientInfo.getFullName().isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(R.string.label_name), patientInfo.getFullName(), R.drawable.ic_user));
        }

        if (patientInfo.getBloodGroup() != null && !patientInfo.getBloodGroup().isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(R.string.label_blood_group), patientInfo.getBloodGroup(), R.drawable.ic_droplet));
        }

        // Calculate age from DOB if available
        if (patientInfo.getDob() != null && !patientInfo.getDob().isEmpty()) {
            String age = calculateAgeFromDOB(patientInfo.getDob());
            healthInfoList.add(new HealthInfo(getString(R.string.label_age), age, R.drawable.ic_calendar));
        }

        if (patientInfo.getGender() != null && !patientInfo.getGender().isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(R.string.label_gender), patientInfo.getGender(), R.drawable.ic_user));
        }

        if (patientInfo.getAddress() != null && !patientInfo.getAddress().isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(R.string.label_address), patientInfo.getAddress(), R.drawable.ic_location));
        }

        if (patientInfo.getContactNumber() != null && !patientInfo.getContactNumber().isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(R.string.label_contact), patientInfo.getContactNumber(), R.drawable.ic_phone));
        }

        if (patientInfo.getEmail() != null && !patientInfo.getEmail().isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(R.string.label_email), patientInfo.getEmail(), R.drawable.ic_mail));
        }

        // Add medical information
        if (patientInfo.getHeightCm() != null && !patientInfo.getHeightCm().isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(R.string.label_height), patientInfo.getHeightCm() + " " + getString(R.string.unit_cm), R.drawable.ic_activity));
        }

        if (patientInfo.getWeightKg() != null && !patientInfo.getWeightKg().isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(R.string.label_weight), patientInfo.getWeightKg() + " " + getString(R.string.unit_kg), R.drawable.ic_activity));
        }

        // Add allergies if available
        if (patientInfo.getAllergies() != null && !patientInfo.getAllergies().isEmpty()) {
            String allergies = String.join(", ", patientInfo.getAllergies());
            healthInfoList.add(new HealthInfo(getString(R.string.label_allergies), allergies, R.drawable.ic_alert));
        }

        // Add medical conditions if available
        if (patientInfo.getMedicalConditions() != null && !patientInfo.getMedicalConditions().isEmpty()) {
            String conditions = String.join(", ", patientInfo.getMedicalConditions());
            healthInfoList.add(new HealthInfo(getString(R.string.label_medical_conditions), conditions, R.drawable.ic_heart));
        }

        // Add past surgeries if available
        if (patientInfo.getPastSurgeries() != null && !patientInfo.getPastSurgeries().isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(R.string.label_past_surgeries), patientInfo.getPastSurgeries(), R.drawable.ic_activity));
        }

        // Add current symptoms if available
        if (patientInfo.getCurrentSymptoms() != null && !patientInfo.getCurrentSymptoms().isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(R.string.label_current_symptoms), patientInfo.getCurrentSymptoms(), R.drawable.ic_alert));
        }

        // If no data was added, show a message
        if (healthInfoList.isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(R.string.label_no_data), getString(R.string.patient_info_not_available), R.drawable.ic_user));
        }
    }

    private String calculateAgeFromDOB(String dob) {
        try {
            // Simple age calculation - you might want to implement proper date parsing
            // This is a basic implementation
            if (dob.length() >= 4) {
                String yearStr = dob.substring(0, 4);
                int birthYear = Integer.parseInt(yearStr);
                int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
                int age = currentYear - birthYear;
                return age + " " + getString(R.string.unit_years);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating age from DOB: " + dob, e);
        }
        return getString(R.string.unknown);
    }

    private void showLoadingState() {
        runOnUiThread(() -> {
            binding.healthInfoRecyclerView.setVisibility(View.GONE);
            // Add a progress bar to your layout or show a loading indicator
            // For now, we'll just keep the recyclerview hidden
        });
    }

    private void hideLoadingState() {
        runOnUiThread(() -> {
            binding.healthInfoRecyclerView.setVisibility(View.VISIBLE);
        });
    }

    private void showErrorState(String message) {
        // You can add an error message view to your layout
        Log.e(TAG, "Error state: " + message);
        // For now, we'll just log the error
    }

    private void initializeSampleData() {
        // Fallback to sample data if API fails
        healthInfoList.clear();
        healthInfoList.add(new HealthInfo(getString(R.string.label_name), "Yash", R.drawable.ic_user));
        healthInfoList.add(new HealthInfo(getString(R.string.label_blood_group), "B+", R.drawable.ic_droplet));
        healthInfoList.add(new HealthInfo(getString(R.string.label_age), "29 " + getString(R.string.unit_years), R.drawable.ic_calendar));
        healthInfoList.add(new HealthInfo(getString(R.string.label_address), "123 Oak Street, Springfield", R.drawable.ic_location));
        adapter.notifyDataSetChanged();
    }
}