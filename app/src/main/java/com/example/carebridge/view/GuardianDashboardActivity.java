package com.example.carebridge.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

/** Main dashboard activity for guardian users with navigation and session management */
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

        // Initialize authentication controller and retrieve current user
        authController = new AuthController(this);
        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser == null) {
            currentUser = authController.getCurrentUser();
        }

        // Bind view components from layout
        tvGuardianName = findViewById(R.id.tvGuardianName);
        btnLogout = findViewById(R.id.btnHeaderLogout);
        viewPager = findViewById(R.id.viewPagerGuardian);
        bottomNavigationView = findViewById(R.id.bottomNavigationGuardian);

        // Debug logging for view binding issues
        if (btnLogout == null) Log.e("GuardianDashboard", getString(R.string.log_btn_logout_not_found));
        if (tvGuardianName == null) Log.e("GuardianDashboard", getString(R.string.log_tv_name_not_found));
        if (viewPager == null) Log.e("GuardianDashboard", getString(R.string.log_viewpager_not_found));
        if (bottomNavigationView == null) Log.e("GuardianDashboard", getString(R.string.log_bottom_nav_not_found));

        // Set guardian name with fallback for missing data
        try {
            if (tvGuardianName != null && currentUser != null && currentUser.getPatientInfo().getFull_name() != null) {
                tvGuardianName.setText(currentUser.getPatientInfo().getFull_name());
            } else if (tvGuardianName != null) {
                tvGuardianName.setText(getString(R.string.guardian_fallback_name));
            }
        } catch (Exception e) {
            Log.e("GuardianDashboard", getString(R.string.log_error_setting_name) + e.getMessage());
        }

        // Setup logout button click listener
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logout());
        }

        // Configure ViewPager for fragment navigation
        if (viewPager != null) {
            viewPager.setAdapter(new GuardianDashboardPagerAdapter(this));
            viewPager.setUserInputEnabled(false); // Disable swipe navigation
        }

        // Setup bottom navigation item selection
        if (bottomNavigationView != null) {
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
        }

        // Sync ViewPager position with bottom navigation
        if (viewPager != null) {
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    if (bottomNavigationView == null) return;
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
    }

    /** Handle user logout with confirmation dialog */
    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout_title))
                .setMessage(getString(R.string.logout_confirmation))
                .setPositiveButton(getString(R.string.yes_button), (dialog, which) -> {
                    authController.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton(getString(R.string.no_button), null)
                .show();
    }
}