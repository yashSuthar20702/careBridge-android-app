package com.example.carebridge.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.model.Prescription;
import com.example.carebridge.utils.ApiConstants;
import com.example.carebridge.utils.SharedPrefManager;
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

    public PrescriptionController(Context context) {
        this.context = context;
        this.sharedPrefManager = new SharedPrefManager(context);
        this.client = new OkHttpClient();
    }

    public interface PrescriptionCallback {
        void onSuccess(List<Prescription> prescriptions);
        void onFailure(String errorMessage);
    }

    public void fetchPrescriptions(PrescriptionCallback callback) {
        String caseId = sharedPrefManager.getCaseId();

        if (caseId == null || caseId.isEmpty()) {
            Log.e(TAG, "Case ID is null or empty. Cannot fetch prescriptions.");
            callback.onFailure("Invalid case ID");
            return;
        }

        // Use ApiConstants for dynamic URL
        String url = ApiConstants.getPrescriptionByCaseIdUrl(caseId);
        Log.d(TAG, "Fetching prescriptions for Case ID: " + caseId);
        Log.d(TAG, "Request URL: " + url);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed: " + e.getMessage(), e);
                mainHandler.post(() -> callback.onFailure("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "HTTP Status Code: " + response.code());

                if (!response.isSuccessful()) {
                    Log.e(TAG, "Unsuccessful response: " + response.message());
                    mainHandler.post(() -> callback.onFailure("Server error: " + response.message()));
                    return;
                }

                String res = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "Raw API Response: " + res);

                if (res.isEmpty()) {
                    Log.e(TAG, "Empty response from API");
                    mainHandler.post(() -> callback.onFailure("Empty response from API"));
                    return;
                }

                mainHandler.post(() -> {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Prescription>>() {}.getType();
                        List<Prescription> prescriptions = gson.fromJson(res, type);

                        if (prescriptions == null || prescriptions.isEmpty()) {
                            Log.w(TAG, "No prescriptions found or parsing returned empty list.");
                            callback.onFailure("No prescriptions found for this case ID");
                        } else {
                            Log.d(TAG, "Parsed " + prescriptions.size() + " prescriptions successfully.");
                            for (Prescription p : prescriptions) {
                                Log.d(TAG, "Prescription ID: " + p.getPrescription_id() +
                                        ", Doctor: " + p.getDoctor_name() +
                                        ", Created At: " + p.getCreated_at());
                            }
                            callback.onSuccess(prescriptions);
                        }
                    } catch (JsonSyntaxException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        callback.onFailure("Response format error (JSON)");
                    } catch (Exception e) {
                        Log.e(TAG, "Unexpected parsing error", e);
                        callback.onFailure("Unexpected error while parsing response");
                    }
                });
            }
        });
    }
}
