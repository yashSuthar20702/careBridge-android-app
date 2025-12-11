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

        Log.d(TAG, "Received Patient ID: " + patientId);
        Log.d(TAG, "Received Patient Name: " + patientName);

        initViews();

        if (patientName != null) tvPatientName.setText(patientName);

        formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String displayDate = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(new Date());
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
        progress.setMessage("Loading current meal plan...");
        progress.setCancelable(false);
        progress.show();

        mealController.fetchMealPlanByCaseId(patientId, new MealController.MealFetchCallback() {
            @Override
            public void onSuccess(JSONObject mealPlan) {
                progress.dismiss();
                try {
                    cardCurrentMeal.setVisibility(View.VISIBLE);

                    tvMorningCurrent.setText("Morning: " + mealPlan.optString("morning_meal", "-"));
                    tvAfternoonCurrent.setText("Afternoon: " + mealPlan.optString("afternoon_meal", "-"));
                    tvEveningCurrent.setText("Evening: " + mealPlan.optString("evening_meal", "-"));
                    tvNightCurrent.setText("Night: " + mealPlan.optString("night_meal", "-"));

                    Log.d(TAG, "Current meal plan loaded: " + mealPlan);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing meal plan JSON", e);
                }
            }

            @Override
            public void onFailure(String message) {
                progress.dismiss();
                cardCurrentMeal.setVisibility(View.GONE);
                Log.w(TAG, "Failed to load meal plan: " + message);
                Toast.makeText(AddMealActivity.this, "No existing meal plan found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --------------------------
    // Save Meal Plan
    // --------------------------
    private void saveMealData(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            Toast.makeText(this, "Patient ID missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        String morning = etMorning.getText().toString().trim();
        String afternoon = etAfternoon.getText().toString().trim();
        String evening = etEvening.getText().toString().trim();
        String night = etNight.getText().toString().trim();

        if (morning.isEmpty() && afternoon.isEmpty() && evening.isEmpty() && night.isEmpty()) {
            Toast.makeText(this, "Please enter at least one meal entry", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPrefManager sharedPref = new SharedPrefManager(this);
        String guardianId = sharedPref.getReferenceId();
        if (guardianId == null || guardianId.isEmpty()) {
            Toast.makeText(this, "Reference ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Saving meal plan...");
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
                        Toast.makeText(AddMealActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        progress.dismiss();
                        Toast.makeText(AddMealActivity.this, "Failed: " + message, Toast.LENGTH_LONG).show();
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
