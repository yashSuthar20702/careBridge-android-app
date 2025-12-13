package com.example.carebridge.wear.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.carebridge.wear.utils.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * WearSyncWorker

 * Background worker used to retry medicine status updates
 * when immediate network call fails.
 */
public class WearSyncWorker extends Worker {

    private static final String TAG = "WearSyncWorker";

    public WearSyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        String logId =
                getInputData().getString(Constants.KEY_LOG_ID);

        String status =
                getInputData().getString(Constants.KEY_TAKEN_STATUS);

        if (logId == null || status == null) {
            Log.e(TAG, "Missing required input data");
            return Result.failure();
        }

        Log.d(TAG, "Retrying sync → logId=" + logId + " status=" + status);

        WearApiService api =
                WearApiClient.getClient().create(WearApiService.class);

        try {
            Call<JsonObject> call =
                    api.updateMedicineStatus(logId, status);

            Response<JsonObject> response =
                    call.execute();

            if (response.isSuccessful()) {
                Log.d(TAG, "Status synced successfully");
                return Result.success();
            } else {
                Log.e(
                        TAG,
                        "Server error: " + response.code()
                );
                return Result.retry();
            }

        } catch (Exception e) {
            Log.e(TAG, "Network error, retrying", e);
            return Result.retry();
        }
    }
}