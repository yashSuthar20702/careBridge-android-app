package com.example.carebridge.wear;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carebridge.shared.controller.AuthController;
import com.example.carebridge.shared.model.User;
import com.example.carebridge.wear.databinding.ActivityLoginBinding;
import com.example.carebridge.wear.utils.Constants;
import com.example.carebridge.wear.utils.WearSharedPrefManager;
import com.example.carebridge.shared.utils.ApiConstants;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthController authController;
    private WearSharedPrefManager wearSharedPrefManager;

    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT_HH_MM_A, Locale.getDefault());
    private final OkHttpClient client = new OkHttpClient();

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

    /**
     * Initialize UI components and set up listeners
     */
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

    /**
     * Validate and attempt user login
     */
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

    /**
     * Perform login operation with provided credentials
     */
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

                                // Send FCM token to backend
                                sendFcmTokenToServer(user.getId(), token);
                            } else {
                                Log.w(Constants.TAG_LOGIN_ACTIVITY, Constants.LOG_EMOJI_ERROR + " FCM token fetch failed", task.getException());
                            }

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

    /**
     * Send FCM token to backend server
     */
    private void sendFcmTokenToServer(int userId, String fcmToken) {
        try {
            JSONObject json = new JSONObject();
            json.put(Constants.KEY_USER_ID, userId);
            json.put(Constants.KEY_FCM_TOKEN, fcmToken);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.get(Constants.HTTP_CONTENT_TYPE_JSON_CHARSET)
            );

            Request request = new Request.Builder()
                    .url(ApiConstants.getUpdateWearFcmTokenUrl())
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(Constants.TAG_LOGIN_ACTIVITY, Constants.LOG_EMOJI_ERROR + " Wear failed to update FCM token: " + e.getMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    Log.d(Constants.TAG_LOGIN_ACTIVITY, Constants.LOG_EMOJI_SUCCESS + " Wear FCM token update response: " + response.body().string());
                }
            });
        } catch (Exception e) {
            Log.e(Constants.TAG_LOGIN_ACTIVITY, Constants.LOG_EMOJI_ERROR + " FCM token JSON building error: " + e.getMessage());
        }
    }

    /**
     * Display error message to user
     */
    private void showError(String msg) {
        binding.errorText.setText(msg);
        binding.errorText.setVisibility(View.VISIBLE);
        binding.errorText.postDelayed(() -> binding.errorText.setVisibility(View.GONE), Constants.UPDATE_INTERVAL_MEDIUM);
    }

    /**
     * Redirect to main dashboard after successful login
     */
    private void redirectToDashboard(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.EXTRA_USER, user);
        startActivity(intent);
        finish();
    }

    /**
     * Start updating clock display
     */
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