package com.example.carebridge.view;

import android.content.Intent;
import android.os.Bundle;
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
        setContentView(R.layout.activity_guardian_dashboard_tabs);

        authController = new AuthController(this);
        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser == null) currentUser = authController.getCurrentUser();

        // ✅ Match correct IDs from XML
        viewPager = findViewById(R.id.viewPagerGuardian);
        bottomNavigationView = findViewById(R.id.bottomNavigationGuardian);
        tvGuardianName = findViewById(R.id.tvGuardianName);
        btnLogout = findViewById(R.id.btnHeaderLogout);

        // ✅ Set Guardian Name
        if (currentUser != null && currentUser.getPatientInfo().getFull_name() != null) {
            tvGuardianName.setText(currentUser.getPatientInfo().getFull_name());
        }

        // ✅ Set Logout Click
        btnLogout.setOnClickListener(v -> logout());

        // ✅ ViewPager Setup
        viewPager.setAdapter(new GuardianDashboardPagerAdapter(this));
        viewPager.setUserInputEnabled(false);

        // ✅ Bottom Navigation Setup
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

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    authController.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
    }
}
