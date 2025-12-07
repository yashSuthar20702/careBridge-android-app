package com.example.carebridge.shared.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.shared.model.MedicineLog;
import com.example.carebridge.shared.model.MedicineLogResponse;
import com.example.carebridge.shared.utils.ApiConstants;
import com.example.carebridge.shared.utils.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MedicineLogController {

    private static final String TAG = "MedicineLogController";
    private final Context context;
    private final SharedPrefManager sharedPrefManager;
    private final OkHttpClient client;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public MedicineLogController(Context context) {
        this.context = context;
        this.sharedPrefManager = new SharedPrefManager(context);
        this.client = new OkHttpClient();
    }

    public interface MedicineLogCallback {
        void onSuccess(List<MedicineLog> logs);
        void onFailure(String errorMessage);
    }

    public void fetchLogs(String caseId, MedicineLogCallback callback) {
        if (caseId == null || caseId.isEmpty()) {
            callback.onFailure("Invalid case ID");
            return;
        }

        String url = ApiConstants.getMedicineLogByCaseIdUrl(caseId);
        Log.d(TAG, "Request URL: " + url);

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onFailure("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "Server Response: " + res);

                if (!response.isSuccessful()) {
                    mainHandler.post(() -> callback.onFailure("Server error: " + response.message()));
                    return;
                }

                mainHandler.post(() -> parseResponse(res, callback));
            }
        });
    }

    private void parseResponse(String json, MedicineLogCallback callback) {
        try {
            Gson gson = new Gson();
            MedicineLogResponse logResponse = gson.fromJson(json, MedicineLogResponse.class);

            if (logResponse != null && logResponse.isSuccess() && logResponse.getLogs() != null) {
                callback.onSuccess(logResponse.getLogs());
            } else {
                callback.onFailure("No logs found");
            }

        } catch (JsonSyntaxException e) {
            Log.e(TAG, "JSON parsing error: ", e);
            callback.onFailure("JSON format error: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected parsing error: ", e);
            callback.onFailure("Unexpected parsing error: " + e.getMessage());
        }
    }
}
