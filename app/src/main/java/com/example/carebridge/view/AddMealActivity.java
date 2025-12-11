package com.example.carebridge.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carebridge.R;
import com.example.carebridge.shared.controller.MealController;
import com.example.carebridge.shared.utils.SharedPrefManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMealActivity extends AppCompatActivity {

    private static final String TAG = "AddMealActivity";

    private TextInputEditText etMorning, etAfternoon, etEvening, etNight;
    private TextView tvPatientName, tvMealDate;
    private MaterialCardView cardCurrentMeal;
    private TextView tvMorningCurrent, tvAfternoonCurrent, tvEveningCurrent, tvNightCurrent;

    private String formattedDate;
    private MealController mealController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        mealController = new MealController();

        // Retrieve patient data from Intent
        String patientId = getIntent().getStringExtra("PATIENT_ID");
        String patientName = getIntent().getStringExtra("PATIENT_NAME");

        Log.d(TAG, String.format(getString(R.string.patient_id_log_format), patientId));
        Log.d(TAG, String.format(getString(R.string.patient_name_log_format), patientName));

        initViews();

        if (patientName != null) tvPatientName.setText(patientName);

        formattedDate = new SimpleDateFormat(getString(R.string.date_format_storage), Locale.getDefault()).format(new Date());
        String displayDate = new SimpleDateFormat(getString(R.string.date_format_display), Locale.getDefault()).format(new Date());
        tvMealDate.setText(displayDate);

        setupClickListeners(patientId);

        if (patientId != null && !patientId.isEmpty()) {
            fetchCurrentMealPlan(patientId);
        }
    }

    private void initViews() {
        etMorning = findViewById(R.id.etMorning);
        etAfternoon = findViewById(R.id.etAfternoon);
        etEvening = findViewById(R.id.etEvening);
        etNight = findViewById(R.id.etNight);

        tvPatientName = findViewById(R.id.tvPatientName);
        tvMealDate = findViewById(R.id.tvMealDate);

        cardCurrentMeal = findViewById(R.id.cardCurrentMeal);
        tvMorningCurrent = findViewById(R.id.tvMorningCurrent);
        tvAfternoonCurrent = findViewById(R.id.tvAfternoonCurrent);
        tvEveningCurrent = findViewById(R.id.tvEveningCurrent);
        tvNightCurrent = findViewById(R.id.tvNightCurrent);
    }

    private void setupClickListeners(String patientId) {
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.btnSubmit).setOnClickListener(v -> saveMealData(patientId));
        findViewById(R.id.btnClear).setOnClickListener(v -> clearForm());
    }

    // --------------------------
    // Fetch Current Meal Plan using new API
    // --------------------------
    private void fetchCurrentMealPlan(String patientId) {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading_current_meal_plan));
        progress.setCancelable(false);
        progress.show();

        mealController.fetchMealPlanByCaseId(patientId, new MealController.MealFetchCallback() {
            @Override
            public void onSuccess(JSONObject mealPlan) {
                progress.dismiss();
                try {
                    cardCurrentMeal.setVisibility(View.VISIBLE);

                    tvMorningCurrent.setText(String.format(getString(R.string.morning_meal_format),
                            mealPlan.optString("morning_meal", "-")));
                    tvAfternoonCurrent.setText(String.format(getString(R.string.afternoon_meal_format),
                            mealPlan.optString("afternoon_meal", "-")));
                    tvEveningCurrent.setText(String.format(getString(R.string.evening_meal_format),
                            mealPlan.optString("evening_meal", "-")));
                    tvNightCurrent.setText(String.format(getString(R.string.night_meal_format),
                            mealPlan.optString("night_meal", "-")));

                    Log.d(TAG, String.format(getString(R.string.current_meal_plan_loaded), mealPlan));
                } catch (Exception e) {
                    Log.e(TAG, getString(R.string.error_parsing_meal_plan), e);
                }
            }

            @Override
            public void onFailure(String message) {
                progress.dismiss();
                cardCurrentMeal.setVisibility(View.GONE);
                Log.w(TAG, String.format(getString(R.string.failed_to_load_meal_plan), message));
                Toast.makeText(AddMealActivity.this,
                        getString(R.string.no_existing_meal_plan_found),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --------------------------
    // Save Meal Plan
    // --------------------------
    private void saveMealData(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.patient_id_missing_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String morning = etMorning.getText().toString().trim();
        String afternoon = etAfternoon.getText().toString().trim();
        String evening = etEvening.getText().toString().trim();
        String night = etNight.getText().toString().trim();

        if (morning.isEmpty() && afternoon.isEmpty() && evening.isEmpty() && night.isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.enter_at_least_one_meal_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPrefManager sharedPref = new SharedPrefManager(this);
        String guardianId = sharedPref.getReferenceId();
        if (guardianId == null || guardianId.isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.reference_id_not_found_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.saving_meal_plan));
        progress.setCancelable(false);
        progress.show();

        mealController.addMealPlan(
                patientId,
                guardianId,
                formattedDate,
                morning,
                afternoon,
                evening,
                night,
                "",
                new MealController.MealAddCallback() {
                    @Override
                    public void onSuccess(String message) {
                        progress.dismiss();
                        Toast.makeText(AddMealActivity.this,
                                getString(R.string.meal_plan_save_success),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        progress.dismiss();
                        Toast.makeText(AddMealActivity.this,
                                String.format(getString(R.string.save_failed_with_message), message),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void clearForm() {
        etMorning.setText("");
        etAfternoon.setText("");
        etEvening.setText("");
        etNight.setText("");
        etMorning.requestFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}