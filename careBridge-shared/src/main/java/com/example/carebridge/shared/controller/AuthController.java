package com.example.carebridge.shared.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.shared.model.User;
import com.example.carebridge.shared.model.PatientInfo;
import com.example.carebridge.shared.utils.ApiConstants;
import com.example.carebridge.shared.utils.SharedPrefManager;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class AuthController {
    private static final String TAG = "AuthController";
    private final SharedPrefManager sharedPrefManager;
    private final OkHttpClient client;

    public AuthController(Context context) {
        this.sharedPrefManager = new SharedPrefManager(context);
        this.client = new OkHttpClient();
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onFailure(String message);
    }

    public void login(String username, String password, LoginCallback callback) {
        Log.d(TAG, "Login request started...");

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
            Log.d(TAG, "Login JSON body: " + json.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error creating login JSON: " + e.getMessage());
            callback.onFailure("Error building JSON");
            return;
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(ApiConstants.getLoginUrl())
                .post(body)
                .build();

        Log.d(TAG, "Login API URL: " + ApiConstants.getLoginUrl());

        client.newCall(request).enqueue(new Callback() {
            final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Login API FAILED: " + e.getMessage());
                handler.post(() -> callback.onFailure("Network error"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                Log.d(TAG, "Login API Response: " + responseStr);

                handler.post(() -> {
                    try {
                        JSONObject res = new JSONObject(responseStr);

                        if (!"success".equalsIgnoreCase(res.optString("status"))) {
                            Log.e(TAG, "Login failed: " + res.optString("message"));
                            callback.onFailure(res.optString("message", "Login failed"));
                            return;
                        }

                        JSONObject userJson = res.getJSONObject("user");
                        Log.d(TAG, "User JSON received: " + userJson.toString());

                        User user = new User();
                        user.setId(userJson.getInt("user_id"));
                        user.setUsername(userJson.getString("username"));
                        user.setRole(userJson.getString("role"));
                        user.setReferenceId(userJson.optString("reference_id"));
                        user.setCreatedAt(userJson.optString("created_at"));

                        JSONObject linked = userJson.optJSONObject("linked_data");
                        String caseIdToStore;

                        if (linked != null && linked.length() > 0) {
                            Log.d(TAG, "Linked data found: " + linked.toString());
                            PatientInfo pi = new Gson().fromJson(linked.toString(), PatientInfo.class);
                            user.setPatientInfo(pi);
                            caseIdToStore = pi.getCaseId() != null ? pi.getCaseId() : user.getReferenceId();
                        } else {
                            Log.d(TAG, "No linked data found.");
                            user.setPatientInfo(new PatientInfo());
                            caseIdToStore = user.getReferenceId();
                        }

                        sharedPrefManager.saveUserSession(user);
                        sharedPrefManager.saveCaseId(caseIdToStore);
                        sharedPrefManager.saveReferenceId(user.getReferenceId());

                        Log.d(TAG, "User session saved successfully.");
                        callback.onSuccess(user);

                        // --- Send FCM token to server ---
                        sendFcmTokenToServer(user);

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing login response: " + e.getMessage());
                        callback.onFailure("Invalid response");
                    }
                });
            }
        });
    }

    private void sendFcmTokenToServer(User user) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String fcmToken = task.getResult();
                    Log.d(TAG, "FCM Token: " + fcmToken);

                    JSONObject json = new JSONObject();
                    try {
                        json.put("user_id", user.getId());
                        json.put("fcm_token", fcmToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }

                    RequestBody body = RequestBody.create(
                            json.toString(),
                            MediaType.get("application/json; charset=utf-8")
                    );

                    Request request = new Request.Builder()
                            .url(ApiConstants.getUpdateFcmTokenUrl())
                            .post(body)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "Failed to update FCM token: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String res = response.body().string();
                            Log.d(TAG, "FCM token update response: " + res);
                        }
                    });
                });
    }

    public boolean isLoggedIn() {
        return sharedPrefManager.isLoggedIn();
    }

    public User getCurrentUser() {
        return sharedPrefManager.getCurrentUser();
    }

    public void logout() {
        Log.d(TAG, "Logout started...");
        User user = sharedPrefManager.getCurrentUser();

        if (user == null) {
            Log.e(TAG, "No user found in session.");
            return;
        }

        sharedPrefManager.clearSession();
        sharedPrefManager.clearCaseId();
        sharedPrefManager.clearReferenceId();
        Log.d(TAG, "Local SharedPref session cleared.");
    }
}
