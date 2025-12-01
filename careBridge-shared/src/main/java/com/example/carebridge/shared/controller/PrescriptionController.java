package com.example.carebridge.shared.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.shared.model.Prescription;
import com.example.carebridge.shared.model.PrescriptionResponse;
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

    public void fetchPrescriptions(PrescriptionCallback callback) {
        String caseId = sharedPrefManager.getCaseId();
        fetchPrescriptionsInternal(caseId, callback);
    }

    public void fetchPrescriptionsWithCaseId(String caseId, PrescriptionCallback callback) {
        fetchPrescriptionsInternal(caseId, callback);
    }

    private void fetchPrescriptionsInternal(String caseId, PrescriptionCallback callback) {
        if (caseId == null || caseId.isEmpty()) {
            callback.onFailure("Invalid case ID");
            return;
        }

        String url = ApiConstants.getPrescriptionByCaseIdUrl(caseId);
        Log.d(TAG, "Request URL: " + url);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() ->
                        callback.onFailure("Network error: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body() != null ? response.body().string() : "";

                if (!response.isSuccessful()) {
                    mainHandler.post(() ->
                            callback.onFailure("Server error: " + response.message())
                    );
                    return;
                }

                mainHandler.post(() -> parseResponse(res, callback));
            }
        });
    }

    private void parseResponse(String json, PrescriptionCallback callback) {
        try {
            Gson gson = new Gson();
            PrescriptionResponse prescriptionResponse =
                    gson.fromJson(json, PrescriptionResponse.class);

            if (prescriptionResponse == null || !prescriptionResponse.isStatus()) {
                callback.onFailure("No prescriptions found");
                return;
            }

            List<Prescription> prescriptions = prescriptionResponse.getPrescriptions();
            callback.onSuccess(prescriptions);

        } catch (JsonSyntaxException e) {
            callback.onFailure("JSON format error");
        } catch (Exception e) {
            callback.onFailure("Unexpected parsing error");
        }
    }
}
