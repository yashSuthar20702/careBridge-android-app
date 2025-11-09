package com.example.carebridge.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.carebridge.R;
import com.example.carebridge.adapters.PatientDashboardPagerAdapter;
import com.example.carebridge.controller.AuthController;
import com.example.carebridge.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

/** Main dashboard activity for patient users with navigation and session management */
public class PatientDashboardActivity extends AppCompatActivity {

    private TextView tvPatientName, tvWelcome;
    private MaterialButton btnHeaderLogout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private AuthController authController;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        authController = new AuthController(this);
        currentUser = (User) getIntent().getSerializableExtra(getString(R.string.intent_user_key));
        if (currentUser == null) currentUser = authController.getCurrentUser();

        tvWelcome = findViewById(R.id.tvWelcome);
        tvPatientName = findViewById(R.id.tvPatientName);
        btnHeaderLogout = findViewById(R.id.btnHeaderLogout);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        if (currentUser != null && currentUser.getPatientInfo() != null) {
            tvPatientName.setText(currentUser.getPatientInfo().getFull_name());
        }

        viewPager.setAdapter(new PatientDashboardPagerAdapter(this));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.nav_personal) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.nav_guardian) {
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0: bottomNavigationView.setSelectedItemId(R.id.nav_home); break;
                    case 1: bottomNavigationView.setSelectedItemId(R.id.nav_personal); break;
                    case 2: bottomNavigationView.setSelectedItemId(R.id.nav_guardian); break;
                }
            }
        });

        // Custom logout dialog
        btnHeaderLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);

        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnLogout = dialogView.findViewById(R.id.btnLogout);

        //  Guardian → Accent Blue
        btnLogout.setBackgroundTintList(
                getColorStateList(R.color.status_active)
        );

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialogStyle)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnLogout.setOnClickListener(v -> {
            dialog.dismiss();
            authController.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        dialog.show();
    }
}
