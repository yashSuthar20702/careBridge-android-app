package com.example.carebridge.wear.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WearNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "WearNotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null) {
            Log.e(TAG, "Received null intent");
            return;
        }

        String logId = intent.getStringExtra("log_id");
        String status = intent.getStringExtra("taken_status");

        if (logId == null || status == null) {
            Log.e(TAG, "Missing log_id or taken_status in intent");
            return;
        }

        Log.d(TAG, "Received action → logId=" + logId + " | status=" + status);

        // Show toast
        Toast.makeText(context, "Medicine " + status, Toast.LENGTH_SHORT).show();

        // Dismiss the notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            manager.cancel(Integer.parseInt(logId.trim()));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid logId for notification cancel", e);
        }

        WearApiService api = WearApiClient.getClient().create(WearApiService.class);

        // Try to update the server directly
        api.updateMedicineStatus(logId, status).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Status updated successfully → " + status);
                } else {
                    Log.e(TAG, "Failed to update status → " + response.code());
                    runFallback(context, logId, status);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "API call failed for logId=" + logId, t);
                runFallback(context, logId, status);
            }
        });
    }

    /**
     * Fallback: enqueue a WorkManager job to retry the update
     */
    private void runFallback(Context context, String logId, String status) {
        Data data = new Data.Builder()
                .putString("log_id", logId)
                .putString("taken_status", status)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(WearSyncWorker.class)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueue(request);

        Log.d(TAG, "Fallback WorkManager enqueued for logId=" + logId + " | status=" + status);
    }
}
