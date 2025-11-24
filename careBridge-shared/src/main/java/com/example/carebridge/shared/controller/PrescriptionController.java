package com.example.carebridge.shared.controller;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.shared.model.Prescription;
import com.example.carebridge.shared.utils.ApiConstants;
import com.example.carebridge.shared.utils.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PrescriptionController {

    private static final String TAG = "PrescriptionController";
    private final Context context;
    private final SharedPrefManager sharedPrefManager;
    private final OkHttpClient client;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public PrescriptionController(Context context) {
        this.context = context;
        this.sharedPrefManager = new SharedPrefManager(context);
        this.client = new OkHttpClient();
    }

    public interface PrescriptionCallback {
        void onSuccess(List<Prescription> prescriptions);
        void onFailure(String errorMessage);
    }

    /** Fetch prescriptions for the saved case ID */
    public void fetchPrescriptions(PrescriptionCallback callback) {
        String caseId = sharedPrefManager.getCaseId();
        fetchPrescriptionsInternal(caseId, callback);
    }

    /** Fetch prescriptions for a specific case ID */
    public void fetchPrescriptionsWithCaseId(String caseId, PrescriptionCallback callback) {
        fetchPrescriptionsInternal(caseId, callback);
    }

    /** Internal method to reduce code duplication */
    private void fetchPrescriptionsInternal(String caseId, PrescriptionCallback callback) {
        if (caseId == null || caseId.isEmpty()) {
            Log.e(TAG, "Invalid case ID");
            callback.onFailure("Invalid case ID");
            return;
        }

        String url = ApiConstants.getPrescriptionByCaseIdUrl(caseId);
        Log.d(TAG, "Fetching prescriptions for Case ID: " + caseId);
        Log.d(TAG, "Request URL: " + url);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed: " + e.getMessage(), e);
                mainHandler.post(() -> callback.onFailure("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "HTTP Status Code: " + response.code());
                Log.d(TAG, "Raw API Response: " + res);

                if (!response.isSuccessful()) {
                    mainHandler.post(() -> callback.onFailure("Server error: " + response.message()));
                    return;
                }

                if (res.isEmpty()) {
                    mainHandler.post(() -> callback.onFailure("Empty response from API"));
                    return;
                }

                mainHandler.post(() -> parsePrescriptions(res, callback));
            }
        });
    }

    /** Parse JSON response and notify callback */
    private void parsePrescriptions(String json, PrescriptionCallback callback) {
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Prescription>>() {}.getType();
            List<Prescription> prescriptions = gson.fromJson(json, type);

            if (prescriptions == null || prescriptions.isEmpty()) {
                Log.w(TAG, "No prescriptions found");
                callback.onFailure("No prescriptions found for this case ID");
            } else {
                Log.d(TAG, "Parsed " + prescriptions.size() + " prescriptions successfully");
                callback.onSuccess(prescriptions);
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "JSON parsing error", e);
            callback.onFailure("Response format error (JSON)");
        } catch (Exception e) {
            Log.e(TAG, "Unexpected parsing error", e);
            callback.onFailure("Unexpected error while parsing response");
        }
    }
}
