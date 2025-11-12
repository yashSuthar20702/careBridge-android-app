package com.example.carebridge.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.carebridge.R;
import com.example.carebridge.adapters.GuardianDashboardPagerAdapter;
import com.example.carebridge.controller.AuthController;
import com.example.carebridge.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class GuardianDashboardActivity extends AppCompatActivity {

    private static final String TAG = "GuardianDashboard";

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private AuthController authController;
    private User currentUser;

    private TextView tvGuardianName;
    private Button btnLogout;

    // ✅ Launcher for runtime notification permission
    private final ActivityResultLauncher<String> requestNotificationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "✅ Notification permission granted by user");
                } else {
                    Log.w(TAG, "⚠️ Notification permission denied by user");
                }
            });

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

        // Set guardian name safely
        try {
            if (currentUser != null &&
                    currentUser.getPatientInfo() != null &&
                    currentUser.getPatientInfo().getFull_name() != null) {
                tvGuardianName.setText(currentUser.getPatientInfo().getFull_name());
            } else {
                tvGuardianName.setText(getString(R.string.guardian_fallback_name));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting name: " + e.getMessage());
        }

        btnLogout.setOnClickListener(v -> openLogoutDialog());

        // Setup ViewPager + Bottom Nav
        viewPager.setAdapter(new GuardianDashboardPagerAdapter(this));
        viewPager.setUserInputEnabled(false);

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

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: bottomNavigationView.setSelectedItemId(R.id.nav_home); break;
                    case 1: bottomNavigationView.setSelectedItemId(R.id.nav_personal); break;
                    case 2: bottomNavigationView.setSelectedItemId(R.id.nav_patients); break;
                }
            }
        });

        // ✅ Ask for notification permission only here, after login
        requestNotificationPermissionIfNeeded();
    }

    /** Ask for POST_NOTIFICATIONS permission only on Android 13+ */
    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "🔔 Requesting notification permission");
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                Log.d(TAG, "✅ Notification permission already granted");
            }
        }
    }

    /** Custom Logout Dialog */
    private void openLogoutDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnLogout = dialogView.findViewById(R.id.btnLogout);

        btnLogout.setBackgroundTintList(getColorStateList(R.color.accent_blue));

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialogStyle)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

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
