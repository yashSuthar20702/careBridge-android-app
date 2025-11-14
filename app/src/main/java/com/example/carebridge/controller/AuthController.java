package com.example.carebridge.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.model.PatientInfo;
import com.example.carebridge.model.User;
import com.example.carebridge.utils.ApiConstants;
import com.example.carebridge.utils.SharedPrefManager;
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

    // ============================
    // LOGIN
    // ============================
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

                            PatientInfo pi =
                                    new Gson().fromJson(linked.toString(), PatientInfo.class);

                            user.setPatientInfo(pi);

                            caseIdToStore = pi.getCase_id() != null ?
                                    pi.getCase_id() :
                                    user.getReferenceId();

                        } else {
                            Log.d(TAG, "No linked data found.");
                            user.setPatientInfo(new PatientInfo());
                            caseIdToStore = user.getReferenceId();
                        }

                        sharedPrefManager.saveUserSession(user);
                        sharedPrefManager.saveCaseId(caseIdToStore);
                        sharedPrefManager.saveReferenceId(user.getReferenceId());

                        Log.d(TAG, "User session saved successfully.");
                        saveFcmTokenToServer(user.getId());

                        callback.onSuccess(user);

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing login response: " + e.getMessage());
                        callback.onFailure("Invalid response");
                    }
                });
            }
        });
    }

    // ============================
    // SAVE FCM TOKEN ON LOGIN
    // ============================
    public void saveFcmTokenToServer(int userId) {

        Log.d(TAG, "Fetching FCM token to store in server...");

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {

            Log.d(TAG, "FCM Token received: " + token);

            try {
                JSONObject json = new JSONObject();
                json.put("user_id", userId);
                json.put("fcm_token", token);

                Log.d(TAG, "JSON for save token: " + json.toString());
                Log.d(TAG, "Save Token API: " + ApiConstants.getUpdateFcmTokenUrl());

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
                    public void onFailure(Call c, IOException e) {
                        Log.e(TAG, "Failed to save FCM token: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call c, Response r) {
                        try {
                            String resp = r.body().string();
                            Log.d(TAG, "Save FCM token API response: " + resp);
                        } catch (Exception ignored) {}
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error building JSON for token save: " + e.getMessage());
            }
        });
    }

    // ============================
    // LOGOUT
    // ============================
    public void logout() {

        Log.d(TAG, "Logout started...");
        User user = sharedPrefManager.getCurrentUser();

        if (user == null) {
            Log.e(TAG, "No user found in session.");
            return;
        }

        int userId = user.getId();
        Log.d(TAG, "Logging out userId=" + userId);

        deleteFcmTokenFromServer(userId);

        FirebaseMessaging.getInstance().deleteToken()
                .addOnSuccessListener(a -> Log.d(TAG, "FCM token deleted locally"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete token locally: " + e.getMessage()));

        sharedPrefManager.clearSession();
        sharedPrefManager.clearCaseId();
        sharedPrefManager.clearReferenceId();
        Log.d(TAG, "Local SharedPref session cleared.");
    }

    // ============================
    // DELETE TOKEN API
    // ============================
    private void deleteFcmTokenFromServer(int userId) {

        Log.d(TAG, "Deleting FCM token from server for userId=" + userId);

        JSONObject json = new JSONObject();
        try {
            json.put("user_id", userId);
            Log.d(TAG, "Delete token JSON: " + json.toString());

        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON for token delete: " + e.getMessage());
        }

        Log.d(TAG, "Delete Token API URL: " + ApiConstants.getDeleteFcmTokenUrl());

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(ApiConstants.getDeleteFcmTokenUrl())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Delete token API FAILED: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String resp = response.body().string();
                    Log.d(TAG, "Delete token API response: " + resp);
                } catch (Exception e) {
                    Log.e(TAG, "Error reading delete response: " + e.getMessage());
                }
            }
        });
    }

    public boolean isLoggedIn() {
        return sharedPrefManager.isLoggedIn();
    }

    public User getCurrentUser() {
        return sharedPrefManager.getCurrentUser();
    }
}
