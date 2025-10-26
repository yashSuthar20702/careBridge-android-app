package com.example.carebridge.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.model.PatientInfo;
import com.example.carebridge.utils.SharedPrefManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PatientController {

    private static final String TAG = "PatientController";
    private final Context context;
    private final OkHttpClient client;
    private final SharedPrefManager sharedPrefManager;

    // Base URL for Android emulator (10.0.2.2)
    private static final String BASE_URL = "http://10.0.2.2/CareBridge/careBridge-web-app/careBridge-website/endpoints/patients/";

    public PatientController(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
        this.sharedPrefManager = new SharedPrefManager(context);
        Log.d(TAG, "[INIT] PatientController initialized");
    }

    public interface PatientCallback {
        void onSuccess(PatientInfo patientInfo);
        void onFailure(String message);
    }

    /**
     * Fetch a single patient by any given case ID
     */
    public void getPatientByCaseId(String caseId, PatientCallback callback) {
        if (caseId == null || caseId.isEmpty()) {
            Log.w(TAG, "[API CALL] Invalid case ID provided: " + caseId);
            callback.onFailure("Invalid case ID");
            return;
        }

        Log.d(TAG, "[API CALL] Using case ID: " + caseId); // <-- Print case ID here

        String url = BASE_URL + "getOne.php?case_id=" + caseId;
        Log.d(TAG, "[API URL] " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

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
                Log.d(TAG, "[RESPONSE] " + resString);

                mainHandler.post(() -> {
                    try {
                        JSONObject resJson = new JSONObject(resString);
                        boolean success = resJson.optBoolean("success", false);

                        if (success) {
                            JSONObject dataJson = resJson.getJSONObject("data");
                            PatientInfo patientInfo = new Gson().fromJson(dataJson.toString(), PatientInfo.class);
                            Log.d(TAG, "[PATIENT INFO] " + patientInfo);
                            callback.onSuccess(patientInfo);
                        } else {
                            String message = resJson.optString("message", "Failed to fetch patient data");
                            Log.w(TAG, "[API FAILURE] " + message);
                            callback.onFailure(message);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "[JSON ERROR] Parsing failed", e);
                        callback.onFailure("Invalid server response");
                    }
                });
            }
        });
    }

    /**
     * Fetch the current patient using the saved case ID from SharedPreferences
     */
    public void getCurrentPatient(PatientCallback callback) {
        String savedCaseId = sharedPrefManager.getCaseId();
        Log.d(TAG, "[SESSION] Retrieved case ID from SharedPreferences: " + savedCaseId);

        if (savedCaseId == null || savedCaseId.isEmpty()) {
            Log.w(TAG, "[SESSION] No case ID found in SharedPreferences");
            callback.onFailure("No case ID found in SharedPreferences");
            return;
        }

        Log.d(TAG, "[SESSION] Calling API with case ID: " + savedCaseId); // <-- Extra log for case ID

        getPatientByCaseId(savedCaseId, callback);
    }
}
