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

/**
 * MainActivity
 *
 * Acts as the entry point after successful login.
 * Handles fragment navigation, notification permission checks,
 * logout flow, and server-side FCM token cleanup.
 */
public class MainActivity extends FragmentActivity {

    private ActivityMainBinding binding;
    private WearSharedPrefManager wearSharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wearSharedPrefManager = new WearSharedPrefManager(this);

        // If user session does not exist, redirect to LoginActivity
        if (!wearSharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Request notification permission if required (Android 13+)
        askNotificationPermission();

        // Load HomeFragment only once
        if (savedInstanceState == null) {
            initializeHomeFragment();
        }
    }

    /**
     * Loads HomeFragment into the fragment container
     */
    private void initializeHomeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    /**
     * Requests notification permission for Android API level 33 and above
     */
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean notificationsEnabled =
                    NotificationManagerCompat.from(this).areNotificationsEnabled();

            if (!notificationsEnabled) {
                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        Constants.PERMISSIONS_REQUEST_NOTIFICATIONS
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.PERMISSIONS_REQUEST_NOTIFICATIONS) {
            handleNotificationPermissionResult(grantResults);
        }
    }

    /**
     * Handles the result of the notification permission request
     */
    private void handleNotificationPermissionResult(@NonNull int[] grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            showConfirmationOverlay(
                    ConfirmationOverlay.SUCCESS_ANIMATION);
        } else {
            showConfirmationOverlay(
                    ConfirmationOverlay.FAILURE_ANIMATION);
        }
    }

    /**
     * Displays a Wear OS confirmation overlay
     */
    private void showConfirmationOverlay(int animationType) {
        new ConfirmationOverlay()
                .setType(animationType)
                .showOn(this);
    }

    /**
     * Public method triggered from UI to start logout process
     */
    public void logout() {
        showLogoutConfirmationDialog();
    }

    /**
     * Shows a confirmation dialog before logging out
     */
    private void showLogoutConfirmationDialog() {
        View dialogView =
                LayoutInflater.from(this).inflate(
                        R.layout.dialog_logout, null);

        TextView btnCancel =
                dialogView.findViewById(R.id.btnCancel);
        TextView btnConfirm =
                dialogView.findViewById(R.id.btnConfirm);

        AlertDialog dialog =
                new AlertDialog.Builder(this, R.style.WearDialogTheme)
                        .setView(dialogView)
                        .setCancelable(true)
                        .create();

        dialog.show();

        setupDialogButtonListeners(
                btnCancel, btnConfirm, dialog);
    }

    /**
     * Assigns click listeners to logout dialog buttons
     */
    private void setupDialogButtonListeners(
            TextView btnCancel,
            TextView btnConfirm,
            AlertDialog dialog) {

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            deleteWearFcmToken();
        });
    }

    /**
     * Initiates background request to remove Wear FCM token from server
     */
    private void deleteWearFcmToken() {
        int userId = wearSharedPrefManager.getUserId();

        new Thread(() -> {
            try {
                sendFcmTokenDeleteRequest(userId);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                runOnUiThread(this::proceedLogout);
            }
        }).start();
    }

    /**
     * Sends HTTP request to backend to delete FCM token
     */
    private void sendFcmTokenDeleteRequest(int userId) throws Exception {
        URL url =
                new URL(ApiConstants.getDeleteWearFcmTokenUrl());

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(
                Constants.HTTP_METHOD_POST);
        connection.setDoOutput(true);
        connection.setRequestProperty(
                Constants.HTTP_HEADER_CONTENT_TYPE,
                Constants.HTTP_CONTENT_TYPE_JSON);

        JSONObject json = new JSONObject();
        json.put(Constants.KEY_USER_ID, userId);

        OutputStream os = connection.getOutputStream();
        os.write(json.toString().getBytes());
        os.flush();
        os.close();

        connection.getResponseCode();
        connection.disconnect();
    }

    /**
     * Clears session and navigates back to LoginActivity
     */
    private void proceedLogout() {
        wearSharedPrefManager.logout();

        showConfirmationOverlay(
                ConfirmationOverlay.SUCCESS_ANIMATION);

        navigateToLoginActivity();
    }

    /**
     * Navigates to LoginActivity and clears activity stack
     */
    private void navigateToLoginActivity() {
        Intent intent =
                new Intent(this, LoginActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}