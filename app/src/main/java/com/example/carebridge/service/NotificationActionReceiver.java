package com.example.carebridge.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.carebridge.network.ApiClient;
import com.example.carebridge.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String logId = intent.getStringExtra("log_id");

        if (logId == null) return;

        String action = intent.getAction();
        String status = "";

        if ("MED_TAKEN".equals(action)) status = "Taken";
        if ("MED_NOT_TAKEN".equals(action)) status = "Not Taken";

        Log.d("ACTION_RECEIVER", "Updating log #" + logId + " → " + status);

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.updateMedicineStatus(logId, status).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("ACTION_RECEIVER", "Updated successfully: " + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("ACTION_RECEIVER", "Error: " + t.getMessage());
            }
        });
    }
}
