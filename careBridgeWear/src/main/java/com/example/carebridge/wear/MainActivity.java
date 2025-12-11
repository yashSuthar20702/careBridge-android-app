package com.example.carebridge.wear;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.wear.widget.ConfirmationOverlay;

import com.example.carebridge.shared.utils.ApiConstants;
import com.example.carebridge.wear.databinding.ActivityMainBinding;
import com.example.carebridge.wear.fragments.HomeFragment;
import com.example.carebridge.wear.utils.Constants;
import com.example.carebridge.wear.utils.WearSharedPrefManager;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends FragmentActivity {

    private ActivityMainBinding binding;
    private WearSharedPrefManager wearSharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wearSharedPrefManager = new WearSharedPrefManager(this);

        // Check if user is logged in, redirect to login if not
        if (!wearSharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Request notification permission on startup
        askNotificationPermission();

        // Set up home fragment if no saved state
        if (savedInstanceState == null) {
            initializeHomeFragment();
        }
    }

    /**
     * Initialize home fragment in container
     */
    private void initializeHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    /**
     * Request notification permission for API 33+
     */
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean notificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled();

            if (!notificationsEnabled) {
                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        Constants.PERMISSIONS_REQUEST_NOTIFICATIONS
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.PERMISSIONS_REQUEST_NOTIFICATIONS) {
            handleNotificationPermissionResult(grantResults);
        }
    }

    /**
     * Handle notification permission request result
     */
    private void handleNotificationPermissionResult(@NonNull int[] grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            showConfirmationOverlay(ConfirmationOverlay.SUCCESS_ANIMATION);
        } else {
            showConfirmationOverlay(ConfirmationOverlay.FAILURE_ANIMATION);
        }
    }

    /**
     * Show confirmation overlay with specified animation type
     */
    private void showConfirmationOverlay(int animationType) {
        new ConfirmationOverlay()
                .setType(animationType)
                .showOn(this);
    }

    /**
     * Logout user and delete FCM token
     */
    public void logout() {
        showLogoutConfirmationDialog();
    }

    /**
     * Display logout confirmation dialog
     */
    private void showLogoutConfirmationDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);

        TextView btnCancel = dialogView.findViewById(R.id.btnCancel);
        TextView btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.WearDialogTheme)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialog.show();

        setupDialogButtonListeners(btnCancel, btnConfirm, dialog);
    }

    /**
     * Setup dialog button click listeners
     */
    private void setupDialogButtonListeners(TextView btnCancel, TextView btnConfirm, AlertDialog dialog) {
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            deleteWearFcmToken();
        });
    }

    /**
     * Delete FCM token from server
     */
    private void deleteWearFcmToken() {
        int userId = wearSharedPrefManager.getUserId();

        new Thread(() -> {
            try {
                sendFcmTokenDeleteRequest(userId);
                runOnUiThread(this::proceedLogout);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(this::proceedLogout);
            }
        }).start();
    }

    /**
     * Send HTTP request to delete FCM token
     */
    private void sendFcmTokenDeleteRequest(int userId) throws Exception {
        URL url = new URL(ApiConstants.getDeleteWearFcmTokenUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod(Constants.HTTP_METHOD_POST);
        conn.setDoOutput(true);
        conn.setRequestProperty(Constants.HTTP_HEADER_CONTENT_TYPE, Constants.HTTP_CONTENT_TYPE_JSON);

        JSONObject json = new JSONObject();
        json.put(Constants.KEY_USER_ID, userId);

        OutputStream os = conn.getOutputStream();
        os.write(json.toString().getBytes());
        os.flush();
        os.close();

        conn.getResponseCode(); // Ensure request completes
        conn.disconnect();
    }

    /**
     * Proceed with logout after token deletion
     */
    private void proceedLogout() {
        wearSharedPrefManager.logout();

        showConfirmationOverlay(ConfirmationOverlay.SUCCESS_ANIMATION);

        navigateToLoginActivity();
    }

    /**
     * Navigate to login activity with clear task flags
     */
    private void navigateToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}