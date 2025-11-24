package com.example.carebridge.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carebridge.R;
import com.example.carebridge.shared.utils.SharedPrefManager;

/** Splash screen activity showing app branding while checking user authentication status */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3-second display time
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPrefManager = new SharedPrefManager(this);

        // Delay splash screen before checking authentication status
        new Handler().postDelayed(() -> {
            checkUserSession();
        }, SPLASH_DELAY);
    }

    /** Check if user has an active session and redirect accordingly */
    private void checkUserSession() {
        if (sharedPrefManager.isLoggedIn()) {
            // User has active session, redirect to appropriate dashboard
            redirectToDashboard();
        } else {
            // No active session, redirect to login screen
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish(); // Close splash activity after redirection
    }

    /** Redirect to patient or guardian dashboard based on user role */
    private void redirectToDashboard() {
        Intent intent;
        com.example.carebridge.shared.model.User user = sharedPrefManager.getCurrentUser();

        // Determine dashboard based on user role
        if (user != null && user.getRole().equals(getString(R.string.patient_role))) {
            intent = new Intent(SplashActivity.this, PatientDashboardActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, GuardianDashboardActivity.class);
        }

        intent.putExtra(getString(R.string.intent_user_key), user);
        startActivity(intent);
        finish(); // Close splash activity
    }
}