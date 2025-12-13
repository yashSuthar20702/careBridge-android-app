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

/**
 * CallActivity
 *
 * This Wear OS activity allows the user (patient) to:
 * 1. View a list of guardians fetched from the backend API
 * 2. Quickly call a selected guardian from the watch
 * 3. Call an emergency number with a single tap
 *
 * The activity follows:
 * - MVVM-friendly separation (Controller + Model + Adapter)
 * - ViewBinding for UI access
 * - Listener interface for RecyclerView item actions
 */
public class CallActivity extends AppCompatActivity
        implements GuardianCallAdapter.OnGuardianCallListener {

    // ViewBinding reference for accessing layout views safely
    private ActivityCallBinding binding;

    // List holding guardian data displayed in RecyclerView
    private final List<Guardian> guardianList = new ArrayList<>();

    // RecyclerView adapter for guardian list
    private GuardianCallAdapter adapter;

    // Controller responsible for fetching guardian data from API
    private PatientGuardianInfoController guardianController;

    // Shared preferences manager for Wear OS user/session data
    private WearSharedPrefManager wearSharedPrefManager;

    /**
     * Activity lifecycle entry point
     * Initializes UI, RecyclerView, and triggers guardian data fetch
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Log activity startup for debugging
        Log.d(Constants.TAG_CALL_ACTIVITY,
                Constants.LOG_EMOJI_SUCCESS + Constants.SPACE +
                        Constants.LOG_MSG_ACTIVITY_STARTED);

        // Initialize controller and shared preference manager
        guardianController = new PatientGuardianInfoController(this);
        wearSharedPrefManager = new WearSharedPrefManager(this);

        // Setup UI interactions
        initializeUI();

        // Configure RecyclerView
        setupRecyclerView();

        // Fetch guardians from backend API
        fetchGuardiansData();
    }

    /**
     * Initializes UI interactions
     * - Handles emergency contact button click
     */
    private void initializeUI() {
        binding.callEmergencyContact.setOnClickListener(v -> {
            Log.d(Constants.TAG_CALL_ACTIVITY,
                    Constants.LOG_EMOJI_CALL + Constants.SPACE +
                            Constants.LOG_MSG_EMERGENCY_CLICKED);

            // Emergency call using predefined emergency number
            makeCall(getString(R.string.emergency_number));
        });
    }

    /**
     * Sets up RecyclerView with adapter and layout manager
     * Shows loading state until data is available
     */
    private void setupRecyclerView() {
        adapter = new GuardianCallAdapter(guardianList, this);
        binding.guardiansRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.guardiansRecyclerView.setAdapter(adapter);

        showLoadingState();
    }

    /**
     * Fetches guardian data from backend using controller
     * Uses callback to handle success and failure cases
     */
    private void fetchGuardiansData() {
        guardianController.getCurrentGuardian(
                new PatientGuardianInfoController.PatientGuardianCallback() {

                    /**
                     * Called when guardian data is successfully fetched
                     */
                    @Override
                    public void onSuccess(List<PatientGuardianInfo> patientGuardianList) {
                        convertToGuardianList(patientGuardianList);

                        runOnUiThread(() -> {
                            hideLoadingState();
                            adapter.notifyDataSetChanged();

                            // Show empty state if no guardians exist
                            if (guardianList.isEmpty()) {
                                showEmptyState();
                            }
                        });
                    }

                    /**
                     * Called when API request fails
                     * Displays error and loads fallback sample data
                     */
                    @Override
                    public void onFailure(String message) {
                        runOnUiThread(() -> {
                            hideLoadingState();
                            showErrorState(message);
                            initializeSampleData();
                        });
                    }
                });
    }

    /**
     * Converts API response objects into local Guardian model
     * Ensures null-safe values using string resources
     */
    private void convertToGuardianList(List<PatientGuardianInfo> patientGuardianList) {
        guardianList.clear();

        for (PatientGuardianInfo info : patientGuardianList) {
            guardianList.add(new Guardian(
                    info.getFull_name() != null ? info.getFull_name() : getString(R.string.unknown),
                    info.getType() != null ? info.getType() : getString(R.string.guardian),
                    info.getRole() != null ? info.getRole() : getString(R.string.caregiver),
                    info.getPhone() != null ? info.getPhone() : Constants.EMPTY_STRING
            ));
        }
    }

    /**
     * Initializes sample guardian data
     * Used ONLY when API fails (offline / server issue)
     */
    private void initializeSampleData() {
        guardianList.clear();

        guardianList.add(new Guardian(
                getString(R.string.sample_name_yash),
                getString(R.string.sample_type_family),
                getString(R.string.sample_relation_friend),
                getString(R.string.sample_phone_yash)
        ));

        guardianList.add(new Guardian(
                getString(R.string.sample_name_dhwani),
                getString(R.string.sample_type_caretaker),
                getString(R.string.sample_relation_nurse),
                getString(R.string.sample_phone_dhwani)
        ));

        guardianList.add(new Guardian(
                getString(R.string.sample_name_jasjit),
                getString(R.string.sample_type_family),
                getString(R.string.sample_relation_guardian),
                getString(R.string.sample_phone_jasjit)
        ));

        guardianList.add(new Guardian(
                getString(R.string.sample_name_dr_smith),
                getString(R.string.sample_type_medical),
                getString(R.string.sample_relation_doctor),
                getString(R.string.sample_phone_dr_smith)
        ));

        adapter.notifyDataSetChanged();
    }

    /**
     * Displays loading state while data is being fetched
     */
    private void showLoadingState() {
        binding.guardiansRecyclerView.setVisibility(View.INVISIBLE);
    }

    /**
     * Hides loading state once data is available
     */
    private void hideLoadingState() {
        binding.guardiansRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows message when no guardians are available
     */
    private void showEmptyState() {
        Toast.makeText(this,
                getString(R.string.no_guardians_available),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays API error message
     */
    private void showErrorState(String message) {
        Toast.makeText(this,
                getString(R.string.error_prefix) + message,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback from RecyclerView when a guardian item is tapped
     */
    @Override
    public void onGuardianCall(Guardian guardian) {
        if (guardian.getPhone() == null || guardian.getPhone().isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.no_valid_phone),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        makeCall(guardian.getPhone());
    }

    /**
     * Launches dialer intent for the given phone number
     * Uses ACTION_DIAL for safety (no direct call permission required)
     */
    private void makeCall(String phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(Constants.URI_SCHEME_TEL + phone));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this,
                    getString(R.string.call_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }
}