package com.example.carebridge.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carebridge.R;
import com.example.carebridge.controller.AuthController;
import com.example.carebridge.model.User;

public class GuardianDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvGuardianName, tvGuardianRole;
    private TextView tvPatientsCount, tvAppointmentsCount;
    private Button btnLogout;
    private User currentUser;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian_dashboard);

        initializeViews();
        setupUserData();
        setupClickListeners();

        authController = new AuthController(this);
    }

    private void initializeViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvGuardianName = findViewById(R.id.tvGuardianName);
        tvGuardianRole = findViewById(R.id.tvGuardianRole);
        tvPatientsCount = findViewById(R.id.tvPatientsCount);
        tvAppointmentsCount = findViewById(R.id.tvAppointmentsCount);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupUserData() {
        // Get the logged-in user from intent extras
        currentUser = (User) getIntent().getSerializableExtra("user");

        if (currentUser != null) {
            tvGuardianName.setText(currentUser.getName());
            tvGuardianRole.setText("Primary Guardian");

            // Set sample data
            tvPatientsCount.setText("2");
            tvAppointmentsCount.setText("3");
        }
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });
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
                .setIcon(R.drawable.ic_login)
                .show();
    }

    private void performLogout() {
        authController.logout();

        // Navigate back to login screen
        Intent intent = new Intent(GuardianDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

//    @Override
//    public void onBackPressed() {
//        // Show exit confirmation instead of going back
//        new AlertDialog.Builder(this)
//                .setTitle("Exit App")
//                .setMessage("Are you sure you want to exit the app?")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finishAffinity();
//                    }
//                })
//                .setNegativeButton("No", null)
//                .show();
//    }
}