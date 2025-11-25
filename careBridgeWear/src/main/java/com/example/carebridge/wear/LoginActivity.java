package com.example.carebridge.wear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carebridge.wear.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "CareBridgePrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            startMainActivity();
            return;
        }

        setupViews();
    }

    private void setupViews() {
        // Setup login button
        binding.loginButton.setOnClickListener(v -> attemptLogin());

        // Setup keyboard actions
        binding.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // Optional: Next button on username field
        binding.usernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    binding.passwordEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private void attemptLogin() {
        String username = binding.usernameEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        // Validation
        if (username.isEmpty()) {
            showError("Please enter username");
            binding.usernameEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter password");
            binding.passwordEditText.requestFocus();
            return;
        }

        // For demo - accept any non-empty credentials
        performLogin();
    }

    private void performLogin() {
        // Show loading state
        binding.loginButton.setEnabled(false);
        binding.loginButton.setText("LOGGING IN...");

        // Simulate login process
        binding.loginButton.postDelayed(() -> {
            // Save login state
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString("username", binding.usernameEditText.getText().toString().trim());
            editor.apply();

            // Show success message
            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

            startMainActivity();
        }, 1000);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        binding.errorText.setText(message);
        binding.errorText.setVisibility(View.VISIBLE);

        // Hide error after 3 seconds
        binding.errorText.postDelayed(() -> {
            binding.errorText.setVisibility(View.GONE);
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset button state when returning to login
        binding.loginButton.setEnabled(true);
        binding.loginButton.setText("LOGIN");
    }
}