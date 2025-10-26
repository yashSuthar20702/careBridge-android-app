package com.example.carebridge.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.carebridge.R;
import com.example.carebridge.adapters.PatientDashboardPagerAdapter;
import com.example.carebridge.controller.AuthController;
import com.example.carebridge.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.TextView;
import android.widget.Button;

public class PatientDashboardActivity extends AppCompatActivity {

    private TextView tvPatientName, tvWelcome;
    private Button btnHeaderLogout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private AuthController authController;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        // Initialize controllers
        authController = new AuthController(this);
        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser == null) currentUser = authController.getCurrentUser();

        // Bind views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvPatientName = findViewById(R.id.tvPatientName);
        btnHeaderLogout = findViewById(R.id.btnHeaderLogout);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set patient name
        if (currentUser != null && currentUser.getPatientInfo() != null) {
            tvPatientName.setText(currentUser.getPatientInfo().getFull_name());
        }

        // Setup ViewPager
        viewPager.setAdapter(new PatientDashboardPagerAdapter(this));

        // Disable swipe if needed (optional)
        // viewPager.setUserInputEnabled(false);

        // Bottom Navigation → ViewPager
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

        // ViewPager → Bottom Navigation
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_personal);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_guardian);
                        break;
                }
            }
        });

        // Logout
        btnHeaderLogout.setOnClickListener(v ->
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
                        .show());
    }
}
