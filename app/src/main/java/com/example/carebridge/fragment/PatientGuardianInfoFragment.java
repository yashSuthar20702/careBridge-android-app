package com.example.carebridge.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carebridge.R;
import com.example.carebridge.controller.PatientGuardianInfoController;
import com.example.carebridge.model.PatientGuardianInfo;

import java.util.List;

public class PatientGuardianInfoFragment extends Fragment {

    private static final String TAG = "PatientGuardianInfoFragment";

    private TextView tvGuardianName, tvGuardianRelationship, tvGuardianPhone, tvGuardianEmail;
    private PatientGuardianInfoController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_guardian_info, container, false);

        tvGuardianName = view.findViewById(R.id.tvGuardianName);
        tvGuardianRelationship = view.findViewById(R.id.tvGuardianRelationship);
        tvGuardianPhone = view.findViewById(R.id.tvGuardianPhone);
        tvGuardianEmail = view.findViewById(R.id.tvGuardianEmail);

        controller = new PatientGuardianInfoController(requireContext());
        fetchGuardianData();

        return view;
    }

    private void fetchGuardianData() {
        controller.getCurrentGuardian(new PatientGuardianInfoController.PatientGuardianCallback() {
            @Override
            public void onSuccess(List<PatientGuardianInfo> guardianList) {
                if (guardianList == null || guardianList.isEmpty()) {
                    Toast.makeText(requireContext(), "No guardian info found", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Assuming single guardian
                PatientGuardianInfo guardian = guardianList.get(0);
                displayGuardianInfo(guardian);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(requireContext(), "Failed: " + message, Toast.LENGTH_LONG).show();
                Log.e(TAG, "[API ERROR] " + message);
            }
        });
    }

    private void displayGuardianInfo(PatientGuardianInfo guardian) {
        tvGuardianName.setText("Guardian: " + safeString(guardian.getFull_name()));
        tvGuardianRelationship.setText("Relationship: " + safeString(guardian.getRole()));
        tvGuardianPhone.setText("Phone: " + safeString(guardian.getPhone()));
        tvGuardianEmail.setText("Email: " + safeString(guardian.getEmail()));
    }

    private String safeString(String value) {
        return (value != null && !value.isEmpty()) ? value : "N/A";
    }
}
