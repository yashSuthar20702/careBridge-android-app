package com.example.carebridge.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carebridge.R;
import com.example.carebridge.controller.AuthController;
import com.example.carebridge.model.User;
import com.example.carebridge.model.PatientInfo;

public class PatientDashboardActivity extends AppCompatActivity {

    private static final String TAG = "PatientDashboard";

    private TextView tvWelcome, tvPatientName, tvPatientId;
    private TextView tvGuardianName, tvGuardianRelationship, tvGuardianPhone, tvGuardianEmail;
    private TextView tvPatientAge, tvBloodType, tvAllergies, tvConditions;
    private Button btnHeaderLogout;

    private User currentUser;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        initializeViews();
        authController = new AuthController(this);

        // Get user from intent or session
        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser == null) currentUser = authController.getCurrentUser();

        if (currentUser != null) populatePatientData();
        else Log.e(TAG, "No user data found!");
    }

    private void initializeViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvPatientName = findViewById(R.id.tvPatientName);
        tvPatientId = findViewById(R.id.tvPatientId);

        btnHeaderLogout = findViewById(R.id.btnHeaderLogout);
        tvGuardianName = findViewById(R.id.tvGuardianName);
        tvGuardianRelationship = findViewById(R.id.tvGuardianRelationship);
        tvGuardianPhone = findViewById(R.id.tvGuardianPhone);
        tvGuardianEmail = findViewById(R.id.tvGuardianEmail);
        tvPatientAge = findViewById(R.id.tvPatientAge);
        tvBloodType = findViewById(R.id.tvBloodType);
        tvAllergies = findViewById(R.id.tvAllergies);
        tvConditions = findViewById(R.id.tvConditions);

        btnHeaderLogout.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("No", null)
                .show());
    }

    private void populatePatientData() {
        PatientInfo info = currentUser.getPatientInfo();

        // Safe defaults if info is null
        String fullName = info != null ? info.getFullName() : "N/A";
        String dob = info != null ? info.getDob() : "N/A";
        String gender = info != null ? info.getGender() : "N/A";
        String status = info != null ? info.getStatus() : "Active";
        String contact = info != null ? info.getContactNumber() : "N/A";
        String email = info != null ? info.getEmail() : "N/A";

        tvWelcome.setText("Welcome Back,");
        tvPatientName.setText(fullName);
        tvPatientId.setText("ID: PAT-" + String.format("%03d", currentUser.getId()));

        tvPatientAge.setText(dob);
        tvBloodType.setText(gender); // or bloodType if available
        tvAllergies.setText("N/A"); // No API info
        tvConditions.setText(status);

        tvGuardianName.setText("N/A"); // No guardian info in API
        tvGuardianRelationship.setText("N/A");
        tvGuardianPhone.setText(contact);
        tvGuardianEmail.setText(email);
    }

    private void performLogout() {
        authController.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
