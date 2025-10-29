package com.example.carebridge.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.model.AssignedPatientInfo;
import com.example.carebridge.utils.ApiConstants;
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

public class AssignedPatientController {

    private static final String TAG = "AssignedPatientController";
    private final OkHttpClient client;

    public AssignedPatientController() {
        this.client = new OkHttpClient();
    }

    public interface AssignedPatientsCallback {
        void onSuccess(List<AssignedPatientInfo> patients);
        void onFailure(String message);
    }

    public void getAssignedPatients(String guardianId, AssignedPatientsCallback callback) {
        if (guardianId == null || guardianId.isEmpty()) {
            callback.onFailure("Invalid guardian ID for patients");
            return;
        }

        String url = ApiConstants.getAssignedPatientsUrl(guardianId);
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
                Log.d(TAG, "[RESPONSE] " + resString);

                mainHandler.post(() -> {
                    try {
                        JSONArray dataArray = new JSONArray(resString);
                        List<AssignedPatientInfo> patients = new ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
                            AssignedPatientInfo patient = new Gson().fromJson(obj.toString(), AssignedPatientInfo.class);
                            patients.add(patient);
                        }
                        callback.onSuccess(patients);
                    } catch (JSONException e) {
                        // Handle non-array response, e.g. {"message":"No patients are currently assigned ..."}
                        try {
                            JSONObject errorObj = new JSONObject(resString);
                            String message = errorObj.optString("message", "Invalid server response");
                            callback.onFailure(message);
                        } catch (Exception ex) {
                            callback.onFailure("Invalid server response");
                        }
                    }
                });
            }
        });
    }
}
