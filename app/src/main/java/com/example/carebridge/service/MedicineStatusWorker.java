package com.example.carebridge.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicineStatusWorker extends Worker {

    public MedicineStatusWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        int logId = getInputData().getInt("log_id", -1);
        String takenStatus = getInputData().getString("taken_status");

        if (logId == -1 || takenStatus == null) {
            return Result.failure();
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);

        Log.d("WORK_MANAGER", "Retrying Update → Log #" + logId + " | " + takenStatus);

        api.updateMedicineStatus(String.valueOf(logId), takenStatus).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("WORK_MANAGER", "Sync Success: " + response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("WORK_MANAGER", "Sync Failed Again: " + t.getMessage());
            }
        });

        return Result.success();
    }
}
