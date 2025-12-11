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
import com.example.carebridge.wear.utils.Constants;
import com.example.carebridge.wear.utils.WearSharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class CallActivity extends AppCompatActivity implements GuardianCallAdapter.OnGuardianCallListener {

    private ActivityCallBinding binding;
    private List<Guardian> guardianList = new ArrayList<>();
    private GuardianCallAdapter adapter;

    private PatientGuardianInfoController guardianController;
    private WearSharedPrefManager wearSharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(Constants.TAG_CALL_ACTIVITY,
                Constants.LOG_EMOJI_SUCCESS + Constants.SPACE + Constants.LOG_MSG_ACTIVITY_STARTED);

        guardianController = new PatientGuardianInfoController(this);
        wearSharedPrefManager = new WearSharedPrefManager(this);

        initializeUI();
        setupRecyclerView();
        fetchGuardiansData();
    }

    /**
     * Initialize UI components and set up listeners
     */
    private void initializeUI() {
        binding.callEmergencyContact.setOnClickListener(v -> {
            Log.d(Constants.TAG_CALL_ACTIVITY,
                    Constants.LOG_EMOJI_CALL + Constants.SPACE + Constants.LOG_MSG_EMERGENCY_CLICKED);
            makeCall(getString(R.string.emergency_number));
        });
    }

    /**
     * Set up RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        adapter = new GuardianCallAdapter(guardianList, this);
        binding.guardiansRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.guardiansRecyclerView.setAdapter(adapter);

        showLoadingState();
    }

    /**
     * Fetch guardians data from API
     */
    private void fetchGuardiansData() {
        Log.d(Constants.TAG_CALL_ACTIVITY,
                Constants.LOG_EMOJI_INFO + Constants.SPACE + Constants.LOG_MSG_FETCHING_GUARDIANS);

        guardianController.getCurrentGuardian(new PatientGuardianInfoController.PatientGuardianCallback() {
            @Override
            public void onSuccess(List<PatientGuardianInfo> patientGuardianList) {
                Log.d(Constants.TAG_CALL_ACTIVITY,
                        Constants.LOG_EMOJI_SUCCESS + Constants.SPACE + Constants.LOG_MSG_API_SUCCESS +
                                Constants.COLON + Constants.SPACE + patientGuardianList.size());

                convertToGuardianList(patientGuardianList);

                runOnUiThread(() -> {
                    hideLoadingState();
                    adapter.notifyDataSetChanged();

                    if (guardianList.isEmpty()) {
                        showEmptyState();
                    } else {
                        logGuardiansData();
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                Log.e(Constants.TAG_CALL_ACTIVITY,
                        Constants.LOG_EMOJI_ERROR + Constants.SPACE + Constants.LOG_MSG_API_FAILED +
                                Constants.COLON + Constants.SPACE + message);

                runOnUiThread(() -> {
                    hideLoadingState();
                    showErrorState(message);
                    initializeSampleData();
                });
            }
        });
    }

    /**
     * Log guardian data for debugging
     */
    private void logGuardiansData() {
        for (Guardian guardian : guardianList) {
            Log.d(Constants.TAG_CALL_ACTIVITY,
                    Constants.LOG_EMOJI_INFO + Constants.SPACE + Constants.LOG_MSG_LOADED_GUARDIAN +
                            Constants.COLON + Constants.SPACE + guardian.getName() +
                            Constants.SPACE + Constants.VERTICAL_BAR + Constants.SPACE + guardian.getPhone());
        }
    }

    /**
     * Convert API response to Guardian model objects
     */
    private void convertToGuardianList(List<PatientGuardianInfo> patientGuardianList) {
        guardianList.clear();

        for (PatientGuardianInfo patientGuardian : patientGuardianList) {
            guardianList.add(new Guardian(
                    patientGuardian.getFull_name() != null ? patientGuardian.getFull_name() : getString(R.string.unknown),
                    patientGuardian.getType() != null ? patientGuardian.getType() : getString(R.string.guardian),
                    patientGuardian.getRole() != null ? patientGuardian.getRole() : getString(R.string.caregiver),
                    patientGuardian.getPhone() != null ? patientGuardian.getPhone() : Constants.EMPTY_STRING
            ));
        }
    }

    /**
     * Initialize with sample data when API fails
     */
    private void initializeSampleData() {
        guardianList.clear();
        guardianList.add(new Guardian(Constants.SAMPLE_NAME_YASH, Constants.SAMPLE_TYPE_FAMILY,
                Constants.SAMPLE_RELATION_FRIEND, Constants.SAMPLE_PHONE_YASH));
        guardianList.add(new Guardian(Constants.SAMPLE_NAME_DHWANI, Constants.SAMPLE_TYPE_CARETAKER,
                Constants.SAMPLE_RELATION_NURSE, Constants.SAMPLE_PHONE_DHWANI));
        guardianList.add(new Guardian(Constants.SAMPLE_NAME_JASJIT, Constants.SAMPLE_TYPE_FAMILY,
                Constants.SAMPLE_RELATION_GUARDIAN, Constants.SAMPLE_PHONE_JASJIT));
        guardianList.add(new Guardian(Constants.SAMPLE_NAME_DR_SMITH, Constants.SAMPLE_TYPE_MEDICAL,
                Constants.SAMPLE_RELATION_DOCTOR, Constants.SAMPLE_PHONE_DR_SMITH));

        Log.d(Constants.TAG_CALL_ACTIVITY,
                Constants.LOG_EMOJI_INFO + Constants.SPACE + Constants.LOG_MSG_SAMPLE_DATA_LOADED);
        adapter.notifyDataSetChanged();
    }

    /**
     * Show loading state while fetching data
     */
    private void showLoadingState() {
        binding.guardiansRecyclerView.setVisibility(View.INVISIBLE);
        Log.d(Constants.TAG_CALL_ACTIVITY,
                Constants.LOG_EMOJI_INFO + Constants.SPACE + Constants.LOG_MSG_LOADING);
    }

    /**
     * Hide loading state when data is loaded
     */
    private void hideLoadingState() {
        binding.guardiansRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Show empty state when no guardians available
     */
    private void showEmptyState() {
        Toast.makeText(this, getString(R.string.no_guardians_available), Toast.LENGTH_SHORT).show();
    }

    /**
     * Show error state with message
     */
    private void showErrorState(String message) {
        Toast.makeText(this, getString(R.string.error_prefix) + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGuardianCall(Guardian guardian) {
        Log.d(Constants.TAG_CALL_ACTIVITY,
                Constants.LOG_EMOJI_CLICK + Constants.SPACE + Constants.LOG_MSG_CALL_CLICK +
                        Constants.COLON + Constants.SPACE + guardian.getName() +
                        Constants.SPACE + Constants.VERTICAL_BAR + Constants.SPACE + guardian.getPhone());

        if (guardian.getPhone() == null || guardian.getPhone().isEmpty()) {
            Toast.makeText(this, getString(R.string.no_valid_phone), Toast.LENGTH_SHORT).show();
            return;
        }

        makeCall(guardian.getPhone());
    }

    /**
     * Initiate phone call with given phone number
     */
    private void makeCall(String phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(Constants.URI_SCHEME_TEL + phone));
            startActivity(intent);

            Log.d(Constants.TAG_CALL_ACTIVITY,
                    Constants.LOG_EMOJI_SUCCESS + Constants.SPACE + Constants.LOG_MSG_DIAL_LAUNCHED);
        } catch (Exception e) {
            Log.e(Constants.TAG_CALL_ACTIVITY,
                    Constants.LOG_EMOJI_ERROR + Constants.SPACE + Constants.LOG_MSG_CALL_FAILED +
                            Constants.COLON + Constants.SPACE + e.getMessage());
            Toast.makeText(this, getString(R.string.call_failed), Toast.LENGTH_SHORT).show();
        }
    }
}