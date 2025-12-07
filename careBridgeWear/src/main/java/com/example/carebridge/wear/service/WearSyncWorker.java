package com.example.carebridge.wear.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.*;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WearSyncWorker extends Worker {

    private static final String TAG = "WearSyncWorker";

    public WearSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String logId = getInputData().getString("log_id");
        String status = getInputData().getString("taken_status");

        if (logId == null || status == null) {
            Log.e(TAG, "Missing log_id or taken_status");
            return Result.failure();
        }

        Log.d(TAG, "Starting sync for logId=" + logId + " status=" + status);

        WearApiService api = WearApiClient.getClient().create(WearApiService.class);

        // Fire-and-forget API call; WorkManager will consider this a success immediately
        api.updateMedicineStatus(logId, status).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Successfully updated status for logId=" + logId);
                } else {
                    Log.e(TAG, "Failed to update status for logId=" + logId + " | Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "API call failed for logId=" + logId, t);
            }
        });

        return Result.success();
    }
}
