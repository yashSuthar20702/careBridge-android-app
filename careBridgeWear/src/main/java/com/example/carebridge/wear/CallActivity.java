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
    private List<Guardian> guardianList = new ArrayList<>();
    private GuardianCallAdapter adapter;

    private PatientGuardianInfoController guardianController;
    private WearSharedPrefManager wearSharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "CallActivity started");

        guardianController = new PatientGuardianInfoController(this);
        wearSharedPrefManager = new WearSharedPrefManager(this);

        setupUI();
        setupRecyclerView();
        fetchGuardiansData();
    }

    private void setupUI() {
        binding.callBackButton.setOnClickListener(v -> finish());

        binding.callEmergencyContact.setOnClickListener(v -> {
            Log.d(TAG, "Emergency clicked → calling 911");
            makeCall(getString(R.string.emergency_number));
        });
    }

    private void setupRecyclerView() {
        adapter = new GuardianCallAdapter(guardianList, this);
        binding.guardiansRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.guardiansRecyclerView.setAdapter(adapter);

        showLoadingState();
    }

    private void fetchGuardiansData() {
        Log.d(TAG, "Fetching guardians...");

        guardianController.getCurrentGuardian(new PatientGuardianInfoController.PatientGuardianCallback() {
            @Override
            public void onSuccess(List<PatientGuardianInfo> patientGuardianList) {
                Log.d(TAG, "API Success → Count: " + patientGuardianList.size());

                convertToGuardianList(patientGuardianList);

                runOnUiThread(() -> {
                    hideLoadingState();
                    adapter.notifyDataSetChanged();

                    if (guardianList.isEmpty()) {
                        showEmptyState();
                    } else {
                        for (Guardian g : guardianList) {
                            Log.d(TAG, "Loaded Guardian → " + g.getName() + " | " + g.getPhone());
                        }
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "API Failed: " + message);

                runOnUiThread(() -> {
                    hideLoadingState();
                    showErrorState(message);
                    initializeSampleData();
                });
            }
        });
    }

    private void convertToGuardianList(List<PatientGuardianInfo> patientGuardianList) {
        guardianList.clear();

        for (PatientGuardianInfo p : patientGuardianList) {
            guardianList.add(new Guardian(
                    p.getFull_name() != null ? p.getFull_name() : getString(R.string.unknown),
                    p.getType() != null ? p.getType() : getString(R.string.guardian),
                    p.getRole() != null ? p.getRole() : getString(R.string.caregiver),
                    p.getPhone() != null ? p.getPhone() : ""
            ));
        }
    }

    private void initializeSampleData() {
        guardianList.clear();
        guardianList.add(new Guardian("Yash", "Family", "Friend", "+1 519-569-2560"));
        guardianList.add(new Guardian("Dhwani", "Caretaker", "Primary Nurse", "+1 519-568-2540"));
        guardianList.add(new Guardian("Jasjit S", "Family", "Primary Guardian", "+1 519-573-0317"));
        guardianList.add(new Guardian("Dr. Smith", "Medical", "Primary Doctor", "+1 519-555-1234"));

        Log.d(TAG, "Sample data loaded.");
        adapter.notifyDataSetChanged();
    }

    private void showLoadingState() {
        binding.guardiansRecyclerView.setVisibility(View.INVISIBLE);
        Log.d(TAG, "Loading...");
    }

    private void hideLoadingState() {
        binding.guardiansRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showEmptyState() {
        Toast.makeText(this, getString(R.string.no_guardians_available), Toast.LENGTH_SHORT).show();
    }

    private void showErrorState(String message) {
        Toast.makeText(this, getString(R.string.error_prefix) + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGuardianCall(Guardian guardian) {
        Log.d(TAG, "Call Click → " + guardian.getName() + " | " + guardian.getPhone());

        if (guardian.getPhone() == null || guardian.getPhone().isEmpty()) {
            Toast.makeText(this, getString(R.string.no_valid_phone), Toast.LENGTH_SHORT).show();
            return;
        }

        makeCall(guardian.getPhone());
    }

    private void makeCall(String phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);

            Log.d(TAG, "Dial Intent Launched");
        } catch (Exception e) {
            Log.e(TAG, "Call failed: " + e.getMessage());
            Toast.makeText(this, getString(R.string.call_failed), Toast.LENGTH_SHORT).show();
        }
    }
}