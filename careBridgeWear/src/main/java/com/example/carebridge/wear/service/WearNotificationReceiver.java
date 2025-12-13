package com.example.carebridge.wear.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.carebridge.wear.R;
import com.example.carebridge.wear.utils.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * WearNotificationReceiver

 * Handles actions from Wear OS notification buttons
 * such as "Taken" and "Not Taken".

 * Updates medicine status on the server.
 */
public class WearNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "WearNotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (context == null || intent == null) {
            Log.e(TAG, "Context or Intent is null");
            return;
        }

        String logId = intent.getStringExtra(Constants.KEY_LOG_ID);
        String status = intent.getStringExtra(Constants.KEY_TAKEN_STATUS);

        if (logId == null || status == null) {
            Log.e(TAG, "Missing log ID or status");
            return;
        }

        Log.d(TAG, "Action received → logId=" + logId + ", status=" + status);

        showUserFeedback(context, status);
        dismissNotification(context, logId);
        updateMedicineStatus(context, logId, status);
    }

    /**
     * Shows feedback to user.
     */
    private void showUserFeedback(@NonNull Context context, @NonNull String status) {
        Toast.makeText(
                context,
                context.getString(R.string.toast_medicine_status, status),
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * Dismisses the notification.
     */
    private void dismissNotification(@NonNull Context context, @NonNull String logId) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager == null) return;

        try {
            manager.cancel(Integer.parseInt(logId.trim()));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid logId for notification cancel", e);
        }
    }

    /**
     * Calls backend API to update medicine status.
     */
    private void updateMedicineStatus(
            @NonNull Context context,
            @NonNull String logId,
            @NonNull String status
    ) {

        WearApiService api =
                WearApiClient.getClient().create(WearApiService.class);

        api.updateMedicineStatus(logId, status)
                .enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<JsonObject> call,
                            @NonNull Response<JsonObject> response
                    ) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Medicine status updated successfully");
                        } else {
                            Log.e(TAG, "Update failed → " + response.code());
                            runFallback(context, logId, status);
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonObject> call,
                            @NonNull Throwable t
                    ) {
                        Log.e(TAG, "API call failed", t);
                        runFallback(context, logId, status);
                    }
                });
    }

    /**
     * Fallback using WorkManager if API fails.
     */
    private void runFallback(
            @NonNull Context context,
            @NonNull String logId,
            @NonNull String status
    ) {

        Data data = new Data.Builder()
                .putString(Constants.KEY_LOG_ID, logId)
                .putString(Constants.KEY_TAKEN_STATUS, status)
                .build();

        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(WearSyncWorker.class)
                        .setInputData(data)
                        .build();

        WorkManager.getInstance(context).enqueue(request);

        Log.d(TAG, "Fallback WorkManager scheduled");
    }
}