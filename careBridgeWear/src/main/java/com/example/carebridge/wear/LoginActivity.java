package com.example.carebridge.wear;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.carebridge.shared.controller.AuthController;
import com.example.carebridge.shared.model.User;
import com.example.carebridge.wear.databinding.ActivityLoginBinding;
import com.example.carebridge.wear.utils.Constants;
import com.example.carebridge.wear.utils.WearSharedPrefManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthController authController;
    private WearSharedPrefManager wearSharedPrefManager;

    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT_HH_MM_A, Locale.getDefault());

    // Notification permission launcher for Android 13+
    private final ActivityResultLauncher<String> requestNotificationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(Constants.TAG_LOGIN_ACTIVITY, "✅ Notification permission granted");
                } else {
                    Log.w(Constants.TAG_LOGIN_ACTIVITY, "⚠️ Notification permission denied");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        timeFormat.setTimeZone(TimeZone.getTimeZone(Constants.TIMEZONE_TORONTO));

        authController = new AuthController(this);
        wearSharedPrefManager = new WearSharedPrefManager(this);

        // Check if user is already logged in
        if (wearSharedPrefManager.getCurrentUser() != null) {
            redirectToDashboard(wearSharedPrefManager.getCurrentUser());
            return;
        }

        initializeViews();
        startClockUpdater();
    }

    private void initializeViews() {
        binding.loginButton.setOnClickListener(v -> attemptLogin());

        binding.passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
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
            return;
        }
        if (password.isEmpty()) {
            showError(getString(R.string.enter_password));
            return;
        }

        performLogin(username, password);
    }

    private void performLogin(String username, String password) {
        binding.loginButton.setEnabled(false);
        binding.loginButton.setText(R.string.logging_in);

        authController.login(username, password, new AuthController.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                wearSharedPrefManager.saveUserSession(user);

                FirebaseApp.initializeApp(LoginActivity.this);

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                String token = task.getResult();
                                Log.d(Constants.TAG_LOGIN_ACTIVITY, Constants.LOG_EMOJI_SUCCESS + " FCM Token (Wear): " + token);

                                wearSharedPrefManager.saveReferenceId(token);

                                // Send Wear FCM token to backend
                                authController.sendFcmTokenToServer(user.getId(), token, true);
                            } else {
                                Log.w(Constants.TAG_LOGIN_ACTIVITY, Constants.LOG_EMOJI_ERROR + " FCM token fetch failed", task.getException());
                            }

                            // ✅ Request notification permission after login success
                            requestNotificationPermissionIfNeeded();

                            redirectToDashboard(user);
                        });
            }

            @Override
            public void onFailure(String message) {
                binding.loginButton.setEnabled(true);
                binding.loginButton.setText(R.string.login);
                showError(message);
            }
        });
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                Log.d(Constants.TAG_LOGIN_ACTIVITY, "✅ Notification permission already granted");
            }
        }
    }

    private void showError(String msg) {
        binding.errorText.setText(msg);
        binding.errorText.setVisibility(View.VISIBLE);
        binding.errorText.postDelayed(() -> binding.errorText.setVisibility(View.GONE), Constants.UPDATE_INTERVAL_MEDIUM);
    }

    private void redirectToDashboard(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.EXTRA_USER, user);
        startActivity(intent);
        finish();
    }

    private void startClockUpdater() {
        timeHandler.post(new Runnable() {
            @Override
            public void run() {
                binding.tvLiveTime.setText(timeFormat.format(new Date()));
                timeHandler.postDelayed(this, Constants.UPDATE_INTERVAL_FAST);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacksAndMessages(null);
    }
}
