package com.example.carebridge.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carebridge.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMealActivity extends AppCompatActivity {

    private TextInputEditText etMorning, etAfternoon, etEvening, etNight;
    private TextView tvPatientName, tvMealDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        // Get patient data from intent
        String patientId = getIntent().getStringExtra("PATIENT_ID");
        String patientName = getIntent().getStringExtra("PATIENT_NAME");

        // Initialize views
        etMorning = findViewById(R.id.etMorning);
        etAfternoon = findViewById(R.id.etAfternoon);
        etEvening = findViewById(R.id.etEvening);
        etNight = findViewById(R.id.etNight);
        tvPatientName = findViewById(R.id.tvPatientName);
        tvMealDate = findViewById(R.id.tvMealDate);

        // Set patient info
        if (patientName != null && !patientName.isEmpty()) {
            tvPatientName.setText(patientName);
        }

        // Set current date
        String currentDate = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(new Date());
        tvMealDate.setText(currentDate);

        // Setup back button
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        // Submit button
        MaterialButton btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> saveMealData(patientId, patientName));

        // Clear button
        MaterialButton btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> clearForm());
    }

    private void saveMealData(String patientId, String patientName) {
        String morning = etMorning.getText().toString().trim();
        String afternoon = etAfternoon.getText().toString().trim();
        String evening = etEvening.getText().toString().trim();
        String night = etNight.getText().toString().trim();

        // Basic validation
        if (morning.isEmpty() && afternoon.isEmpty() && evening.isEmpty() && night.isEmpty()) {
            Toast.makeText(this, "Please enter at least one meal entry", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Save to database or API
        // For now, show success message
        Toast.makeText(this, "Meal data saved successfully!", Toast.LENGTH_SHORT).show();
        finish();
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