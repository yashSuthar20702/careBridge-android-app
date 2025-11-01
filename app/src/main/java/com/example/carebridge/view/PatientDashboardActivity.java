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

/** Main dashboard activity for patient users with navigation and session management */
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

        // Initialize authentication controller and retrieve current user
        authController = new AuthController(this);
        currentUser = (User) getIntent().getSerializableExtra(getString(R.string.intent_user_key));
        if (currentUser == null) currentUser = authController.getCurrentUser();

        // Bind view components from layout
        tvWelcome = findViewById(R.id.tvWelcome);
        tvPatientName = findViewById(R.id.tvPatientName);
        btnHeaderLogout = findViewById(R.id.btnHeaderLogout);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Display patient name from user data
        if (currentUser != null && currentUser.getPatientInfo() != null) {
            tvPatientName.setText(currentUser.getPatientInfo().getFull_name());
        }

        // Setup ViewPager with patient dashboard fragments
        viewPager.setAdapter(new PatientDashboardPagerAdapter(this));

        // Optional: Disable swipe navigation between fragments
        // viewPager.setUserInputEnabled(false);

        // Handle bottom navigation item selection
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

        // Sync ViewPager position with bottom navigation selection
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

        // Setup logout button with confirmation dialog
        btnHeaderLogout.setOnClickListener(v ->
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
                        .show());
    }
}