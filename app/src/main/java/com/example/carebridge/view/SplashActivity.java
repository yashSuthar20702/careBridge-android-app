package com.example.carebridge.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carebridge.R;
import com.example.carebridge.utils.SharedPrefManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPrefManager = new SharedPrefManager(this);

        new Handler().postDelayed(() -> {
            checkUserSession();
        }, SPLASH_DELAY);
    }

    private void checkUserSession() {
        if (sharedPrefManager.isLoggedIn()) {
            // User is logged in, go to appropriate dashboard
            redirectToDashboard();
        } else {
            // User is not logged in, go to login
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish();
    }

    private void redirectToDashboard() {
        Intent intent;
        com.example.carebridge.model.User user = sharedPrefManager.getCurrentUser();

        if (user != null && user.getRole().equals("Patient")) {
            intent = new Intent(SplashActivity.this, PatientDashboardActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, GuardianDashboardActivity.class);
        }

        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
}