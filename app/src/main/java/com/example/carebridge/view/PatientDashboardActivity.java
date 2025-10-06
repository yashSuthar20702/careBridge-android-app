package com.example.carebridge.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carebridge.R;
import com.example.carebridge.controller.AuthController;
import com.example.carebridge.model.User;
import com.example.carebridge.adapters.MedicationAdapter;
import com.example.carebridge.model.Medication;
import java.util.ArrayList;
import java.util.List;

public class PatientDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvPatientName, tvPatientId, tvHealthStatus;
    private TextView tvGuardianName, tvGuardianRelationship, tvGuardianPhone, tvGuardianEmail;
    private TextView tvPatientAge, tvBloodType, tvAllergies, tvConditions;
    private Button btnHeaderLogout, btnLogout;
    private RecyclerView rvMedications;
    private MedicationAdapter medicationAdapter;
    private User currentUser;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        initializeViews();
        setupUserData();
        setupMedications();
        setupGuardianData();
        setupPersonalInfo();
        setupClickListeners();

        authController = new AuthController(this);
    }

    private void initializeViews() {
        // Header views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvPatientName = findViewById(R.id.tvPatientName);
        tvPatientId = findViewById(R.id.tvPatientId);
        tvHealthStatus = findViewById(R.id.tvHealthStatus);
        btnHeaderLogout = findViewById(R.id.btnHeaderLogout);

        // Medication views
        rvMedications = findViewById(R.id.rvMedications);

        // Guardian views
        tvGuardianName = findViewById(R.id.tvGuardianName);
        tvGuardianRelationship = findViewById(R.id.tvGuardianRelationship);
        tvGuardianPhone = findViewById(R.id.tvGuardianPhone);
        tvGuardianEmail = findViewById(R.id.tvGuardianEmail);

        // Personal information views
        tvPatientAge = findViewById(R.id.tvPatientAge);
        tvBloodType = findViewById(R.id.tvBloodType);
        tvAllergies = findViewById(R.id.tvAllergies);
        tvConditions = findViewById(R.id.tvConditions);
    }

    private void setupUserData() {
        // Get the logged-in user from intent extras
        currentUser = (User) getIntent().getSerializableExtra("user");

        if (currentUser != null) {
            tvPatientName.setText(currentUser.getName());
            tvPatientId.setText("ID: PAT-" + String.format("%03d", currentUser.getId()));

            // You can set health status based on user data or default
            tvHealthStatus.setText("Stable");
        }
    }

    private void setupMedications() {
        // Setup RecyclerView for medications
        rvMedications.setLayoutManager(new LinearLayoutManager(this));

        // Sample data - replace with actual data from your database
        List<Medication> medicationList = getSampleMedications();

        medicationAdapter = new MedicationAdapter(medicationList);
        rvMedications.setAdapter(medicationAdapter);
    }

    private List<Medication> getSampleMedications() {
        List<Medication> medications = new ArrayList<>();

        // Sample medications - replace with actual data
        medications.add(new Medication("Paracetamol 500mg", "1 tablet - After meals", "08:00 AM"));
        medications.add(new Medication("Vitamin D3", "1 capsule - With breakfast", "09:00 AM"));
        medications.add(new Medication("Blood Pressure Med", "1 tablet - Before lunch", "12:30 PM"));
        medications.add(new Medication("Diabetes Medication", "1 injection - Evening", "06:00 PM"));

        return medications;
    }

    private void setupGuardianData() {
        // Sample guardian data - replace with actual data from your database
        tvGuardianName.setText("Sarah Johnson");
        tvGuardianRelationship.setText("Daughter");
        tvGuardianPhone.setText("+1 (555) 123-4567");
        tvGuardianEmail.setText("sarah.j@email.com");
    }

    private void setupPersonalInfo() {
        // Sample personal information - replace with actual data from your database
        tvPatientAge.setText("72 years");
        tvBloodType.setText("O+");
        tvAllergies.setText("Penicillin, Shellfish");
        tvConditions.setText("Hypertension, Diabetes");
    }

    private void setupClickListeners() {
        // Header logout button
        btnHeaderLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        // You can add click listeners for medication items if needed
        medicationAdapter.setOnItemClickListener(new MedicationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Medication medication) {
                // Handle medication item click
                showMedicationDetails(medication);
            }
        });
    }

    private void showMedicationDetails(Medication medication) {
        // Show medication details dialog or navigate to details screen
        new AlertDialog.Builder(this)
                .setTitle(medication.getName())
                .setMessage("Dosage: " + medication.getDosage() + "\nTime: " + medication.getTime())
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performLogout();
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(R.drawable.ic_logout)
                .show();
    }

    private void performLogout() {
        authController.logout();

        // Navigate back to login screen
        Intent intent = new Intent(PatientDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when activity resumes
        refreshData();
    }

    private void refreshData() {
        // Refresh medications and other data
        setupMedications();
    }
}