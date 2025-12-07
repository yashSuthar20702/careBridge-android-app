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
import com.example.carebridge.wear.utils.WearSharedPrefManager;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends FragmentActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST = 200;
    private ActivityMainBinding binding;
    private WearSharedPrefManager wearSharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wearSharedPrefManager = new WearSharedPrefManager(this);

        if (!wearSharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 🔥 Ask Notification Permission on startup
        askNotificationPermission();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    /**
     * =========================================
     * 🔔 ASK NOTIFICATION PERMISSION (API 33+)
     * =========================================
     */
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean enabled = NotificationManagerCompat.from(this).areNotificationsEnabled();

            if (!enabled) {
                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST
                );
            }
        }
    }

    // Handle Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                new ConfirmationOverlay()
                        .setType(ConfirmationOverlay.SUCCESS_ANIMATION)
                        .showOn(this);

            } else {
                // User denied → Optional UI alert
                new ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .showOn(this);
            }
        }
    }

    /**
     * =========================================
     * LOGOUT + DELETE TOKEN
     * =========================================
     */

    public void logout() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);

        TextView btnCancel = dialogView.findViewById(R.id.btnCancel);
        TextView btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        AlertDialog dialog =
                new AlertDialog.Builder(this, R.style.WearDialogTheme)
                        .setView(dialogView)
                        .setCancelable(true)
                        .create();

        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            deleteWearFcmToken();
        });
    }

    private void deleteWearFcmToken() {
        int userId = wearSharedPrefManager.getUserId();

        new Thread(() -> {
            try {
                URL url = new URL(ApiConstants.getDeleteWearFcmTokenUrl());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();
                json.put("user_id", userId);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                conn.disconnect();

                runOnUiThread(this::proceedLogout);

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(this::proceedLogout);
            }
        }).start();
    }

    private void proceedLogout() {
        wearSharedPrefManager.logout();

        new ConfirmationOverlay()
                .setType(ConfirmationOverlay.SUCCESS_ANIMATION)
                .showOn(this);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }
}
