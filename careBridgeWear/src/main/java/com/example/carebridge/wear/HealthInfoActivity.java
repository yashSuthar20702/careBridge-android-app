package com.example.carebridge.wear;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carebridge.shared.controller.PatientController;
import com.example.carebridge.shared.model.PatientInfo;
import com.example.carebridge.wear.adapters.HealthInfoAdapter;
import com.example.carebridge.wear.databinding.ActivityHealthInfoBinding;
import com.example.carebridge.wear.models.HealthInfo;
import com.example.carebridge.wear.utils.Constants;
import com.example.carebridge.wear.utils.WearSharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class HealthInfoActivity extends AppCompatActivity {

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

    /**
     * Set up RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        healthInfoList = new ArrayList<>();
        adapter = new HealthInfoAdapter(healthInfoList);
        binding.healthInfoRecyclerView.setAdapter(adapter);
        binding.healthInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Show loading state initially
        showLoadingState();
    }

    /**
     * Set up click listeners for UI elements
     */
    private void setupClickListeners() {
        binding.healthInfoBackButton.setOnClickListener(v -> finish());
    }

    /**
     * Fetch patient data from API
     */
    private void fetchPatientData() {
        Log.d(Constants.TAG_HEALTH_INFO_ACTIVITY,
                Constants.LOG_EMOJI_INFO + Constants.SPACE + Constants.LOG_MSG_HEALTH_INFO_FETCHING);

        // Show loading state
        showLoadingState();

        patientController.getCurrentPatient(new PatientController.PatientCallback() {
            @Override
            public void onSuccess(PatientInfo patientInfo) {
                Log.d(Constants.TAG_HEALTH_INFO_ACTIVITY,
                        Constants.LOG_EMOJI_SUCCESS + Constants.SPACE + Constants.LOG_MSG_HEALTH_INFO_FETCH_SUCCESS +
                                Constants.COLON + Constants.SPACE + patientInfo.getFullName());

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
                Log.e(Constants.TAG_HEALTH_INFO_ACTIVITY,
                        Constants.LOG_EMOJI_ERROR + Constants.SPACE + Constants.LOG_MSG_HEALTH_INFO_FETCH_FAILED +
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
     * Convert PatientInfo to HealthInfo list
     */
    private void convertPatientInfoToHealthInfo(PatientInfo patientInfo) {
        healthInfoList.clear();

        // Add basic patient information
        addPatientInfoIfNotEmpty(patientInfo.getFullName(), R.string.label_name, R.drawable.ic_user);
        addPatientInfoIfNotEmpty(patientInfo.getBloodGroup(), R.string.label_blood_group, R.drawable.ic_droplet);

        // Calculate age from DOB if available
        if (patientInfo.getDob() != null && !patientInfo.getDob().isEmpty()) {
            String age = calculateAgeFromDOB(patientInfo.getDob());
            healthInfoList.add(new HealthInfo(getString(R.string.label_age), age, R.drawable.ic_calendar));
        }

        addPatientInfoIfNotEmpty(patientInfo.getGender(), R.string.label_gender, R.drawable.ic_user);
        addPatientInfoIfNotEmpty(patientInfo.getAddress(), R.string.label_address, R.drawable.ic_location);
        addPatientInfoIfNotEmpty(patientInfo.getContactNumber(), R.string.label_contact, R.drawable.ic_phone);
        addPatientInfoIfNotEmpty(patientInfo.getEmail(), R.string.label_email, R.drawable.ic_mail);

        // Add medical information with units
        addMedicalInfoWithUnit(patientInfo.getHeightCm(), R.string.label_height, R.string.unit_cm, R.drawable.ic_activity);
        addMedicalInfoWithUnit(patientInfo.getWeightKg(), R.string.label_weight, R.string.unit_kg, R.drawable.ic_activity);

        // Add collections if available
        addCollectionInfo(patientInfo.getAllergies(), R.string.label_allergies, R.drawable.ic_alert);
        addCollectionInfo(patientInfo.getMedicalConditions(), R.string.label_medical_conditions, R.drawable.ic_heart);

        // Add other medical information
        addPatientInfoIfNotEmpty(patientInfo.getPastSurgeries(), R.string.label_past_surgeries, R.drawable.ic_activity);
        addPatientInfoIfNotEmpty(patientInfo.getCurrentSymptoms(), R.string.label_current_symptoms, R.drawable.ic_alert);

        // If no data was added, show a message
        if (healthInfoList.isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(R.string.label_no_data),
                    getString(R.string.patient_info_not_available),
                    R.drawable.ic_user));
        }
    }

    /**
     * Add patient information if not empty
     */
    private void addPatientInfoIfNotEmpty(String value, int labelResId, int iconResId) {
        if (value != null && !value.isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(labelResId), value, iconResId));
        }
    }

    /**
     * Add medical information with unit
     */
    private void addMedicalInfoWithUnit(String value, int labelResId, int unitResId, int iconResId) {
        if (value != null && !value.isEmpty()) {
            String formattedValue = value + Constants.SPACE + getString(unitResId);
            healthInfoList.add(new HealthInfo(getString(labelResId), formattedValue, iconResId));
        }
    }

    /**
     * Add collection information (allergies, conditions)
     */
    private void addCollectionInfo(List<String> items, int labelResId, int iconResId) {
        if (items != null && !items.isEmpty()) {
            String joinedItems = String.join(Constants.COMMA_SPACE, items);
            healthInfoList.add(new HealthInfo(getString(labelResId), joinedItems, iconResId));
        }
    }

    /**
     * Calculate age from date of birth
     */
    private String calculateAgeFromDOB(String dob) {
        try {
            // Simple age calculation - extract year from YYYY-MM-DD format
            if (dob.length() >= Constants.DOB_YEAR_LENGTH) {
                String yearStr = dob.substring(Constants.POSITION_FIRST, Constants.DOB_YEAR_LENGTH);
                int birthYear = Integer.parseInt(yearStr);
                int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
                int age = currentYear - birthYear;
                return age + Constants.SPACE + getString(R.string.unit_years);
            }
        } catch (Exception e) {
            Log.e(Constants.TAG_HEALTH_INFO_ACTIVITY,
                    Constants.LOG_EMOJI_ERROR + Constants.SPACE + Constants.LOG_MSG_HEALTH_INFO_AGE_CALC_ERROR +
                            Constants.COLON + Constants.SPACE + dob, e);
        }
        return getString(R.string.unknown);
    }

    /**
     * Show loading state while fetching data
     */
    private void showLoadingState() {
        runOnUiThread(() -> {
            binding.healthInfoRecyclerView.setVisibility(View.GONE);
            // Add a progress bar to your layout or show a loading indicator
        });
    }

    /**
     * Hide loading state when data is loaded
     */
    private void hideLoadingState() {
        runOnUiThread(() -> {
            binding.healthInfoRecyclerView.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Show error state with message
     */
    private void showErrorState(String message) {
        Log.e(Constants.TAG_HEALTH_INFO_ACTIVITY,
                Constants.LOG_EMOJI_ERROR + Constants.SPACE + Constants.LOG_MSG_HEALTH_INFO_ERROR_STATE +
                        Constants.COLON + Constants.SPACE + message);
        // You can add an error message view to your layout
    }

    /**
     * Initialize with sample data when API fails
     */
    private void initializeSampleData() {
        // Fallback to sample data if API fails
        healthInfoList.clear();
        healthInfoList.add(new HealthInfo(getString(R.string.label_name),
                Constants.SAMPLE_NAME_YASH,
                R.drawable.ic_user));
        healthInfoList.add(new HealthInfo(getString(R.string.label_blood_group),
                Constants.SAMPLE_BLOOD_GROUP,
                R.drawable.ic_droplet));
        healthInfoList.add(new HealthInfo(getString(R.string.label_age),
                Constants.SAMPLE_AGE + Constants.SPACE + getString(R.string.unit_years),
                R.drawable.ic_calendar));
        healthInfoList.add(new HealthInfo(getString(R.string.label_address),
                Constants.SAMPLE_ADDRESS,
                R.drawable.ic_location));
        adapter.notifyDataSetChanged();
    }
}