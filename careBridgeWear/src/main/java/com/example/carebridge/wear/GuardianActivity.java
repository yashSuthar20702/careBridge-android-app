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

import java.util.ArrayList;
import java.util.List;

/**
 * GuardianActivity
 * Displays guardians assigned to the current patient on Wear OS
 */
public class GuardianActivity extends AppCompatActivity {

    private ActivityGuardianBinding binding;
    private List<Guardian> guardianList;
    private GuardianAdapter guardianAdapter;
    private PatientGuardianInfoController guardianController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuardianBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        guardianController = new PatientGuardianInfoController(this);

        setupRecyclerView();
        fetchGuardianData();
    }

    /**
     * Setup RecyclerView
     */
    private void setupRecyclerView() {
        guardianList = new ArrayList<>();
        guardianAdapter = new GuardianAdapter(guardianList);

        binding.guardianRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.guardianRecyclerView.setAdapter(guardianAdapter);

        showLoadingState();
    }

    /**
     * Fetch guardian data from API
     */
    private void fetchGuardianData() {
        Log.d(Constants.TAG_GUARDIAN_ACTIVITY,
                Constants.LOG_EMOJI_INFO + " Fetching guardian data");

        guardianController.getCurrentGuardian(new PatientGuardianInfoController.PatientGuardianCallback() {

            @Override
            public void onSuccess(List<PatientGuardianInfo> patientGuardianList) {
                convertToGuardianList(patientGuardianList);

                runOnUiThread(() -> {
                    hideLoadingState();
                    guardianAdapter.notifyDataSetChanged();

                    if (guardianList.isEmpty()) {
                        showEmptyState();
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                Log.e(Constants.TAG_GUARDIAN_ACTIVITY,
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
    private void convertToGuardianList(List<PatientGuardianInfo> patientGuardianList) {
        guardianList.clear();

        if (patientGuardianList == null || patientGuardianList.isEmpty()) {
            Log.d(Constants.TAG_GUARDIAN_ACTIVITY,
                    Constants.LOG_EMOJI_INFO + " No guardian data");
            return;
        }

        for (PatientGuardianInfo info : patientGuardianList) {
            guardianList.add(new Guardian(
                    info.getFull_name(),
                    info.getType(),
                    info.getRole(),
                    info.getPhone()
            ));
        }
    }

    /**
     * Fallback sample data
     */
    private void initializeSampleData() {
        guardianList.clear();

        guardianList.add(new Guardian(
                Constants.SAMPLE_NAME_YASH,
                Constants.SAMPLE_TYPE_FAMILY,
                Constants.SAMPLE_RELATION_FRIEND,
                Constants.SAMPLE_PHONE_YASH
        ));

        guardianList.add(new Guardian(
                Constants.SAMPLE_NAME_DHWANI,
                Constants.SAMPLE_TYPE_CARETAKER,
                Constants.SAMPLE_RELATION_NURSE,
                Constants.SAMPLE_PHONE_DHWANI
        ));

        guardianAdapter.notifyDataSetChanged();
    }

    /* ---------------- UI STATES ---------------- */

    private void showLoadingState() {
        binding.progressGuardian.setVisibility(View.VISIBLE);
        binding.guardianRecyclerView.setVisibility(View.GONE);
        binding.tvEmptyState.setVisibility(View.GONE);
        binding.tvErrorState.setVisibility(View.GONE);
    }

    private void hideLoadingState() {
        binding.progressGuardian.setVisibility(View.GONE);
        binding.guardianRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showEmptyState() {
        binding.tvEmptyState.setVisibility(View.VISIBLE);
        binding.guardianRecyclerView.setVisibility(View.GONE);
    }

    private void showErrorState() {
        binding.tvErrorState.setVisibility(View.VISIBLE);
        binding.guardianRecyclerView.setVisibility(View.GONE);
    }
}