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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.carebridge.R;
import com.example.carebridge.shared.controller.PatientController;
import com.example.carebridge.shared.model.PatientInfo;
import com.example.carebridge.shared.model.User;
import com.example.carebridge.shared.utils.SharedPrefManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PersonalInfoFragment extends Fragment {

    private static final String TAG = "PersonalInfoFragment";

    private TextView tvFullName, tvPatientAge, tvGender, tvBloodType,
            tvHeight, tvWeight, tvAllergies, tvConditions,
            tvPastSurgeries, tvCurrentSymptoms, tvAddress,
            tvContactNumber, tvEmail, tvStatus, tvWarningMessage;

    private ShimmerFrameLayout shimmerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialCardView cardWarning;
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

        fetchPatientData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "Swipe-to-refresh triggered");
            fetchPatientData();
        });

        return view;
    }

    /** Bind UI Views */
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
        cardWarning = view.findViewById(R.id.cardWarning);
        tvWarningMessage = view.findViewById(R.id.tvWarningMessage);
    }

    /** Fetch patient data safely */
    private void fetchPatientData() {
        User user = sharedPrefManager.getCurrentUser();

        shimmerLayout.startShimmer();
        shimmerLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        cardWarning.setVisibility(View.GONE);

        patientController.getCurrentPatient(new PatientController.PatientCallback() {
            @Override
            public void onSuccess(PatientInfo patientInfo) {
                if (getActivity() == null || getView() == null) return;

                getActivity().runOnUiThread(() -> {
                    View root = getView();
                    if (root == null) return;

                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    if (patientInfo == null) {
                        cardWarning.setVisibility(View.VISIBLE);
                        tvWarningMessage.setText(getString(R.string.no_patient_data_found));
                        return;
                    }

                    cardWarning.setVisibility(View.GONE);
                    displayPatientInfo(patientInfo);
                });
            }

            @Override
            public void onFailure(String message) {
                if (getActivity() == null || getView() == null) return;

                getActivity().runOnUiThread(() -> {
                    View root = getView();
                    if (root == null) return;

                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    // SAFE VERSION — NO requireView()
                    View cardContent = root.findViewById(R.id.cardContent);
                    if (cardContent != null) cardContent.setVisibility(View.GONE);

                    cardWarning.setVisibility(View.VISIBLE);
                    tvWarningMessage.setText(getString(R.string.patient_data_load_error));
                });
            }
        });
    }

    /** Display data */
    private void displayPatientInfo(PatientInfo patientInfo) {
        View root = getView();
        if (root == null) return;

        setBoldLabel(tvFullName, getString(R.string.full_name_label), safeString(patientInfo.getFullName()));
        setBoldLabel(tvPatientAge, getString(R.string.age_label), calculateAge(patientInfo.getDob()) + "");
        setBoldLabel(tvGender, getString(R.string.gender_label), safeString(patientInfo.getGender()));
        setBoldLabel(tvBloodType, getString(R.string.blood_type_label), safeString(patientInfo.getBloodGroup()));
        setBoldLabel(tvHeight, getString(R.string.height_label), safeString(patientInfo.getHeightCm()) + getString(R.string.cm_unit));
        setBoldLabel(tvWeight, getString(R.string.weight_label), safeString(patientInfo.getWeightKg()) + getString(R.string.kg_unit));

        setBoldLabel(tvAllergies, getString(R.string.allergies_label), joinList(patientInfo.getAllergies()));
        setBoldLabel(tvConditions, getString(R.string.conditions_label), joinList(patientInfo.getMedicalConditions()));
        setBoldLabel(tvPastSurgeries, getString(R.string.past_surgeries_label), safeString(patientInfo.getPastSurgeries()));
        setBoldLabel(tvCurrentSymptoms, getString(R.string.current_symptoms_label), safeString(patientInfo.getCurrentSymptoms()));
        setBoldLabel(tvAddress, getString(R.string.address_label), safeString(patientInfo.getAddress()));
        setBoldLabel(tvContactNumber, getString(R.string.phone_label), safeString(patientInfo.getContactNumber()));
        setBoldLabel(tvEmail, getString(R.string.email_label), safeString(patientInfo.getEmail()));
        setBoldLabel(tvStatus, getString(R.string.status_label), safeString(patientInfo.getStatus()));

        View cardContent = root.findViewById(R.id.cardContent);
        if (cardContent != null) cardContent.setVisibility(View.VISIBLE);
    }

    /** Bold label helper */
    private void setBoldLabel(TextView textView, String label, String value) {
        SpannableString spannable = new SpannableString(label + " " + value);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), 0);
        textView.setText(spannable);
    }

    /** Calculate age */
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
            Log.e(TAG, "DOB parse error: " + dob, e);
            return 0;
        }
    }

    /** Safe string helper */
    private String safeString(String value) {
        return (value != null && !value.isEmpty()) ? value : getString(R.string.not_available_text);
    }

    /** Join list helper */
    private String joinList(List<String> list) {
        return (list != null && !list.isEmpty()) ? String.join(", ", list) : getString(R.string.not_available_text);
    }
}
