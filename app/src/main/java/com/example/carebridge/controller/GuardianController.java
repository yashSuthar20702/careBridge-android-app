package com.example.carebridge.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.model.GuardianInfo;
import com.example.carebridge.model.PatientInfo;
import com.example.carebridge.utils.SharedPrefManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GuardianController {

    private static final String TAG = "GuardianController";
    private final Context context;
    private final OkHttpClient client;
    private final SharedPrefManager sharedPrefManager;

    private static final String BASE_URL = "http://10.0.2.2/CareBridge/careBridge-web-app/careBridge-website/endpoints/guardians/";

    public GuardianController(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
        this.sharedPrefManager = new SharedPrefManager(context);
        Log.d(TAG, "[INIT] GuardianController initialized");
    }

    public interface GuardianCallback {
        void onSuccess(GuardianInfo guardianInfo);
        void onFailure(String message);
    }

    public interface PatientsCallback {
        void onSuccess(List<PatientInfo> patients);
        void onFailure(String message);
    }

    // Fetch guardian by ID
    public void getGuardianById(String guardianId, GuardianCallback callback) {
        if (guardianId == null || guardianId.isEmpty()) {
            callback.onFailure("Invalid guardian ID");
            return;
        }

        String url = BASE_URL + "getOne.php?guardian_id=" + guardianId;
        Log.d(TAG, "[API REQUEST] GET " + url);

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "[NETWORK ERROR] " + e.getMessage());
                mainHandler.post(() -> callback.onFailure("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resString = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "[API RESPONSE] " + resString);

                mainHandler.post(() -> {
                    try {
                        GuardianInfo guardianInfo = new Gson().fromJson(resString, GuardianInfo.class);
                        if (guardianInfo != null && guardianInfo.getGuardian_id() != null) {
                            Log.d(TAG, "[DATA PARSED DIRECT] " + guardianInfo.toString());
                            callback.onSuccess(guardianInfo);
                        } else {
                            callback.onFailure("No guardian data available");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "[JSON ERROR] Invalid server response", e);
                        callback.onFailure("Invalid server response");
                    }
                });
            }
        });
    }

    // Fetch currently saved guardian
    public void getCurrentGuardian(GuardianCallback callback) {
        String savedRefId = sharedPrefManager.getReferenceId();
        Log.d(TAG, "[CURRENT GUARDIAN] Saved reference ID: " + savedRefId);
        if (savedRefId == null || savedRefId.isEmpty()) {
            callback.onFailure("No reference ID found");
            return;
        }
        getGuardianById(savedRefId, callback);
    }

    // Fetch patients assigned to guardian
    public void getAssignedPatients(String guardianId, PatientsCallback callback) {
        if (guardianId == null || guardianId.isEmpty()) {
            callback.onFailure("Invalid guardian ID for patients");
            return;
        }

        String url = BASE_URL.replace("guardians/", "patientguardianassignment/") + "getByGuardian.php?guardian_id=" + guardianId;
        Log.d(TAG, "[API REQUEST] GET " + url);

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onFailure("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resString = response.body() != null ? response.body().string() : "";
                mainHandler.post(() -> {
                    try {
                        JSONArray dataArray = new JSONArray(resString);
                        List<PatientInfo> patients = new ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
                            patients.add(new Gson().fromJson(obj.toString(), PatientInfo.class));
                        }
                        callback.onSuccess(patients);
                    } catch (JSONException e) {
                        callback.onFailure("Invalid server response");
                    }
                });
            }
        });
    }
}
