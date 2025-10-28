package com.example.carebridge.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.model.PatientGuardianInfo;
import com.example.carebridge.utils.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PatientGuardianInfoController {

    private static final String TAG = "PatientGuardianInfoController";
    private final Context context;
    private final OkHttpClient client;
    private final SharedPrefManager sharedPrefManager;

    // Emulator localhost base URL
    private static final String BASE_URL =
            "http://10.0.2.2/CareBridge/careBridge-web-app/careBridge-website/endpoints/patientguardianassignment/";

    public PatientGuardianInfoController(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
        this.sharedPrefManager = new SharedPrefManager(context);
    }

    public interface PatientGuardianCallback {
        void onSuccess(List<PatientGuardianInfo> guardianList);
        void onFailure(String message);
    }

    public void getGuardianByCaseId(String caseId, PatientGuardianCallback callback) {
        if (caseId == null || caseId.isEmpty()) {
            callback.onFailure("Invalid case ID");
            return;
        }

        String url = BASE_URL + "getByPatient.php?case_id=" + caseId;
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
                            JSONArray dataArray = resJson.getJSONArray("data");
                            Type listType = new TypeToken<List<PatientGuardianInfo>>(){}.getType();
                            List<PatientGuardianInfo> guardianList = new Gson().fromJson(dataArray.toString(), listType);
                            callback.onSuccess(guardianList);
                        } else {
                            String message = resJson.optString("message", "Failed to fetch guardian info");
                            callback.onFailure(message);
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "[JSON ERROR]", e);
                        callback.onFailure("Invalid server response");
                    }
                });
            }
        });
    }

    public void getCurrentGuardian(PatientGuardianCallback callback) {
        String savedCaseId = sharedPrefManager.getCaseId();
        Log.d(TAG, "[SESSION] Case ID: " + savedCaseId);

        if (savedCaseId == null || savedCaseId.isEmpty()) {
            callback.onFailure("No case ID found");
            return;
        }

        getGuardianByCaseId(savedCaseId, callback);
    }
}
