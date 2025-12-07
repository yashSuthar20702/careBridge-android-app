package com.example.carebridge.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Data;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActionReceiver extends BroadcastReceiver {

    private static final String TAG = "ACTION_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Log the entire intent for debugging
        Bundle extras = intent.getExtras();
        Log.d(TAG, "Intent received: " + intent.getAction() + " | extras: " + (extras != null ? extras.toString() : "null"));

        // Extract log_id and status
        String logIdStr = intent.getStringExtra("log_id");
        String takenStatus = intent.getStringExtra("taken_status");

        if (logIdStr == null || takenStatus == null) {
            Log.e(TAG, "Missing log_id or taken_status in intent");
            return;
        }

        int logId;
        try {
            logId = Integer.parseInt(logIdStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid log_id: " + logIdStr);
            return;
        }

        Log.d(TAG, "Action Received → Log #" + logId + " | " + takenStatus);

        // Show toast immediately
        Toast.makeText(context, "Medicine " + takenStatus, Toast.LENGTH_SHORT).show();

        // Call API
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.updateMedicineStatus(String.valueOf(logId), takenStatus).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() == null || !response.isSuccessful()) {
                    Log.e(TAG, "API null or failed response, fallback triggered");
                    runWorkManagerFallback(context, logId, takenStatus);
                    return;
                }

                Log.d(TAG, "API Response: " + response.body());

                // Clear the notification after API success
                NotificationHelper.clearNotification(context, logId);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "API Failed: " + t.getMessage());
                runWorkManagerFallback(context, logId, takenStatus);
            }
        });
    }

    private void runWorkManagerFallback(Context context, int logId, String status) {
        Data data = new Data.Builder()
                .putInt("log_id", logId)
                .putString("taken_status", status)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(MedicineStatusWorker.class)
                .setInputData(data)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueue(request);

        Log.d(TAG, "WorkManager Fallback Triggered for Log #" + logId);
    }
}
