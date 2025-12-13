package com.example.carebridge.wear;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

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

/**
 * LoginActivity
 *
 * Handles user authentication on Wear OS.
 * Includes input validation, session persistence,
 * Firebase Cloud Messaging registration, and
 * notification permission handling.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthController authController;
    private WearSharedPrefManager wearSharedPrefManager;

    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat(Constants.TIME_FORMAT_HH_MM_A, Locale.getDefault());

    /**
     * Notification permission launcher for Android 13+
     */
    private final ActivityResultLauncher<String> requestNotificationPermission =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            Log.d(Constants.TAG_LOGIN_ACTIVITY,
                                    "Notification permission granted");
                        } else {
                            Log.w(Constants.TAG_LOGIN_ACTIVITY,
                                    "Notification permission denied");
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        timeFormat.setTimeZone(
                TimeZone.getTimeZone(Constants.TIMEZONE_TORONTO));

        authController = new AuthController(this);
        wearSharedPrefManager = new WearSharedPrefManager(this);

        if (wearSharedPrefManager.getCurrentUser() != null) {
            redirectToDashboard(
                    wearSharedPrefManager.getCurrentUser());
            return;
        }

        initializeViews();
        setupKeyboardListeners();
        startClockUpdater();
    }

    /**
     * Initialize UI components and actions
     */
    private void initializeViews() {
        binding.loginButton.setOnClickListener(
                v -> attemptLogin());

        binding.usernameEditText.setImeOptions(
                EditorInfo.IME_ACTION_NEXT
                        | EditorInfo.IME_FLAG_NO_ENTER_ACTION);

        binding.passwordEditText.setImeOptions(
                EditorInfo.IME_ACTION_DONE
                        | EditorInfo.IME_FLAG_NO_ENTER_ACTION);
    }

    /**
     * Configure keyboard navigation for Wear OS
     */
    private void setupKeyboardListeners() {

        binding.usernameEditText.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_NEXT
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || isEnterKey(event)) {
                        binding.passwordEditText.requestFocus();
                        return true;
                    }
                    return false;
                });

        binding.passwordEditText.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE
                            || actionId == EditorInfo.IME_ACTION_NEXT
                            || isEnterKey(event)) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                });

        binding.getRoot().setOnClickListener(
                v -> hideKeyboard());
    }

    private boolean isEnterKey(KeyEvent event) {
        return event != null
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                && event.getAction() == KeyEvent.ACTION_DOWN;
    }

    private void hideKeyboard() {
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager)
                            getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(
                        focusedView.getWindowToken(), 0);
            }
            focusedView.clearFocus();
        }
    }

    /**
     * Validate input fields before login
     */
    private void attemptLogin() {
        String username =
                binding.usernameEditText.getText().toString().trim();
        String password =
                binding.passwordEditText.getText().toString().trim();

        binding.errorText.setVisibility(View.GONE);

        if (TextUtils.isEmpty(username)) {
            showError(getString(R.string.enter_username));
            binding.usernameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showError(getString(R.string.enter_password));
            binding.passwordEditText.requestFocus();
            return;
        }

        performLogin(username, password);
    }

    /**
     * Perform authentication using backend service
     */
    private void performLogin(String username, String password) {
        hideKeyboard();

        binding.loginButton.setEnabled(false);
        binding.loginButton.setText(R.string.logging_in);

        authController.login(username, password,
                new AuthController.LoginCallback() {

                    @Override
                    public void onSuccess(User user) {
                        wearSharedPrefManager.saveUserSession(user);

                        FirebaseApp.initializeApp(LoginActivity.this);

                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(task -> {

                                    if (task.isSuccessful()
                                            && task.getResult() != null) {

                                        String token = task.getResult();
                                        wearSharedPrefManager
                                                .saveReferenceId(token);

                                        authController.sendFcmTokenToServer(
                                                user.getId(),
                                                token,
                                                true);
                                    }

                                    requestNotificationPermissionIfNeeded();
                                    redirectToDashboard(user);
                                });
                    }

                    @Override
                    public void onFailure(String message) {
                        binding.loginButton.setEnabled(true);
                        binding.loginButton.setText(R.string.login);
                        showError(message);
                        binding.passwordEditText.requestFocus();
                    }
                });
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestNotificationPermission.launch(
                        Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void showError(String msg) {
        binding.errorText.setText(msg);
        binding.errorText.setVisibility(View.VISIBLE);
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
                binding.tvLiveTime.setText(
                        timeFormat.format(new Date()));
                timeHandler.postDelayed(
                        this, Constants.UPDATE_INTERVAL_FAST);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacksAndMessages(null);
    }
}