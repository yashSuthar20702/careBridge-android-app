package com.example.carebridge.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.carebridge.R;
import com.example.carebridge.controller.PatientController;
import com.example.carebridge.model.PatientInfo;
import com.example.carebridge.model.User;
import com.example.carebridge.utils.SharedPrefManager;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PersonalInfoFragment extends Fragment {

    private static final String TAG = "PersonalInfoFragment";

    private TextView tvFullName, tvPatientAge, tvGender, tvBloodType,
            tvHeight, tvWeight, tvAllergies, tvConditions,
            tvPastSurgeries, tvCurrentSymptoms, tvAddress,
            tvContactNumber, tvEmail, tvStatus;

    private ShimmerFrameLayout shimmerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PatientController patientController;
    private SharedPrefManager sharedPrefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);
        bindViews(view);

        sharedPrefManager = new SharedPrefManager(requireContext());
        patientController = new PatientController(requireContext());

        shimmerLayout.startShimmer();
        shimmerLayout.setVisibility(View.VISIBLE);

        // Fetch patient data initially
        fetchPatientData();

        // Add swipe to refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "Swipe-to-refresh triggered");
            fetchPatientData();
        });

        return view;
    }

    private void bindViews(View view) {
        tvFullName = view.findViewById(R.id.tvFullName);
        tvPatientAge = view.findViewById(R.id.tvPatientAge);
        tvGender = view.findViewById(R.id.tvGender);
        tvBloodType = view.findViewById(R.id.tvBloodType);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvAllergies = view.findViewById(R.id.tvAllergies);
        tvConditions = view.findViewById(R.id.tvConditions);
        tvPastSurgeries = view.findViewById(R.id.tvPastSurgeries);
        tvCurrentSymptoms = view.findViewById(R.id.tvCurrentSymptoms);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvContactNumber = view.findViewById(R.id.tvContactNumber);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvStatus = view.findViewById(R.id.tvStatus);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

    private void fetchPatientData() {
        User currentUser = sharedPrefManager.getCurrentUser();
        if (currentUser == null || currentUser.getPatientInfo() == null) {
            Log.w(TAG, "[USER] No current user found");
        }

        shimmerLayout.startShimmer();
        shimmerLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);

        patientController.getCurrentPatient(new PatientController.PatientCallback() {
            @Override
            public void onSuccess(PatientInfo patientInfo) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    if (patientInfo == null) {
                        Toast.makeText(requireContext(), "No patient data found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    displayPatientInfo(patientInfo);
                });
            }

            @Override
            public void onFailure(String message) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(requireContext(), "Failed to fetch patient info: " + message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void displayPatientInfo(PatientInfo patientInfo) {
        setBoldLabel(tvFullName, "Full Name:", safeString(patientInfo.getFull_name()));
        setBoldLabel(tvPatientAge, "Age:", calculateAge(patientInfo.getDob()) + "");
        setBoldLabel(tvGender, "Gender:", safeString(patientInfo.getGender()));
        setBoldLabel(tvBloodType, "Blood Type:", safeString(patientInfo.getBlood_group()));
        setBoldLabel(tvHeight, "Height:", safeString(patientInfo.getHeight_cm()) + " cm");
        setBoldLabel(tvWeight, "Weight:", safeString(patientInfo.getWeight_kg()) + " kg");
        setBoldLabel(tvAllergies, "Allergies:", joinList(patientInfo.getAllergies()));
        setBoldLabel(tvConditions, "Conditions:", joinList(patientInfo.getMedical_conditions()));
        setBoldLabel(tvPastSurgeries, "Past Surgeries:", safeString(patientInfo.getPast_surgeries()));
        setBoldLabel(tvCurrentSymptoms, "Current Symptoms:", safeString(patientInfo.getCurrent_symptoms()));
        setBoldLabel(tvAddress, "Address:", safeString(patientInfo.getAddress()));
        setBoldLabel(tvContactNumber, "Contact Number:", safeString(patientInfo.getContact_number()));
        setBoldLabel(tvEmail, "Email:", safeString(patientInfo.getEmail()));
        setBoldLabel(tvStatus, "Status:", safeString(patientInfo.getStatus()));

        requireView().findViewById(R.id.cardContent).setVisibility(View.VISIBLE);
    }

    private void setBoldLabel(TextView textView, String label, String value) {
        SpannableString spannable = new SpannableString(label + " " + value);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), 0);
        textView.setText(spannable);
    }

    private int calculateAge(String dob) {
        if (dob == null || dob.isEmpty()) return 0;
        try {
            Calendar birth = Calendar.getInstance();
            birth.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) age--;
            return age;
        } catch (Exception e) {
            Log.e(TAG, "[ERROR] Failed to parse DOB: " + dob, e);
            return 0;
        }
    }

    private String safeString(String value) {
        return (value != null && !value.isEmpty()) ? value : "N/A";
    }

    private String joinList(List<String> list) {
        return (list != null && !list.isEmpty()) ? String.join(", ", list) : "N/A";
    }
}
