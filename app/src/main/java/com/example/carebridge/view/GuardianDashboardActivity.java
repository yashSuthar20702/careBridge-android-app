package com.example.carebridge.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.carebridge.R;
import com.example.carebridge.adapters.GuardianDashboardPagerAdapter;
import com.example.carebridge.controller.AuthController;
import com.example.carebridge.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class GuardianDashboardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private AuthController authController;
    private User currentUser;

    private TextView tvGuardianName;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian_dashboard);

        authController = new AuthController(this);
        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser == null) currentUser = authController.getCurrentUser();

        tvGuardianName = findViewById(R.id.tvGuardianName);
        btnLogout = findViewById(R.id.btnHeaderLogout);
        viewPager = findViewById(R.id.viewPagerGuardian);
        bottomNavigationView = findViewById(R.id.bottomNavigationGuardian);

        // Debug check
        if (btnLogout == null) Log.e("GuardianDashboard", "Logout button not found");
        if (tvGuardianName == null) Log.e("GuardianDashboard", "Guardian name textview not found");
        if (viewPager == null) Log.e("GuardianDashboard", "ViewPager not found");
        if (bottomNavigationView == null) Log.e("GuardianDashboard", "BottomNav not found");

        // Set guardian name
        try {
            if (currentUser != null &&
                    currentUser.getPatientInfo() != null &&
                    currentUser.getPatientInfo().getFull_name() != null) {

                tvGuardianName.setText(currentUser.getPatientInfo().getFull_name());
            } else {
                tvGuardianName.setText(getString(R.string.guardian_fallback_name));
            }
        } catch (Exception e) {
            Log.e("GuardianDashboard", "Error setting name: " + e.getMessage());
        }

        // Logout
        btnLogout.setOnClickListener(v -> openLogoutDialog());

        // ViewPager Setup
        viewPager.setAdapter(new GuardianDashboardPagerAdapter(this));
        viewPager.setUserInputEnabled(false); //  swipe enabled

        // Bottom Nav navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (id == R.id.nav_personal) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (id == R.id.nav_patients) {
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        });

        // Sync ViewPager with BottomNav
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_personal);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_patients);
                        break;
                }
            }
        });
    }

    /**  Custom Logout Dialog (Blue Accent) */
    private void openLogoutDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);

        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnLogout = dialogView.findViewById(R.id.btnLogout);

        //  Guardian → Accent Blue
        btnLogout.setBackgroundTintList(
                getColorStateList(R.color.accent_blue)
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
