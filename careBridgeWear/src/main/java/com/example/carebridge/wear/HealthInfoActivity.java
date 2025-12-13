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

import java.util.ArrayList;
import java.util.List;

/**
 * HealthInfoActivity
 * Displays patient health information on Wear OS
 */
public class HealthInfoActivity extends AppCompatActivity {

    private ActivityHealthInfoBinding binding;
    private List<HealthInfo> healthInfoList;
    private HealthInfoAdapter healthInfoAdapter;
    private PatientController patientController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        patientController = new PatientController(this);

        setupRecyclerView();
        fetchPatientData();
    }

    /**
     * Setup RecyclerView
     */
    private void setupRecyclerView() {
        healthInfoList = new ArrayList<>();
        healthInfoAdapter = new HealthInfoAdapter(healthInfoList);

        binding.healthInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.healthInfoRecyclerView.setAdapter(healthInfoAdapter);

        showLoadingState();
    }

    /**
     * Fetch patient data from API
     */
    private void fetchPatientData() {
        Log.d(Constants.TAG_HEALTH_INFO_ACTIVITY,
                Constants.LOG_EMOJI_INFO + " Fetching patient info");

        patientController.getCurrentPatient(new PatientController.PatientCallback() {

            @Override
            public void onSuccess(PatientInfo patientInfo) {
                convertPatientInfoToHealthInfo(patientInfo);

                runOnUiThread(() -> {
                    hideLoadingState();
                    healthInfoAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onFailure(String message) {
                Log.e(Constants.TAG_HEALTH_INFO_ACTIVITY,
                        Constants.LOG_EMOJI_ERROR + " " + message);

                runOnUiThread(() -> {
                    hideLoadingState();
                    showErrorState();
                    initializeSampleData();
                });
            }
        });
    }

    /**
     * Convert API model to UI model
     */
    private void convertPatientInfoToHealthInfo(PatientInfo patientInfo) {
        healthInfoList.clear();

        if (patientInfo == null) {
            showNoData();
            return;
        }

        addIfNotEmpty(patientInfo.getFullName(), R.string.label_name, R.drawable.ic_user);
        addIfNotEmpty(patientInfo.getBloodGroup(), R.string.label_blood_group, R.drawable.ic_droplet);
        addIfNotEmpty(patientInfo.getGender(), R.string.label_gender, R.drawable.ic_user);
        addIfNotEmpty(patientInfo.getAddress(), R.string.label_address, R.drawable.ic_location);
        addIfNotEmpty(patientInfo.getContactNumber(), R.string.label_contact, R.drawable.ic_phone);
        addIfNotEmpty(patientInfo.getEmail(), R.string.label_email, R.drawable.ic_mail);

        if (patientInfo.getDob() != null && !patientInfo.getDob().isEmpty()) {
            healthInfoList.add(new HealthInfo(
                    getString(R.string.label_age),
                    calculateAgeFromDOB(patientInfo.getDob()),
                    R.drawable.ic_calendar
            ));
        }

        addWithUnit(patientInfo.getHeightCm(), R.string.label_height, R.string.unit_cm);
        addWithUnit(patientInfo.getWeightKg(), R.string.label_weight, R.string.unit_kg);

        if (healthInfoList.isEmpty()) {
            showNoData();
        }
    }

    private void addIfNotEmpty(String value, int label, int icon) {
        if (value != null && !value.isEmpty()) {
            healthInfoList.add(new HealthInfo(getString(label), value, icon));
        }
    }

    private void addWithUnit(String value, int label, int unit) {
        if (value != null && !value.isEmpty()) {
            healthInfoList.add(new HealthInfo(
                    getString(label),
                    value + Constants.SPACE + getString(unit),
                    R.drawable.ic_activity
            ));
        }
    }

    private String calculateAgeFromDOB(String dob) {
        try {
            int birthYear = Integer.parseInt(dob.substring(0, 4));
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            return (currentYear - birthYear) + Constants.SPACE + getString(R.string.unit_years);
        } catch (Exception e) {
            return getString(R.string.unknown);
        }
    }

    private void showNoData() {
        healthInfoList.add(new HealthInfo(
                getString(R.string.label_no_data),
                getString(R.string.patient_info_not_available),
                R.drawable.ic_user
        ));
    }

    /* ---------- UI STATES ---------- */

    private void showLoadingState() {
        binding.progressHealthInfo.setVisibility(View.VISIBLE);
        binding.healthInfoRecyclerView.setVisibility(View.GONE);
        binding.tvErrorState.setVisibility(View.GONE);
    }

    private void hideLoadingState() {
        binding.progressHealthInfo.setVisibility(View.GONE);
        binding.healthInfoRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorState() {
        binding.tvErrorState.setVisibility(View.VISIBLE);
        binding.healthInfoRecyclerView.setVisibility(View.GONE);
    }

    /**
     * Fallback sample data
     */
    private void initializeSampleData() {
        healthInfoList.clear();

        healthInfoList.add(new HealthInfo(getString(R.string.label_name),
                Constants.SAMPLE_NAME_YASH, R.drawable.ic_user));
        healthInfoList.add(new HealthInfo(getString(R.string.label_blood_group),
                Constants.SAMPLE_BLOOD_GROUP, R.drawable.ic_droplet));
        healthInfoAdapter.notifyDataSetChanged();
    }
}