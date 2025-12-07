package com.example.carebridge.wear;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carebridge.shared.controller.AuthController;
import com.example.carebridge.shared.model.User;
import com.example.carebridge.wear.databinding.ActivityLoginBinding;
import com.example.carebridge.wear.utils.WearSharedPrefManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "WearLoginActivity";

    private ActivityLoginBinding binding;
    private AuthController authController;
    private WearSharedPrefManager wearSharedPrefManager;

    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ontario timezone for live time
        timeFormat.setTimeZone(TimeZone.getTimeZone("America/Toronto"));

        authController = new AuthController(this);
        wearSharedPrefManager = new WearSharedPrefManager(this);

        // Redirect if already logged in
        User currentUser = wearSharedPrefManager.getCurrentUser();
        if (currentUser != null) {
            redirectToDashboard(currentUser);
            return;
        }

        setupViews();
        startClockUpdater();
    }

    private void setupViews() {
        binding.loginButton.setOnClickListener(v -> attemptLogin());

        binding.passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        binding.usernameEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.passwordEditText.requestFocus();
                return true;
            }
            return false;
        });
    }

    private void attemptLogin() {
        String username = binding.usernameEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (username.isEmpty()) {
            showError(getString(R.string.enter_username));
            binding.usernameEditText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showError(getString(R.string.enter_password));
            binding.passwordEditText.requestFocus();
            return;
        }

        performLogin(username, password);
    }

    private void performLogin(String username, String password) {
        binding.loginButton.setEnabled(false);
        binding.loginButton.setText(getString(R.string.logging_in));

        authController.login(username, password, new AuthController.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                // Save user session
                wearSharedPrefManager.saveUserSession(user);

                // Initialize Firebase
                FirebaseApp.initializeApp(LoginActivity.this);

                // Generate FCM token
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                wearSharedPrefManager.saveReferenceId(task.getResult());
                                Log.d(TAG, "FCM Token: " + task.getResult());
                            } else {
                                Log.w(TAG, "Fetching FCM token failed", task.getException());
                            }
                            redirectToDashboard(user);
                        });
            }

            @Override
            public void onFailure(String message) {
                binding.loginButton.setEnabled(true);
                binding.loginButton.setText(getString(R.string.login));
                showError(message);
            }
        });
    }

    private void showError(String message) {
        binding.errorText.setText(message);
        binding.errorText.setVisibility(android.view.View.VISIBLE);
        binding.errorText.postDelayed(() -> binding.errorText.setVisibility(android.view.View.GONE), 3000);
    }

    private void redirectToDashboard(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    private void startClockUpdater() {
        timeHandler.post(new Runnable() {
            @Override
            public void run() {
                if (binding != null) {
                    binding.tvLiveTime.setText(timeFormat.format(new Date()));
                }
                timeHandler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.loginButton.setEnabled(true);
        binding.loginButton.setText(getString(R.string.login));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacksAndMessages(null);
    }
}